package me.ex0ns.stockbot.utils

/**
  * Created by ex0ns on 2/15/16.
  */
object Strings {
  val NOT_IN_STOCK    = "Item is not in stock, you may want to use `addItem` instead !"
  val NEGATIVE_STOCK  = "Could not have a negative stock"
  val ALREADY_EXISTS  = "Item is already in the spreadsheet"
  val NO_CELL_FOUND   = "Could not find a cell to insert new item to"
  val NO_WORKSHEET    = "No worksheet available"

  def CELL_NOT_FOUND(row: Int, col: Int) = s"Unable to locate cell at ($row,$col)"
}
