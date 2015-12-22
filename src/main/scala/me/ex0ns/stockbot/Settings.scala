package me.ex0ns.stockbot

import com.typesafe.config.ConfigFactory

/**
  * Created by ex0ns on 12/22/15.
  */
class Settings {

  private val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "drive-client")
  config.checkValid(ConfigFactory.defaultReference(), "telegram-client")

  val email = config.getString("drive-client.email")
  val keyPath = config.getString("drive-client.key-path")
  val botKeyPath = config.getString("telegram-client.key-path")
}
