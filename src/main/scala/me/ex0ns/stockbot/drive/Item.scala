package me.ex0ns.stockbot.drive

/**
  * Created by ex0ns on 12/27/15.
  */
case class Item(name: String, count: Int) {
  override def toString() = name + " : " + count
}
