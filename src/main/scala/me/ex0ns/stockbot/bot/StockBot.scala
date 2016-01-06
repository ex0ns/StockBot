package me.ex0ns.stockbot.bot

import info.mukel.telegram.bots.{Commands, Polling, TelegramBot, Utils}
import me.ex0ns.stockbot.drive.{DriveService, Item}
import me.ex0ns.stockbot.Settings


/**
  * Created by ex0ns on 12/22/15.
  */

object StockBot {

  private val settings : Settings = new Settings
  private val service  : DriveService = new DriveService(settings)
  private val bot : StockBot = new StockBot(service, settings)

  def apply() = {
    println("Bot is up and running !")

    bot.on("ls") { (sender, _) =>
      bot.replyTo(sender) {
        val items = bot.getStockItems.map(_.toString())
        s"You currently have ${items.length} items in stock:\n${items.mkString("\n")}"
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

  def getStockItems : List[Item] = service.getAllItems

  def addItem(name: String, count: Int) = service.addItem(name, count)

}
