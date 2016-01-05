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

  def getStockItems : List[Item] = service.getAllItems

  def addItem(name: String, count: Int) = service.addItem(name, count)

}
