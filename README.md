# StockBot

StockBot is a [Telegram](https://telegram.org) bot, made in scala using the [telegrambot4s](https://github.com/mukel/telegrambot4s) library.
It aims to help managing home stocks and make the logistics/communication easier.

### Installation && build

You obviously need Scala and SBT to make it run.

Then you'll need a Google Drive key as well as one for the Telegram Bot.

- Follow [this](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) to get the needed P12 key
- And [this](https://core.telegram.org/bots) to obtains the Telegram key for your brand new bot.


Simply create a 'keys' folder at the root and add the keys (see [Configuration](#configuration)).

### Configuration <a name="configuration">

The configuration is done through the [application.conf](https://github.com/ex0ns/StockBot/blob/master/src/main/resources/application.conf) file.
It's composed of two parts, the first one `drive-client` which is the Google Drive related configuration and the `telegram-client` file
which is the telegram one.


The base configuration: 

```
drive-client {
  email="stockbot@telegrambot-1166.iam.gserviceaccount.com" // Email of the Drive Service Account 
  key-path="keys/drive.p12" // Path to the drive key
  filename="stock.xsls" // The sheet's name
}

telegram-client {
  key-path="./keys/telegram.key" // Path to the telegram key
}

```

You are go to go ! The last step is to share the stock sheet you want the bot to manage to the Service Account email
so the bot can access it.

### Contribute

Feel free to contribute, report any bug or submit ideas to improve the bot !

### License

See [License](https://github.com/ex0ns/StockBot/blob/master/LICENSE) !
