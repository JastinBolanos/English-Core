package com.example.presentation.alarm

import android.Manifest
import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

fun formatAlarmTime12Hour(hour: Int, minute: Int): String {
  val amPm = if (hour >= 12) "PM" else "AM"
  val hour12 = when {
    hour == 0 -> 12
    hour > 12 -> hour - 12
    else -> hour
  }
  return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
}

fun formatAlarmTime24Hour(hour: Int, minute: Int): String {
  return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AlarmsViewModel = viewModel(
    factory = AlarmsViewModel.Factory(
      LocalContext.current.applicationContext as Application
    )
  )
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  // Permiso de notificaciones para Android 13+
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      if (!isGranted) {
        Toast.makeText(context, "Permite notificaciones para recibir el aviso", Toast.LENGTH_SHORT).show()
      }
    }
  )

  // Cargar por defecto la hora guardada de SharedPreferences si está activa, o la hora actual
  val now = Calendar.getInstance()
  val initialHour = if (uiState.isAlarmActive && uiState.hour in 0..23) {
    uiState.hour
  } else {
    now.get(Calendar.HOUR_OF_DAY)
  }
  val initialMinute = if (uiState.isAlarmActive && uiState.minute in 0..59) {
    uiState.minute
  } else {
    now.get(Calendar.MINUTE)
  }

  val timePickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = false
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("screen_alarmas"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(id = R.string.btn_alarmas),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.back)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Text(
        text = "SELECTOR DE ALARMA EXACTA",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.align(Alignment.Start)
      )

      Text(
        text = "Elige la hora de tu práctica. El sistema enviará un aviso 10 minutos antes y activará el sonido a la hora exacta.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.Start)
      )

      // Tarjeta de Alarma Activa guardada en SharedPreferences (Persistencia Automática)
      AnimatedVisibility(
        visible = uiState.isAlarmActive,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        val targetHour = uiState.hour
        val targetMinute = uiState.minute
        val time12 = formatAlarmTime12Hour(targetHour, targetMinute)
        val time24 = formatAlarmTime24Hour(targetHour, targetMinute)

        // Hora del aviso preventivo (10 min antes)
        val prevMillis = uiState.scheduledInfo?.preventiveAtMillis ?: 0L
        val prevFormatted = if (prevMillis > 0L) {
          val prevLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(prevMillis), ZoneId.systemDefault())
          formatAlarmTime12Hour(prevLdt.hour, prevLdt.minute)
        } else {
          // Fallback por cálculo
          val rawMinutes = (targetHour * 60 + targetMinute - 10 + 1440) % 1440
          formatAlarmTime12Hour(rawMinutes / 60, rawMinutes % 60)
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_active_alarm"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDCFCE7)
          ),
          border = BorderStroke(1.5.dp, Color(0xFF86EFAC)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(0xFF166534),
                  modifier = Modifier.size(36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "⏰ Alarma activa: $time12",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF166534),
                    fontSize = 17.sp
                  )
                  Text(
                    text = "Formato 24h: $time24",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF15803D)
                  )
                }
              }
            }

            HorizontalDivider(
              color = Color(0xFF86EFAC),
              thickness = 1.dp,
              modifier = Modifier.padding(vertical = 2.dp)
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = Color(0xFF166534),
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Aviso preventivo programado a las $prevFormatted (10 min antes)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF166534)
              )
            }

            Text(
              text = "• Sonido de alarma a la hora exacta (inmune a Doze y ahorro de batería)",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF166534)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Botón 'Eliminar / Apagar Alarma'
            OutlinedButton(
              onClick = {
                viewModel.cancelAlarm()
                Toast.makeText(context, "Alarma apagada y eliminada", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_cancel_alarm"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF991B1B)
              ),
              border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
              Icon(
                imageVector = Icons.Default.AlarmOff,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF991B1B)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Eliminar / Apagar Alarma",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF991B1B)
              )
            }
          }
        }
      }

      // TimePicker de Material 3 interactivo
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("time_picker_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
              clockDialColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              selectorColor = MaterialTheme.colorScheme.primary,
              containerColor = MaterialTheme.colorScheme.surface
            )
          )
        }
      }

      // Botón 'Guardar Alarma' / 'Reemplazar Alarma'
      Button(
        onClick = {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }

          val info = viewModel.scheduleAlarm(timePickerState.hour, timePickerState.minute)
          val timeFormatted = formatAlarmTime12Hour(info.hour, info.minute)

          Toast.makeText(
            context,
            "Alarma programada y guardada para las $timeFormatted",
            Toast.LENGTH_SHORT
          ).show()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_save_alarm"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Icon(imageVector = Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (uiState.isAlarmActive) "Actualizar / Reemplazar Alarma" else "Guardar Alarma",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      }
    }
  }
}
