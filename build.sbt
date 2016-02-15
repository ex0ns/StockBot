name := "StockBot"
version := "0.1"
scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("snapshots")
scalacOptions ++= Seq("-feature")
libraryDependencies ++= Seq(
  "info.mukel" %% "telegrambot4s" % "1.0.2-SNAPSHOT",
  "com.google.gdata" % "core" % "1.47.1",
  "com.google.apis" % "google-api-services-oauth2" % "v2-rev83-1.19.1",
  "com.google.apis" % "google-api-services-drive" % "v2-rev160-1.19.1",
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.slf4j" % "slf4j-simple" % "1.6.4"
)
