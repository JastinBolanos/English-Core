package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ScheduledAlarmInfo(
  val hour: Int,
  val minute: Int,
  val triggerAtMillis: Long,
  val preventiveAtMillis: Long
)

data class SavedAlarmState(
  val isActive: Boolean,
  val hour: Int,
  val minute: Int
)

class AlarmScheduler(private val context: Context) {

  companion object {
    const val PREFS_NAME = "alarm_prefs"
    const val KEY_IS_ACTIVE = "is_alarm_active"
    const val KEY_ALARM_HOUR = "alarm_hour"
    const val KEY_ALARM_MINUTE = "alarm_minute"
    const val KEY_TRIGGER_MILLIS = "trigger_millis"
    const val KEY_PREVENTIVE_MILLIS = "preventive_millis"

    private const val REQUEST_CODE_EXACT = 2001
    private const val REQUEST_CODE_PREVENTIVE = 2002
  }

  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun scheduleAlarm(hour: Int, minute: Int): ScheduledAlarmInfo {
    val now = LocalDateTime.now()
    var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    if (!target.isAfter(now)) {
      target = target.plusDays(1)
    }

    val triggerAtMillis = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val preventiveAtMillis = triggerAtMillis - (10 * 60 * 1000L) // 10 minutes prior

    // Schedule exact alarm
    val exactIntent = Intent(context, AlarmReceiver::class.java).apply {
      putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_ALARM)
    }
    val exactPendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_EXACT,
      exactIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager?.let { am ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, exactPendingIntent)
      } else {
        am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, exactPendingIntent)
      }
    }

    // Schedule preventive reminder if in future
    if (preventiveAtMillis > System.currentTimeMillis()) {
      val prevIntent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, AlarmReceiver.TYPE_PREVENTIVE)
      }
      val prevPendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_PREVENTIVE,
        prevIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
      alarmManager?.let { am ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, preventiveAtMillis, prevPendingIntent)
        } else {
          am.setExact(AlarmManager.RTC_WAKEUP, preventiveAtMillis, prevPendingIntent)
        }
      }
    }

    // Persist to SharedPreferences
    prefs.edit()
      .putBoolean(KEY_IS_ACTIVE, true)
      .putInt(KEY_ALARM_HOUR, hour)
      .putInt(KEY_ALARM_MINUTE, minute)
      .putLong(KEY_TRIGGER_MILLIS, triggerAtMillis)
      .putLong(KEY_PREVENTIVE_MILLIS, preventiveAtMillis)
      .apply()

    return ScheduledAlarmInfo(
      hour = hour,
      minute = minute,
      triggerAtMillis = triggerAtMillis,
      preventiveAtMillis = preventiveAtMillis
    )
  }

  fun cancelAlarm() {
    val exactIntent = Intent(context, AlarmReceiver::class.java)
    val exactPendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_EXACT,
      exactIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager?.cancel(exactPendingIntent)

    val prevIntent = Intent(context, AlarmReceiver::class.java)
    val prevPendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_PREVENTIVE,
      prevIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager?.cancel(prevPendingIntent)

    prefs.edit()
      .putBoolean(KEY_IS_ACTIVE, false)
      .apply()
  }

  fun getSavedAlarmState(): SavedAlarmState {
    return SavedAlarmState(
      isActive = prefs.getBoolean(KEY_IS_ACTIVE, false),
      hour = prefs.getInt(KEY_ALARM_HOUR, -1),
      minute = prefs.getInt(KEY_ALARM_MINUTE, -1)
    )
  }

  fun getActiveScheduledAlarmInfo(): ScheduledAlarmInfo? {
    val isActive = prefs.getBoolean(KEY_IS_ACTIVE, false)
    if (!isActive) return null

    val hour = prefs.getInt(KEY_ALARM_HOUR, -1)
    val minute = prefs.getInt(KEY_ALARM_MINUTE, -1)
    if (hour !in 0..23 || minute !in 0..59) return null

    var triggerMillis = prefs.getLong(KEY_TRIGGER_MILLIS, 0L)
    var prevMillis = prefs.getLong(KEY_PREVENTIVE_MILLIS, 0L)

    if (triggerMillis <= System.currentTimeMillis()) {
      // Recompute next occurrence
      val now = LocalDateTime.now()
      var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
      if (!target.isAfter(now)) {
        target = target.plusDays(1)
      }
      triggerMillis = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
      prevMillis = triggerMillis - (10 * 60 * 1000L)
    }

    return ScheduledAlarmInfo(
      hour = hour,
      minute = minute,
      triggerAtMillis = triggerMillis,
      preventiveAtMillis = prevMillis
    )
  }
}
