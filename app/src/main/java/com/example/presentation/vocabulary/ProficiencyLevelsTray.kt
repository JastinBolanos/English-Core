package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modelo de nivel de competencia según el Marco Común Europeo (MCER / CEFR)
 * basado en el número de palabras en dominio (palabras con copa).
 */
data class ProficiencyLevel(
    val code: String,
    val title: String,
    val subtitle: String,
    val minWords: Int,
    val targetWords: Int,
    val color: Color,
    val description: String,
    val badgeIcon: String = "🏆"
)

private val CEFR_LEVELS = listOf(
    ProficiencyLevel(
        code = "A1",
        title = "Principiante",
        subtitle = "Supervivencia Básica",
        minWords = 0,
        targetWords = 500,
        color = Color(0xFF10B981),
        description = "Comprensión de expresiones cotidianas y frases de supervivencia, saludos, presentaciones personales y necesidades inmediatas."
    ),
    ProficiencyLevel(
        code = "A2",
        title = "Elemental",
        subtitle = "Comunicación Cotidiana",
        minWords = 500,
        targetWords = 1000,
        color = Color(0xFF38BDF8),
        description = "Frases y vocabulario habitual sobre familia, lugares, compras y empleo. Capacidad para resolver tareas simples y rutinarias."
    ),
    ProficiencyLevel(
        code = "B1",
        title = "Intermedio",
        subtitle = "Independencia Lingüística",
        minWords = 1000,
        targetWords = 1800,
        color = Color(0xFF6366F1),
        description = "Comprensión de textos estándar de trabajo o estudio. Desenvolvimiento autónomo en viajes, narración de experiencias y planes."
    ),
    ProficiencyLevel(
        code = "B2",
        title = "Intermedio Alto",
        subtitle = "Fluidez Operativa",
        minWords = 1800,
        targetWords = 2400,
        color = Color(0xFFA855F7),
        description = "Comprensión de ideas complejas y debates técnicos. Fluidez espontánea con hablantes nativos sin esfuerzo evidente."
    ),
    ProficiencyLevel(
        code = "C1",
        title = "Avanzado",
        subtitle = "Dominio Total (Meta de 8 Meses)",
        minWords = 2400,
        targetWords = 2811,
        color = Color(0xFFFACC15),
        description = "Dominio operativo eficaz del idioma. Uso flexible y espontáneo en entornos profesionales, sociales y académicos sin buscar expresiones."
    )
)

/**
 * Bandeja flotante que muestra los Niveles de Dominio (CEFR) según el número
 * de palabras dominadas (copas ganadas). Al llegar a 2809+ palabras se alcanza el C1.
 */
@Composable
fun ProficiencyLevelsTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    masteredWordsCount: Int,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    BackHandler { onClose() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("proficiency_levels_tray_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Fondo semi-transparente oscurecido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClose() }
        )

        // Contenedor flotante centrado con diseño limpio y sobrio
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.65f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* no-op: interceptar toques */ }
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(22.dp))
                .testTag("proficiency_levels_tray_surface"),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // CABECERA: Título con trofeo y botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFACC15).copy(alpha = 0.15f),
                            border = null
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFACC15),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "NIVELES DE DOMINIO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFFF8FAFC)
                            )
                            Text(
                                text = "Marco Común Europeo (CEFR)",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("proficiency_tray_btn_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // LISTA DE NIVELES SCROLLEABLE
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CEFR_LEVELS.forEach { level ->
                        LevelCard(
                            level = level,
                            currentWords = masteredWordsCount
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Tarjeta individual limpia para cada nivel (A1, A2, B1, B2, C1).
 */
@Composable
private fun LevelCard(
    level: ProficiencyLevel,
    currentWords: Int
) {
    val isCompleted = currentWords >= level.targetWords
    val isCurrent = currentWords in level.minWords until level.targetWords
    val isLocked = currentWords < level.minWords

    val levelProgress = remember(currentWords, level.minWords, level.targetWords) {
        if (currentWords >= level.targetWords) {
            1f
        } else if (currentWords < level.minWords) {
            0f
        } else {
            val range = (level.targetWords - level.minWords).toFloat()
            if (range > 0f) {
                ((currentWords - level.minWords).toFloat() / range).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
            isCompleted -> Color(0xFF0F1B2E)
            isCurrent -> Color(0xFF132238)
            else -> Color(0xFF0B1324).copy(alpha = 0.7f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isCurrent) level.color.copy(alpha = 0.35f) else Color(0xFF1E293B)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Fila superior: Badge de nivel, título, meta de palabras y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge con código limpio (A1, A2, B1, B2, C1) sin bordes pesados
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = level.color.copy(alpha = if (isLocked) 0.08f else 0.16f),
                        border = null
                    ) {
                        Text(
                            text = level.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isLocked) Color(0xFF64748B) else level.color,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Column {
                        Text(
                            text = level.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isLocked) Color(0xFF94A3B8) else Color(0xFFF8FAFC)
                        )
                        Text(
                            text = level.subtitle,
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Estado sobrio y sin emojis (Completado / En curso / Bloqueado)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        isCompleted -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "SUPERADO",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                        isCurrent -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = level.color.copy(alpha = 0.16f),
                                border = null
                            ) {
                                Text(
                                    text = "EN CURSO",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.5.sp,
                                    color = level.color,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                )
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                                border = null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "PRÓXIMO",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Explicación descriptiva del nivel
            Text(
                text = level.description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = if (isLocked) Color(0xFF64748B) else Color(0xFFCBD5E1)
            )

            // Requisito de palabras y progreso dentro del nivel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (level.code == "C1") {
                        "Meta: 2811+ palabras en dominio"
                    } else {
                        "Rango: ${level.minWords} – ${level.targetWords} palabras"
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCurrent) level.color else Color(0xFF94A3B8)
                )

                Text(
                    text = when {
                        isCompleted -> "${level.targetWords}/${level.targetWords} (100%)"
                        isCurrent -> "$currentWords/${level.targetWords} (${(levelProgress * 100).toInt()}%)"
                        else -> "0/${level.targetWords}"
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color(0xFF10B981) else if (isCurrent) level.color else Color(0xFF64748B)
                )
            }

            // Barra de progreso si está en curso o completado
            if (!isLocked) {
                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp)),
                    color = if (isCompleted) Color(0xFF10B981) else level.color,
                    trackColor = Color(0xFF1E293B)
                )
            }
        }
    }
}
