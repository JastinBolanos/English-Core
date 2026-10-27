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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
 * Representa uno de los 4 objetivos diarios de la ruta de aprendizaje sin presión de horarios.
 */
data class DailyGoalItem(
    val id: String,
    val stepLabel: String,
    val categoryLabel: String,
    val title: String,
    val description: String,
    val actionTextPending: String,
    val actionTextDone: String,
    val icon: ImageVector,
    val accentColor: Color
)

object DailyGoalsData {
    val goals = listOf(
        DailyGoalItem(
            id = "goal_listening",
            stepLabel = "Paso 1",
            categoryLabel = "Escuchar",
            title = "Objetivo 1: Inmersión Auditiva",
            description = "Escucha activa de un audio o video. Concéntrate en la pronunciación y en identificar vocabulario nuevo.",
            actionTextPending = "Marcar escucha completada",
            actionTextDone = "¡Escucha completada!",
            icon = Icons.Default.Headphones,
            accentColor = Color(0xFFF59E0B)
        ),
        DailyGoalItem(
            id = "goal_reading",
            stepLabel = "Paso 2",
            categoryLabel = "Leer",
            title = "Objetivo 2: Comprensión Lectora",
            description = "Lee un texto corto. Identifica conectores y analiza cómo se estructuran las oraciones.",
            actionTextPending = "Marcar lectura completada",
            actionTextDone = "¡Lectura completada!",
            icon = Icons.Default.MenuBook,
            accentColor = Color(0xFF38BDF8)
        ),
        DailyGoalItem(
            id = "goal_speaking",
            stepLabel = "Paso 3",
            categoryLabel = "Hablar",
            title = "Objetivo 3: Producción Oral",
            description = "Practica shadowing (imitar audios) y haz un diario reflexivo en voz alta sobre tu día.",
            actionTextPending = "Marcar práctica oral completada",
            actionTextDone = "¡Práctica oral completada!",
            icon = Icons.Default.RecordVoiceOver,
            accentColor = Color(0xFFA855F7)
        ),
        DailyGoalItem(
            id = "goal_conversation",
            stepLabel = "Paso 4",
            categoryLabel = "Conversación",
            title = "Objetivo 4: Conversación en Vivo",
            description = "Conéctate con Gemini Live. Ten una charla fluida sobre el vocabulario o tema que aprendiste hoy y responde a sus preguntas de forma natural.",
            actionTextPending = "Marcar conversación completada",
            actionTextDone = "¡Charla superada!",
            icon = Icons.Default.Forum,
            accentColor = Color(0xFF10B981)
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
 * Bandeja flotante modal: "Tus Objetivos de Hoy" / "Ruta de Aprendizaje".
 * Organiza los 4 pasos diarios de aprendizaje a ritmo propio sin presión del reloj.
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

    val completedCount = completedMap.values.count { it }
    val totalGoals = DailyGoalsData.goals.size
    val progressRatio = if (totalGoals > 0) completedCount.toFloat() / totalGoals.toFloat() else 0f

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
                    .fillMaxHeight(0.80f)
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
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Encabezado General: Tus Objetivos de Hoy / Ruta de Aprendizaje
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
                                    text = "Ruta de Aprendizaje • A tu propio ritmo",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF94A3B8)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicador de avance y progreso diario
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (completedCount == totalGoals) "¡Todos los objetivos de hoy logrados! 🎉"
                                else "$completedCount de $totalGoals pasos completados",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (completedCount == totalGoals) Color(0xFF34D399) else Color(0xFF94A3B8)
                                )
                            )
                            Text(
                                text = "${(progressRatio * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completedCount == totalGoals) Color(0xFF34D399) else Color(0xFF38BDF8)
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (completedCount == totalGoals) Color(0xFF34D399) else Color(0xFF38BDF8),
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista desplazable con los 4 bloques de objetivos
                    Column(
                        modifier = Modifier
                            .weight(1f)
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
 * Tarjeta de cada uno de los 4 objetivos de aprendizaje.
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Fila superior: Ícono, Título y Etiqueta de Paso / Logro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = goal.categoryLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = goal.accentColor,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = Color(0xFFF8FAFC)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                // Etiqueta del Paso (ej: "Paso 1", "Paso 2") o "Logrado ✓"
                if (isDone) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFF10B981).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "LOGRADO ✓",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                color = Color(0xFF34D399)
                            )
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(
                                color = goal.accentColor.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = goal.accentColor.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = goal.stepLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                color = goal.accentColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción clara del objetivo
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Check de acción interactivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("btn_toggle_${goal.id}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isDone) "Completado" else "Pendiente",
                        tint = if (isDone) Color(0xFF34D399) else Color(0xFF64748B),
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = if (isDone) goal.actionTextDone else goal.actionTextPending,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = if (isDone) Color(0xFF34D399) else Color(0xFFCBD5E1)
                        )
                    )
                }

                Text(
                    text = if (isDone) "Completado" else "Tocar para marcar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDone) Color(0xFF10B981) else Color(0xFF64748B)
                    )
                )
            }
        }
    }
}
