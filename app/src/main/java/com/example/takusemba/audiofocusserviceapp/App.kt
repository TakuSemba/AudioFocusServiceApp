package com.example.takusemba.audiofocusserviceapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    val channel = NotificationChannel(
        Config.channelId,
        Config.channelName,
        NotificationManager.IMPORTANCE_NONE
    )
    val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(channel)
  }
}