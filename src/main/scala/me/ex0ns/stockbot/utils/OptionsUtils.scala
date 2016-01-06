package me.ex0ns.stockbot.utils

import scala.language.implicitConversions
/**
  * Created by ex0ns on 1/5/16.
  */
object OptionsUtils {
  implicit  def toOption[T](obj: T) : Option[T] = Option(obj)
}
