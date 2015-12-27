package me.ex0ns.stockbot

object main {

  def main(args: Array[String]) : Unit = {
    val settings : Settings = new Settings
    val service  = new DriveService(settings)

    val bot = new StockBot(service, settings)

    bot.getStockItems.foreach(println(_))
  }

}
