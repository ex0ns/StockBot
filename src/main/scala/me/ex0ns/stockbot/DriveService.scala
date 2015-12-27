package me.ex0ns.stockbot

import java.net.URL
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.model.File
import com.google.api.services.drive.{Drive, DriveScopes}
import com.google.gdata.client.spreadsheet.SpreadsheetService
import com.google.gdata.data.spreadsheet._

import scala.collection.JavaConversions._

/**
  * Created by ex0ns on 12/22/15.
  */
class DriveService(settings: Settings) {

  private val SCOPE_SPREADSHEET = "https://spreadsheets.google.com/feeds"
  private val SERVICE_NAME = classOf[DriveService].getSimpleName
  private val logger = Logger(LoggerFactory.getLogger(SERVICE_NAME))

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
    .setHttpRequestInitializer(credential).build()

  def apply() = service

  def files : List[File] = service.files().list().execute().getItems.toList

  private val file : File =  {
    val files = this.files.filter(f => f.getTitle == settings.filename)
    files match {
      case f :: fs => f
      case _ => throw  new NoSuchElementException("Could not locate: '" + settings.filename + "' in Drive")
    }
  }

  val spreadsheetsService = new SpreadsheetService(SERVICE_NAME)
  spreadsheetsService.setOAuth2Credentials(credential)
  logger.debug("File found, with id: " + file.getId)

  val sheetURL = new URL("https://spreadsheets.google.com/feeds/worksheets/" + file.getId + "/private/full")
  val worksheets : List[WorksheetEntry] = spreadsheetsService.getFeed(sheetURL,  classOf[WorksheetFeed]).getEntries.toList

  val worksheet : Option[WorksheetEntry] = worksheets match {
    case sheet :: sheets => Some(sheet)
    case _ =>
      logger.debug("Could not find any spreadsheets with this name")
      None
  }

  worksheet.map(sheet => {
    val cellFeedUrl = sheet.getCellFeedUrl
    val cellFeed : List[CellEntry] = spreadsheetsService.getFeed(cellFeedUrl, classOf[CellFeed]).getEntries.toList
    cellFeed.map(cell => {
      println(cell.getCell.getInputValue + " at " + cell.getTitle.getPlainText)
    })
  })

}
