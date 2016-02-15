package me.ex0ns.stockbot.drive

import java.net.URL

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.model.File
import com.google.api.services.drive.{Drive, DriveScopes}
import com.google.gdata.client.spreadsheet.SpreadsheetService
import com.google.gdata.data.spreadsheet._
import com.typesafe.scalalogging.Logger
import me.ex0ns.stockbot.Settings
import me.ex0ns.stockbot.drive.DriveService.DriveMessage
import me.ex0ns.stockbot.utils.OptionsUtils._
import me.ex0ns.stockbot.utils.Strings
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by ex0ns on 12/22/15.
  */

object DriveService {
  case class DriveMessage(error: Boolean, message: String = "")
}

class DriveService(settings: Settings) {

  /**
    * Improved logger object to return a DriveMessage object
    * @param logger
    *               The logger to pimp
    */
  implicit class LoggerDrive(logger: Logger) {
    def debugDrive(text: String) : DriveMessage = {
      logger.debug(text)
      new DriveMessage(false, text)
    }

    def errorDrive(text: String) : DriveMessage = {
      logger.error(text)
      new DriveMessage(true, text)
    }
  }

  private val SCOPE_SPREADSHEET = "https://spreadsheets.google.com/feeds"
  private val SERVICE_NAME = classOf[DriveService].getSimpleName
  private val logger = Logger(LoggerFactory.getLogger(classOf[DriveService]))

  private val emailAddress = settings.email
  private val jsonFactory = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private val credential = new GoogleCredential.Builder()
    .setTransport(httpTransport)
    .setJsonFactory(jsonFactory)
    .setServiceAccountId(emailAddress)
    .setServiceAccountPrivateKeyFromP12File(new java.io.File(settings.keyPath))
    .setServiceAccountScopes(SCOPE_SPREADSHEET :: DriveScopes.all().toList)
    .build()

  private val service = new Drive.Builder(httpTransport, jsonFactory, null)
    .setHttpRequestInitializer(credential)
    .setApplicationName(SERVICE_NAME)
    .build()

  def apply() = service

  def files : List[File] = service.files().list().execute().getItems.toList

  private val file : File =  {
    val files = this.files.filter(f => f.getTitle == settings.filename)
    files match {
      case f :: fs => f
      case _ => throw  new NoSuchElementException(Strings.NO_SUCH_FILE(settings.filename))
    }
  }

  private val spreadsheetsService = new SpreadsheetService(SERVICE_NAME)
  spreadsheetsService.setOAuth2Credentials(credential)
  logger.debug("File found, with id: " + file.getId)

  private val sheetURL = new URL("https://spreadsheets.google.com/feeds/worksheets/" + file.getId + "/private/full")
  private val worksheets : List[WorksheetEntry] = spreadsheetsService.getFeed(sheetURL,  classOf[WorksheetFeed]).getEntries.toList

  private val worksheet : Option[WorksheetEntry] = worksheets match {
    case sheet :: sheets => sheet
    case _ =>
      logger.debug(Strings.WRONG_WORKSHEET_NAME)
      None
  }

  /**
    * Find cells in the spreadsheet with the given filter
    * @param filter
    *         The filter to select the cells
    * @return
    *         List of cells that matches the filter
    */
  private def filterCells(filter : CellEntry => Boolean) : List[CellEntry] = {
    worksheet match {
      case Some(sheet) =>
        val cellFeedUrl = sheet.getCellFeedUrl
        val cellFeed : List[CellEntry] = spreadsheetsService.getFeed(cellFeedUrl, classOf[CellFeed]).getEntries.toList
        cellFeed.filter(cell => filter(cell))
      case None => List()
    }
  }

  /**
    * Find a cell entry based on the given filter
    * @param filter
    *         The filter to select the cells
    * @return
    *         The first cell matching the given filter
    */
  private def filterCell(filter: CellEntry => Boolean) : Option[CellEntry] = {
    filterCells(filter) match {
      case cell :: cells => cell
      case _ => None
    }
  }

  private def findCellByText(text: String) : Option[CellEntry] =
    filterCell(cell => cell.getCell.getInputValue.toLowerCase == text.toLowerCase)

  private def getCell(row: Int, col: Int) : Option[CellEntry] =
    filterCell(cell => cell.getCell.getRow == row && cell.getCell.getCol == col)

