package me.ex0ns.stockbot.bot

import com.typesafe.scalalogging.Logger
import info.mukel.telegrambot4s._, api._, methods._, models._, Implicits._
import me.ex0ns.stockbot.drive.DriveService.DriveMessage
import me.ex0ns.stockbot.drive.{DriveService, Item}
import me.ex0ns.stockbot.Settings
import me.ex0ns.stockbot.utils.Strings
import org.slf4j.LoggerFactory

import scala.io.Source


/**
  * Created by ex0ns on 12/22/15.
  */
object StockBot{

  private val settings : Settings = new Settings
  private val service  : DriveService = new DriveService(settings)
  private val bot : StockBot = new StockBot(service, settings)

  def apply() = {
    bot.logger.debug("Bot is up and running !")

    bot.on("ls") { implicit msg => _ =>
      val items = bot.getStockItems.map(_.toString())
      bot.api.request(SendMessage(msg.sender,  Strings.CURRENT_STOCK(items.length, items.mkString("\n"))))
    }

    bot.on("rm") { implicit msg => args =>
      bot.reply({
        if(args.size != 2) {
          bot.usage
        } else {
          bot.removeItem(args.head, args(1).toInt) match {
            case DriveMessage(true, msg) => msg
            case DriveMessage(false, stock) => Strings.REMOVE_STOCK(args.head, args(1), stock)
          }
        }
      })
    }

    bot.on("add") { implicit msg => args =>
      bot.reply({
        if(args.size != 2) {
          bot.usage
        } else {
          bot.addItem(args.head, args(1).toInt) match {
            case DriveMessage(true, msg) => msg
            case DriveMessage(false, stock) => Strings.ADD_STOCK(args.head, stock)
          }
        }
      })
    }

    bot.on("help") { implicit msg => _ =>
      bot.reply(bot.usage)
    }

    bot
  }
}

class StockBot(
    service: DriveService,
    settings: Settings) extends TelegramBot with Commands with Polling{


  override def token = Source.fromFile(settings.botKeyPath).getLines().next
  private val logger = Logger(LoggerFactory.getLogger(classOf[StockBot]))

  def getStockItems : List[Item] = service.getAllItems

  def addItem(name: String, count: Int) : DriveMessage = service.addItem(name, count)

  def removeItem(name: String, count: Int) : DriveMessage = service.removeStock(name, count)

  def usage : String = {
    """Usage:
      |   ls: list all the available items in stock
      |   add item number: add number of item to the stock
      |   rm item number: remove number of item from the stock
      |   help: display this
    """.stripMargin
  }
}
