package com.example.presentation.schedule

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Representa uno de los 3 bloques de aprendizaje diario.
 */
data class ScheduleBlockItem(
    val id: String,
    val title: String,
    val timeLabel: String,
    val startHour: Int,
    val endHour: Int,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

enum class BlockLiveStatus {
    ACTIVE_NOW,
    UPCOMING_NEXT,
    COMPLETED_PAST,
    FUTURE
}

object DailyScheduleData {
    val blocks = listOf(
        ScheduleBlockItem(
            id = "slot_morning",
            title = "Primer Horario • Mañana",
            timeLabel = "9:00 AM – 10:00 AM",
            startHour = 9,
            endHour = 10,
            description = "1 hora de escucha activa, fijación de vocabulario y repetición en voz alta.",
            icon = Icons.Default.WbSunny,
            accentColor = Color(0xFFF59E0B)
        ),
        ScheduleBlockItem(
            id = "slot_afternoon",
            title = "Segundo Horario • Tarde",
            timeLabel = "2:00 PM – 3:00 PM",
            startHour = 14,
            endHour = 15,
            description = "1 hora de técnica de sombras (shadowing), conectores y construcción de oraciones.",
            icon = Icons.Default.LightMode,
            accentColor = Color(0xFF38BDF8)
        ),
        ScheduleBlockItem(
            id = "slot_evening",
            title = "Tercer Horario • Noche",
            timeLabel = "6:00 PM – 7:00 PM",
            startHour = 18,
            endHour = 19,
            description = "1 hora de producción hablada, repaso general y diario reflexivo del día.",
            icon = Icons.Default.NightsStay,
            accentColor = Color(0xFFA855F7)
        )
    )
}

/**
 * Gestor ligero de persistencia para el checklist diario de los 3 bloques.
 */
class ScheduleStorage(context: Context) {
    private val prefs = context.getSharedPreferences("daily_study_schedule_prefs", Context.MODE_PRIVATE)

    fun isBlockDoneToday(dateKey: String, blockId: String): Boolean {
        return prefs.getBoolean("${dateKey}_$blockId", false)
    }

    fun toggleBlockDoneToday(dateKey: String, blockId: String): Boolean {
        val current = isBlockDoneToday(dateKey, blockId)
        val newState = !current
        prefs.edit().putBoolean("${dateKey}_$blockId", newState).apply()
        return newState
    }
}

/**
 * Bandeja flotante modal del horario diario de aprendizaje (3 bloques: 9-10 AM, 2-3 PM, 6-7 PM).
 * Sirve como guía de estudio diario centrado en pantalla.
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

    // Fecha y hora actual en Perú
    val peruZone = remember { ZoneId.of("America/Lima") }
    val nowInPeru = remember(isOpen) { ZonedDateTime.now(peruZone) }
    val currentDateKey = remember(nowInPeru) {
        nowInPeru.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
    }
    val currentHour = nowInPeru.hour
    val currentMinute = nowInPeru.minute

    // Estado reactivo de las casillas completadas hoy
    val completedMap = remember(currentDateKey) {
        mutableStateMapOf<String, Boolean>().apply {
            DailyScheduleData.blocks.forEach { b ->
                put(b.id, storage.isBlockDoneToday(currentDateKey, b.id))
            }
        }
    }

    val completedCount = completedMap.values.count { it }

    // Pulsación suave para el bloque activo en tiempo real
    val infiniteTransition = rememberInfiniteTransition(label = "active_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

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
                    .fillMaxHeight(0.68f)
                    .clickable(enabled = false) {}
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.65f),
                                Color(0xFFF59E0B).copy(alpha = 0.45f),
                                Color(0xFFA855F7).copy(alpha = 0.65f)
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
                    // Barra superior: Título, Hora actual en Perú y Botón Cerrar
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
                                    .size(38.dp)
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
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Reloj horario",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Horario Diario de Estudio",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFFF8FAFC)
                                    )
                                )
                                Text(
                                    text = "Guía de 3 horas • 1h por bloque",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
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
                                contentDescription = "Cerrar horario",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Barra de progreso diaria
                    Column(modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { completedCount / 3f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF34D399),
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista desplazable con los 3 bloques solicitados
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DailyScheduleData.blocks.forEach { block ->
                            val isDone = completedMap[block.id] == true

                            // Calcular estado en vivo respecto a la hora actual de Perú
                            val liveStatus = when {
                                currentHour == block.startHour -> BlockLiveStatus.ACTIVE_NOW
                                currentHour < block.startHour &&
                                    DailyScheduleData.blocks.filter { it.startHour < block.startHour }.all { currentHour >= it.endHour } -> BlockLiveStatus.UPCOMING_NEXT
                                currentHour >= block.endHour -> BlockLiveStatus.COMPLETED_PAST
                                else -> BlockLiveStatus.FUTURE
                            }

                            ScheduleBlockCard(
                                block = block,
                                isDone = isDone,
                                liveStatus = liveStatus,
                                pulseGlow = pulseGlow,
                                onToggleDone = {
                                    val newState = storage.toggleBlockDoneToday(currentDateKey, block.id)
                                    completedMap[block.id] = newState
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
 * Tarjeta individual para cada uno de los 3 bloques de horario.
 */
@Composable
private fun ScheduleBlockCard(
    block: ScheduleBlockItem,
    isDone: Boolean,
    liveStatus: BlockLiveStatus,
    pulseGlow: Float,
    onToggleDone: () -> Unit
) {
    val isActiveNow = liveStatus == BlockLiveStatus.ACTIVE_NOW

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(block.id)
            .border(
                width = if (isActiveNow) 1.5.dp else 1.dp,
                color = when {
                    isActiveNow -> block.accentColor.copy(alpha = 0.6f + 0.4f * pulseGlow)
                    isDone -> Color(0xFF10B981).copy(alpha = 0.6f)
                    else -> Color(0xFF1E293B)
                },
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isActiveNow -> Color(0xFF131D31)
                isDone -> Color(0xFF0C1D1B).copy(alpha = 0.7f)
                else -> Color(0xFF0F1829)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Icono e información de hora
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = block.accentColor.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = block.accentColor.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = block.icon,
                            contentDescription = block.title,
                            tint = block.accentColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Column {
                        Text(
                            text = block.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = block.accentColor
                            )
                        )
                        Text(
                            text = block.timeLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFFF8FAFC)
                            )
                        )
                    }
                }

                // Insignia de estado en vivo
                when {
                    isActiveNow -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFEF4444).copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFEF4444).copy(alpha = pulseGlow),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "EN CURSO ⚡",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFFCA5A5)
                                )
                            )
                        }
                    }
                    liveStatus == BlockLiveStatus.UPCOMING_NEXT -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF38BDF8).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "PRÓXIMO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF7DD3FC)
                                )
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF1E293B).copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.5.dp)
                        ) {
                            Text(
                                text = "1 HORA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Descripción del enfoque del bloque
            Text(
                text = block.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(9.dp))

            // Botón interactivo para marcar la hora realizada hoy
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
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_toggle_${block.id}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isDone) "Completado" else "Pendiente",
                        tint = if (isDone) Color(0xFF34D399) else Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isDone) "¡1 hora completada hoy!" else "Marcar 1h completada",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = if (isDone) Color(0xFF34D399) else Color(0xFFCBD5E1)
                        )
                    )
                }

                Text(
                    text = if (isDone) "Completado" else "Tocar para marcar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = if (isDone) Color(0xFF10B981) else Color(0xFF64748B)
                    )
                )
            }
        }
    }
}
