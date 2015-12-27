package me.ex0ns.stockbot

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

/**
  * Created by ex0ns on 12/22/15.
  */
class Settings {

  implicit def integerListToInt(d: List[java.lang.Integer]) : List[Int] = d.map(_.intValue())

  private val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "drive-client")
  config.checkValid(ConfigFactory.defaultReference(), "telegram-client")

  val email = config.getString("drive-client.email")
  val keyPath = config.getString("drive-client.key-path")
  val botKeyPath = config.getString("telegram-client.key-path")
  val filename = config.getString("drive-client.filename")

  val startRow = config.hasPath("drive-client.start-row") match {
    case true => config.getInt("drive-client.start-row")
    case false => 1
  }

  val cols : List[Int] = config.hasPath("drive-client.cols") match {
    case true => config.getIntList("drive-client.cols").toList
    case false => List(1)
  }

}
