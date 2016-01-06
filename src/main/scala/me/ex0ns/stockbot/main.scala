package me.ex0ns.stockbot

import me.ex0ns.stockbot.bot.StockBot

object main {

  def main(args: Array[String]) : Unit = {


    val bot = StockBot()

    bot.run()
  }

}