  private def getNextCol(cell: Cell) : Option[CellEntry] = getCell(cell.getRow, cell.getCol + 1)
  private def getNextRow(cell: Cell) : Option[CellEntry] = getCell(cell.getRow + 1, cell.getCol)

  private def changeStock(item: String, value: Int) : DriveMessage = {
    findCellByText(item) match {
      case Some(cell) =>
        getNextCol(cell.getCell) match {
          case Some(nextCell) =>
            val oldStock = nextCell.getCell.getValue.toInt
            val newStock = oldStock + value
            if(newStock < 0)  {
              logger.errorDrive(Strings.NEGATIVE_STOCK)
            } else {
              nextCell.changeInputValueLocal(newStock.toString)
              nextCell.update
              new DriveMessage(false, newStock.toString)
            }
          case None =>
            logger.errorDrive(Strings.CELL_NOT_FOUND(cell.getCell.getRow, cell.getCell.getCol))
        }
      case None =>
        logger.errorDrive(Strings.NOT_IN_STOCK)
    }
  }

  /**
    * Remove stock of an item
    * @param item
    *             The item to change the stock of
    * @param value
    *             The number of items to remove
    * @return
    */
  def removeStock(item: String, value: Int) : DriveMessage = {
    if(value < 0) throw new IllegalArgumentException("Could not remove negative value from stock, please use addStock function")
    changeStock(item, -value)
  }

  /**
    * Add stock of the given item
    * @param item
    *             The item to change the stock of
    * @param value
    *              The number of items to add
    * @return
    */
  def addStock(item: String, value: Int) : DriveMessage = {
    if (value < 0) throw new IllegalArgumentException("Could not add negative value to stock, please use removeStock function")
    changeStock(item, value)
  }

  private def getAllItemsName: List[String] = {
    filterCells(cell => cell.getCell.getRow >= settings.startRow && settings.cols.contains(cell.getCell.getCol))
      .map(cellEntry => cellEntry.getCell.getInputValue)
  }

  private def getAllItemsCount: List[Int] = {
    filterCells(cell => cell.getCell.getRow >= settings.startRow && settings.cols.map(c => c + 1).contains(cell.getCell.getCol))
      .map(cellEntry => cellEntry.getCell.getInputValue.toInt)
  }

  private def exists(item: String) : Boolean = getAllItemsName.map(_.toLowerCase).contains(item)

  private def rowsInCol(sheet: WorksheetEntry, col: Int) : Int = {
    val url = new URL(sheet.getCellFeedUrl.toString + "?min-col=" + col.toString + "&max-col=" + col.toString)
    val cellFeed = spreadsheetsService.getFeed(url, classOf[CellFeed])
    cellFeed.getTotalResults
  }

  private def findNewItemCell(defaultValue: String = "") : Option[CellEntry] = {
    worksheet match {
      case Some(sheet) =>
        val position = settings.cols.map(col => {
          (col, rowsInCol(sheet, col))
        }).minBy(_._2)
        new CellEntry(position._2 + 1, position._1, defaultValue)
      case None => None
    }
  }

  /**
    * Add a new item in the spreadsheet (if not already present)
    * @param item
    *             The name of the item to add
    * @param initialValue
    *             THe initial stock of the item
    */
  def addItem(item: String, initialValue: Int) : DriveMessage = {
    if(exists(item)) {
      logger.debug(Strings.ALREADY_EXISTS)
      addStock(item, initialValue)
    } else {
      worksheet match {
        case Some(sheet) =>
          val url : URL = sheet.getCellFeedUrl
          val cellFeed = spreadsheetsService.getFeed(url, classOf[CellFeed])
          findNewItemCell(item) match {
            case Some(cell) =>
              cellFeed.insert(cell)
              val valueCell = new CellEntry(cell.getCell.getRow, cell.getCell.getCol + 1, initialValue.toString)
              cellFeed.insert(valueCell)
              new DriveMessage(false, initialValue.toString)
            case None =>
              logger.errorDrive(Strings.NO_CELL_FOUND)
          }
        case None =>
          logger.errorDrive(Strings.NO_WORKSHEET)
      }
    }
  }

  /**
    * Return all the items described in the spreadsheet
    * @return
    *         List of array described in the spreadsheet
    */
  def getAllItems : List[Item] = {
    getAllItemsName.zip(getAllItemsCount).map {
      case (name, count) => new Item(name, count)
    }
  }

}
