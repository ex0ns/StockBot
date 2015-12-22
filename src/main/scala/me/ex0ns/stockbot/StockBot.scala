package me.ex0ns.stockbot

import info.mukel.telegram.bots.{Utils, Polling, Commands, TelegramBot}

import scala.collection.JavaConversions._

/**
  * Created by ex0ns on 12/22/15.
  */
class StockBot(
    service: DriveService,
    settings: Settings)
  extends TelegramBot(Utils.tokenFromFile(settings.botKeyPath))
  with Polling
  with Commands {

  def printDrive() : Unit = {
    println("You currently have " +  service().files().list().execute().getItems.size() + " file(s) in your drive:")
    service().files().list().execute().getItems.foreach(f => println("\t- " + f.getTitle))
  }

}
