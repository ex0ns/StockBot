package me.ex0ns.stockbot.bot

import com.typesafe.scalalogging.Logger
import info.mukel.telegram.bots.{Commands, Polling, TelegramBot, Utils}
import me.ex0ns.stockbot.drive.DriveService.DriveMessage
import me.ex0ns.stockbot.drive.{DriveService, Item}
import me.ex0ns.stockbot.Settings
import org.slf4j.LoggerFactory


/**
  * Created by ex0ns on 12/22/15.
  */
object StockBot {

  private val settings : Settings = new Settings
  private val service  : DriveService = new DriveService(settings)
  private val bot : StockBot = new StockBot(service, settings)

  def apply() = {
    bot.logger.debug("Bot is up and running !")

    bot.on("ls") { (sender, _) =>
      bot.replyTo(sender) {
        val items = bot.getStockItems.map(_.toString())
        s"You currently have ${items.length} items in stock:\n${items.mkString("\n")}"
      }
    }

    bot.on("rm") { (sender, args) =>
      bot.replyTo(sender) {
        if(args.size != 2) {
          bot.usage
        } else {
          bot.removeItem(args.head, args(1).toInt) match {
            case DriveMessage(true, msg) => msg
            case DriveMessage(false, stock) =>
              s"Removed ${args(1)} of ${args.head}, $stock remaining"
          }
        }
      }
    }

    bot.on("add") { (sender, args) =>
      bot.replyTo(sender) {
        if(args.size != 2) {
          bot.usage
        } else {
          bot.addItem(args.head, args(1).toInt) match {
            case DriveMessage(true, msg) => msg
            case DriveMessage(false, stock) => s"There is now $stock remaining ${args.head}"
          }
        }
      }
    }

    bot.on("help") { (sender, args) =>
      bot.replyTo(sender) {
          bot.usage
      }
    }

    bot
  }
}

class StockBot(
    service: DriveService,
    settings: Settings)
  extends TelegramBot(Utils.tokenFromFile(settings.botKeyPath))
  with Polling
  with Commands {

  private val logger = Logger(LoggerFactory.getLogger(classOf[StockBot]))

  def getStockItems : List[Item] = service.getAllItems

  def addItem(name: String, count: Int) : DriveMessage = service.addItem(name, count)

  def removeItem(name: String, count: Int) : DriveMessage = service.removeStock(name, count)

  def usage : String = {
    ""
  }

}
