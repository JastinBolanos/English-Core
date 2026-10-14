package com.example.presentation.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarm.AlarmScheduler
import com.example.alarm.ScheduledAlarmInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AlarmsUiState(
  val isAlarmActive: Boolean = false,
  val hour: Int = -1,
  val minute: Int = -1,
  val scheduledInfo: ScheduledAlarmInfo? = null
)

class AlarmsViewModel(
  application: Application,
  private val scheduler: AlarmScheduler = AlarmScheduler(application)
) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(AlarmsUiState())
  val uiState: StateFlow<AlarmsUiState> = _uiState.asStateFlow()

  init {
    loadSavedAlarm()
  }

  fun loadSavedAlarm() {
    val saved = scheduler.getSavedAlarmState()
    if (saved.isActive && saved.hour in 0..23 && saved.minute in 0..59) {
      val info = scheduler.getActiveScheduledAlarmInfo()
      _uiState.value = AlarmsUiState(
        isAlarmActive = true,
        hour = saved.hour,
        minute = saved.minute,
        scheduledInfo = info
      )
    } else {
      _uiState.value = AlarmsUiState(
        isAlarmActive = false,
        hour = if (saved.hour in 0..23) saved.hour else -1,
        minute = if (saved.minute in 0..59) saved.minute else -1,
        scheduledInfo = null
      )
    }
  }

  fun scheduleAlarm(hour: Int, minute: Int): ScheduledAlarmInfo {
    val info = scheduler.scheduleAlarm(hour, minute)
    _uiState.value = AlarmsUiState(
      isAlarmActive = true,
      hour = hour,
      minute = minute,
      scheduledInfo = info
    )
    return info
  }

  fun cancelAlarm() {
    scheduler.cancelAlarm()
    _uiState.update { current ->
      current.copy(
        isAlarmActive = false,
        scheduledInfo = null
      )
    }
  }

  class Factory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return AlarmsViewModel(application) as T
    }
  }
}
