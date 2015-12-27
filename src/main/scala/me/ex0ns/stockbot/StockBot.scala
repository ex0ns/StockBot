package me.ex0ns.stockbot

import info.mukel.telegram.bots.{Commands, Polling, TelegramBot, Utils}

/**
  * Created by ex0ns on 12/22/15.
  */
class StockBot(
    service: DriveService,
    settings: Settings)
  extends TelegramBot(Utils.tokenFromFile(settings.botKeyPath))
  with Polling
  with Commands {

  def printDrive : Unit = {
    println("You currently have " +  service.files.size + " file(s) in your drive:")
    service.files.foreach(f => println("\t- " + f.getTitle))
  }

  def getStockItems : List[Item] = service.getAllItems

}
