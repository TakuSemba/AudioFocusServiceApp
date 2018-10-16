package com.example.takusemba.audiofocusserviceapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

class AudioFocusGainTransientService : Service() {

  companion object {
    const val TAG = "AudioFocusGainService"
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  private val request: AudioFocusRequest = AudioFocusRequest
      .Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
      .setAudioAttributes(AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_GAME)
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .build()
      )
      .setOnAudioFocusChangeListener {}
      .setAcceptsDelayedFocusGain(true)
      .build()

  override fun onCreate() {
    super.onCreate()
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.requestAudioFocus(request)
    val notification = NotificationCompat.Builder(this, "test")
        .setContentTitle("AUDIOFOCUS_GAIN_TRANSIENT")
        .build()
    startForeground(1, notification)
    Log.d(TAG, "request audio focus")
  }

  override fun onDestroy() {
    super.onDestroy()
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.abandonAudioFocusRequest(request)
    Log.d(TAG, "release audio focus")
  }
}