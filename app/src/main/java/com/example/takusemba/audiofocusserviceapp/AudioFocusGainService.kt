package com.example.takusemba.audiofocusserviceapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log

class AudioFocusGainService : Service() {

  companion object {
    const val TAG = "AudioFocusGainService"

    const val AUDIO_REQUEST_TYPE = "AUDIO_REQUEST_TYPE"
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  enum class AudioRequestType(val id: Int, val audioFocusId: Int, val title: String) {
    GAIN(
        id = 1,
        audioFocusId = AudioManager.AUDIOFOCUS_GAIN,
        title = "AUDIOFOCUS_GAIN"
    ),

    GAIN_TRANSIENT(
        id = 2,
        audioFocusId = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
        title = "AUDIOFOCUS_GAIN_TRANSIENT"
    ),

    GAIN_TRANSIENT_MAY_DUCK(
        id = 3,
        audioFocusId = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
        title = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK"
    ),

    GAIN_TRANSIENT_EXCLUSIVE(
        id = 4,
        audioFocusId = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
        title = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE"
    )
  }

  private lateinit var request: AudioFocusRequest

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

    if (intent.hasExtra(AUDIO_REQUEST_TYPE)) {
      val audioRequest = when (intent.getIntExtra(AUDIO_REQUEST_TYPE, -1)) {
        AudioRequestType.GAIN.id -> AudioRequestType.GAIN
        AudioRequestType.GAIN_TRANSIENT.id -> AudioRequestType.GAIN_TRANSIENT
        AudioRequestType.GAIN_TRANSIENT_MAY_DUCK.id -> AudioRequestType.GAIN_TRANSIENT_MAY_DUCK
        AudioRequestType.GAIN_TRANSIENT_EXCLUSIVE.id -> AudioRequestType.GAIN_TRANSIENT_EXCLUSIVE
        else -> throw IllegalStateException("invalid request type")
      }

      val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
      request = AudioFocusRequest
          .Builder(audioRequest.audioFocusId)
          .setAudioAttributes(AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setOnAudioFocusChangeListener {}
          .setAcceptsDelayedFocusGain(true)
          .build()
      audioManager.requestAudioFocus(request)

      val notification = NotificationCompat.Builder(this, Config.channelId)
          .setContentTitle(audioRequest.title)
          .setSmallIcon(R.mipmap.ic_launcher)
          .build()

      val notificationManager = NotificationManagerCompat.from(this)
      notificationManager.notify(Config.notificationId, notification)
      startForeground(Config.foregroundId, notification)
      Log.d(TAG, "request audio focus")
    } else {
      stopSelf()
    }

    return Service.START_NOT_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::request.isInitialized) {
      val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
      audioManager.abandonAudioFocusRequest(request)
      Log.d(TAG, "release audio focus")
    }
  }
}