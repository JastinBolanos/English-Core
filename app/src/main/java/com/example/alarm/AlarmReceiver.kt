package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AlarmReceiver : BroadcastReceiver() {

  companion object {
    const val EXTRA_ALARM_TYPE = "extra_alarm_type"
    const val TYPE_PREVENTIVE = "type_preventive"
    const val TYPE_ALARM = "type_alarm"

    const val CHANNEL_PREVENTIVE = "channel_preventive_reminder"
    const val CHANNEL_ALARM = "channel_exact_alarm"

    const val NOTIFICATION_ID_PREVENTIVE = 1001
    const val NOTIFICATION_ID_ALARM = 1002
  }

  override fun onReceive(context: Context, intent: Intent) {
    val alarmType = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: TYPE_ALARM
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    createNotificationChannels(context, notificationManager)

    when (alarmType) {
      TYPE_PREVENTIVE -> {
        // Notificación preventiva (10 minutos antes)
        showPreventiveNotification(context, notificationManager)
      }
      TYPE_ALARM -> {
        // Alarma exacta a la hora cero con sonido de alarma nativo
        triggerExactAlarm(context, notificationManager)
      }
    }
  }

  private fun showPreventiveNotification(context: Context, notificationManager: NotificationManager) {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_PREVENTIVE)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle("Recordatorio de Inglés (10 min antes)")
      .setContentText("Tu sesión programada de práctica comenzará en 10 minutos. ¡Prepárate!")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    notificationManager.notify(NOTIFICATION_ID_PREVENTIVE, notification)
  }

  private fun triggerExactAlarm(context: Context, notificationManager: NotificationManager) {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      1,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
      ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, CHANNEL_ALARM)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle("¡Hora de Entrenar!")
      .setContentText("Es momento de tu sesión de inglés. Entra ahora a repasar tus fórmulas y vocabulario.")
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setSound(alertUri)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    notificationManager.notify(NOTIFICATION_ID_ALARM, notification)

    // Reproducción de sonido con RingtoneManager
    try {
      val ringtone = RingtoneManager.getRingtone(context.applicationContext, alertUri)
      ringtone?.audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
      ringtone?.play()
    } catch (e: Exception) {
      Log.e("AlarmReceiver", "Error al reproducir Ringtone de alarma: ${e.message}")
    }
  }

  private fun createNotificationChannels(context: Context, manager: NotificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
      val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      val preventiveChannel = NotificationChannel(
        CHANNEL_PREVENTIVE,
        "Avisos Preventivos (10 min antes)",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notificaciones preventivas antes de la hora de alarma"
        enableVibration(true)
      }

      val alarmChannel = NotificationChannel(
        CHANNEL_ALARM,
        "Alarmas Exactas de Inglés",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Alarmas sonoras a la hora exacta programada"
        enableVibration(true)
        setSound(alertUri, audioAttributes)
      }

      manager.createNotificationChannel(preventiveChannel)
      manager.createNotificationChannel(alarmChannel)
    }
  }
}
