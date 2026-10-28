package com.example.presentation.schedule

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Representa uno de los objetivos diarios de la misión del día: Escuchar, Hablar, Leer.
 */
data class DailyGoalItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val accentColor: Color
)

object DailyGoalsData {
    val goals = listOf(
        DailyGoalItem(
            id = "goal_listening",
            title = "Escuchar",
            icon = Icons.Default.Headphones,
            accentColor = Color(0xFFF59E0B)
        ),
        DailyGoalItem(
            id = "goal_speaking",
            title = "Hablar",
            icon = Icons.Default.RecordVoiceOver,
            accentColor = Color(0xFFA855F7)
        ),
        DailyGoalItem(
            id = "goal_reading",
            title = "Leer",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            accentColor = Color(0xFF38BDF8)
        )
    )
}

/**
 * Gestor de persistencia diaria para registrar los objetivos completados hoy.
 * Compatible hacia atrás con claves anteriores.
 */
class ScheduleStorage(context: Context) {
    private val prefs = context.getSharedPreferences("daily_study_schedule_prefs", Context.MODE_PRIVATE)

    fun isBlockDoneToday(dateKey: String, blockId: String): Boolean {
        val legacyId = when (blockId) {
            "goal_listening" -> "slot_morning"
            "goal_reading" -> "slot_afternoon"
            "goal_speaking" -> "slot_evening"
            else -> blockId
        }
        return prefs.getBoolean("${dateKey}_$blockId", false) || prefs.getBoolean("${dateKey}_$legacyId", false)
    }

    fun toggleBlockDoneToday(dateKey: String, blockId: String): Boolean {
        val current = isBlockDoneToday(dateKey, blockId)
        val newState = !current
        prefs.edit().putBoolean("${dateKey}_$blockId", newState).apply()
        return newState
    }
}

/**
 * Bandeja flotante modal: "Tus Objetivos de Hoy" / "Misión del Día".
 * Presenta únicamente los objetivos clave: Escuchar, Hablar, Leer.
 */
@Composable
fun FloatingDailyScheduleTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    BackHandler { onClose() }

    val context = LocalContext.current
    val storage = remember { ScheduleStorage(context) }

    // Fecha actual para seguimiento diario
    val peruZone = remember { ZoneId.of("America/Lima") }
    val nowInPeru = remember(isOpen) { ZonedDateTime.now(peruZone) }
    val currentDateKey = remember(nowInPeru) {
        nowInPeru.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
    }

    // Estado reactivo de objetivos completados hoy
    val completedMap = remember(currentDateKey) {
        mutableStateMapOf<String, Boolean>().apply {
            DailyGoalsData.goals.forEach { g ->
                put(g.id, storage.isBlockDoneToday(currentDateKey, g.id))
            }
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(tween(180)) + slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(220)),
        exit = fadeOut(tween(150)) + slideOutVertically(targetOffsetY = { it / 6 }, animationSpec = tween(180)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
                    .clickable(enabled = false) {}
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.65f),
                                Color(0xFFF59E0B).copy(alpha = 0.45f),
                                Color(0xFFA855F7).copy(alpha = 0.65f),
                                Color(0xFF10B981).copy(alpha = 0.65f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("schedule_tray_container"),
                color = Color(0xFF0C1322),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Encabezado General: Tus Objetivos de Hoy / Misión del Día
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF38BDF8).copy(alpha = 0.35f),
                                                Color(0xFF0F172A)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    contentDescription = "Objetivos de hoy",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Tus Objetivos de Hoy",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFFF8FAFC)
                                    )
                                )
                                Text(
                                    text = "Misión del Día",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF38BDF8)
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_close_schedule_tray")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar objetivos",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista con los objetivos del día: Escuchar, Hablar, Leer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DailyGoalsData.goals.forEach { goal ->
                            val isDone = completedMap[goal.id] == true

                            GoalBlockCard(
                                goal = goal,
                                isDone = isDone,
                                onToggleDone = {
                                    val newState = storage.toggleBlockDoneToday(currentDateKey, goal.id)
                                    completedMap[goal.id] = newState
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta limpia para cada objetivo: Escuchar, Hablar, Leer (sin descripciones ni etiquetas de paso).
 */
@Composable
private fun GoalBlockCard(
    goal: DailyGoalItem,
    isDone: Boolean,
    onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(goal.id)
            .border(
                width = if (isDone) 1.2.dp else 1.dp,
                color = if (isDone) Color(0xFF10B981).copy(alpha = 0.7f) else Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFF0C1D1B).copy(alpha = 0.7f) else Color(0xFF0F1829)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleDone() }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = goal.accentColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = goal.accentColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = goal.icon,
                        contentDescription = goal.title,
                        tint = goal.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDone) Color(0xFF34D399) else Color(0xFFF8FAFC)
                    )
                )
            }

            // Check interactivo de estado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDone) Color(0xFF10B981).copy(alpha = 0.18f)
                        else Color(0xFF1E293B).copy(alpha = 0.6f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDone) Color(0xFF10B981).copy(alpha = 0.5f)
                        else Color(0xFF334155).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onToggleDone() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_toggle_${goal.id}")
            ) {
                Icon(
                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isDone) "Completado" else "Pendiente",
                    tint = if (isDone) Color(0xFF34D399) else Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isDone) "Logrado ✓" else "Pendiente",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isDone) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )
                )
            }
        }
    }
}
