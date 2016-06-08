package me.ex0ns.stockbot.utils

/**
  * Created by ex0ns on 2/15/16.
  */
object Strings {
  val NOT_IN_STOCK    = "Item is not in stock, you may want to use `addItem` instead !"
  val NEGATIVE_STOCK  = "Can not have a negative stock"
  val ALREADY_EXISTS  = "Item is already in the spreadsheet"
  val NO_CELL_FOUND   = "Unable to find a cell to insert new item to"
  val NO_WORKSHEET    = "No worksheet available"
  val WRONG_WORKSHEET_NAME = "Unable to find any spreadsheets with this name"
  val WRONG_RM_PARAMETER = "Could not remove negative value from stock, please use addStock function"
  val WRONG_ADD_PARAMETER = "Could not add negative value to stock, please use removeStock function"

  def CELL_NOT_FOUND(row: Int, col: Int) = s"Unable to locate cell at ($row,$col)"
  def NO_SUCH_FILE(filename: String) = s"Could not locate: '$filename' in Drive"
  def CURRENT_STOCK(size: Int, data: String) = s"You currently have $size items in stock:\n$data"
  def REMOVE_STOCK(name: String, quantity: String, remaining: String) =
    s"Removed $name of $quantity, $remaining remaining"
  def ADD_STOCK(name: String, quantity: String) = s"There is now $quantity remaining $name"
}
