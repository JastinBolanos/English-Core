package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

data class ScheduledAlarmInfo(
  val hour: Int,
  val minute: Int,
  val triggerAtMillis: Long,
  val preventiveAtMillis: Long
)

data class SavedAlarmState(
  val hour: Int,
  val minute: Int,
  val isActive: Boolean,
  val triggerAtMillis: Long = 0L,
  val preventiveAtMillis: Long = 0L
)

class AlarmScheduler(private val context: Context) {

  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  companion object {
    const val PREFS_NAME = "alarm_prefs"
    const val KEY_ALARM_HOUR = "alarm_hour"
    const val KEY_ALARM_MINUTE = "alarm_minute"
    const val KEY_IS_ALARM_ACTIVE = "is_alarm_active"
    const val KEY_TRIGGER_MILLIS = "trigger_at_millis"
    const val KEY_PREVENTIVE_MILLIS = "preventive_at_millis"

    const val REQUEST_CODE_PREVENTIVE = 2001
    const val REQUEST_CODE_EXACT = 2002
  }

  fun canScheduleExactAlarms(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      alarmManager.canScheduleExactAlarms()
    } else {
      true
    }
  }

  fun getSavedAlarmState(): SavedAlarmState {
    val isActive = prefs.getBoolean(KEY_IS_ALARM_ACTIVE, false)
    val hour = prefs.getInt(KEY_ALARM_HOUR, -1)
    val minute = prefs.getInt(KEY_ALARM_MINUTE, -1)
    val trigger = prefs.getLong(KEY_TRIGGER_MILLIS, 0L)
    val preventive = prefs.getLong(KEY_PREVENTIVE_MILLIS, 0L)
    return SavedAlarmState(
      hour = hour,
      minute = minute,
      isActive = isActive,
      triggerAtMillis = trigger,
      preventiveAtMillis = preventive
    )
  }

  fun getActiveScheduledAlarmInfo(): ScheduledAlarmInfo? {
    val state = getSavedAlarmState()
    if (!state.isActive || state.hour !in 0..23 || state.minute !in 0..59) {
      return null
    }

    val now = LocalDateTime.now()
    var target = now.withHour(state.hour).withMinute(state.minute).withSecond(0).withNano(0)
    if (!target.isAfter(now)) {
      target = target.plusDays(1)
    }
    val zone = ZoneId.systemDefault()
    val exactTriggerMillis = target.atZone(zone).toInstant().toEpochMilli()
    val preventiveTriggerMillis = exactTriggerMillis - (10 * 60 * 1000L)

    return ScheduledAlarmInfo(
      hour = state.hour,
      minute = state.minute,
      triggerAtMillis = exactTriggerMillis,
      preventiveAtMillis = preventiveTriggerMillis
    )
  }

  /**
   * Programa la alarma exacta para la hora elegida por el usuario:
   * 1. Notificación preventiva exactamente 10 minutos antes (X - 10 min)
   * 2. Alarma con sonido exactamente a la hora cero (X)
   * Si la hora ya pasó en el día actual, se suma automáticamente 1 día (plusDays(1)).
   */
  fun scheduleAlarm(hour: Int, minute: Int): ScheduledAlarmInfo {
    val now = LocalDateTime.now()
    var targetDateTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

    // Si la hora elegida ya pasó hoy o es el momento actual, se programa para mañana
    if (!targetDateTime.isAfter(now)) {
      targetDateTime = targetDateTime.plusDays(1)
    }

    val zone = ZoneId.systemDefault()
    val exactTriggerMillis = targetDateTime.atZone(zone).toInstant().toEpochMilli()
    val preventiveTriggerMillis = exactTriggerMillis - (10 * 60 * 1000L) // X - 10 minutos

    // 1. PendingIntent para notificación preventiva (10 min antes)
    val preventiveIntent = Intent(context, AlarmReceiver::class.java).apply {
      putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_PREVENTIVE)
    }
    val preventivePendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_PREVENTIVE,
      preventiveIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (preventiveTriggerMillis > System.currentTimeMillis()) {
      setExactAlarm(preventiveTriggerMillis, preventivePendingIntent)
    }

    // 2. PendingIntent para alarma exacta a la hora cero (Sonido de alarma)
    val exactIntent = Intent(context, AlarmReceiver::class.java).apply {
      putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_ALARM)
    }
    val exactPendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_EXACT,
      exactIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    setExactAlarm(exactTriggerMillis, exactPendingIntent)

    // Guardar permanentemente en SharedPreferences ('alarm_prefs')
    prefs.edit()
      .putInt(KEY_ALARM_HOUR, hour)
      .putInt(KEY_ALARM_MINUTE, minute)
      .putBoolean(KEY_IS_ALARM_ACTIVE, true)
      .putLong(KEY_TRIGGER_MILLIS, exactTriggerMillis)
      .putLong(KEY_PREVENTIVE_MILLIS, preventiveTriggerMillis)
      .apply()

    return ScheduledAlarmInfo(
      hour = hour,
      minute = minute,
      triggerAtMillis = exactTriggerMillis,
      preventiveAtMillis = preventiveTriggerMillis
    )
  }

  fun scheduleAlarm(dateTime: LocalDateTime): ScheduledAlarmInfo {
    val zone = ZoneId.systemDefault()
    val exactTriggerMillis = dateTime.atZone(zone).toInstant().toEpochMilli()
    val preventiveTriggerMillis = exactTriggerMillis - (10 * 60 * 1000L)

    val preventiveIntent = Intent(context, AlarmReceiver::class.java).apply {
      putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_PREVENTIVE)
    }
    val preventivePendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_PREVENTIVE,
      preventiveIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (preventiveTriggerMillis > System.currentTimeMillis()) {
      setExactAlarm(preventiveTriggerMillis, preventivePendingIntent)
    }

    val exactIntent = Intent(context, AlarmReceiver::class.java).apply {
      putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_ALARM)
    }
    val exactPendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_EXACT,
      exactIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    setExactAlarm(exactTriggerMillis, exactPendingIntent)

    prefs.edit()
      .putInt(KEY_ALARM_HOUR, dateTime.hour)
      .putInt(KEY_ALARM_MINUTE, dateTime.minute)
      .putBoolean(KEY_IS_ALARM_ACTIVE, true)
      .putLong(KEY_TRIGGER_MILLIS, exactTriggerMillis)
      .putLong(KEY_PREVENTIVE_MILLIS, preventiveTriggerMillis)
      .apply()

    return ScheduledAlarmInfo(
      hour = dateTime.hour,
      minute = dateTime.minute,
      triggerAtMillis = exactTriggerMillis,
      preventiveAtMillis = preventiveTriggerMillis
    )
  }

  fun cancelAlarm() {
    try {
      val preventiveIntent = Intent(context, AlarmReceiver::class.java)
      val preventivePending = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_PREVENTIVE,
        preventiveIntent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
      )
      preventivePending?.let { alarmManager.cancel(it) }

      val exactIntent = Intent(context, AlarmReceiver::class.java)
      val exactPending = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_EXACT,
        exactIntent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
      )
      exactPending?.let { alarmManager.cancel(it) }

      // Actualizar SharedPreferences: is_alarm_active = false
      prefs.edit()
        .putBoolean(KEY_IS_ALARM_ACTIVE, false)
        .apply()
    } catch (e: Exception) {
      Log.e("AlarmScheduler", "Error al cancelar alarmas: ${e.message}")
    }
  }

  private fun setExactAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
    try {
      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
      )
    } catch (e: SecurityException) {
      Log.w("AlarmScheduler", "Sin permiso de alarma exacta, usando setAndAllowWhileIdle: ${e.message}")
      alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
      )
    }
  }
}
