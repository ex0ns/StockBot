package me.ex0ns.stockbot

import java.io.File

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.{Drive, DriveScopes}

/**
  * Created by ex0ns on 12/22/15.
  */
class DriveService(settings: Settings) {

  private val emailAddress = settings.email
  private val jsonFactory = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private val credential = new GoogleCredential.Builder()
    .setTransport(httpTransport)
    .setJsonFactory(jsonFactory)
    .setServiceAccountId(emailAddress)
    .setServiceAccountPrivateKeyFromP12File(new File(settings.keyPath))
    .setServiceAccountScopes(DriveScopes.all())
    .build()

  private val service = new Drive.Builder(httpTransport, jsonFactory, null)
    .setHttpRequestInitializer(credential).build()

  def apply() = service

}
