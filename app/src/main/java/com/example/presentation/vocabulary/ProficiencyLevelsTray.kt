package com.example.presentation.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        targetWords = 2809,
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

    val infiniteTransition = rememberInfiniteTransition(label = "c1_trophy_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val currentLevel = remember(masteredWordsCount) {
        when {
            masteredWordsCount >= 2809 -> CEFR_LEVELS[4]
            masteredWordsCount >= 2400 -> CEFR_LEVELS[4]
            masteredWordsCount >= 1800 -> CEFR_LEVELS[3]
            masteredWordsCount >= 1000 -> CEFR_LEVELS[2]
            masteredWordsCount >= 500 -> CEFR_LEVELS[1]
            else -> CEFR_LEVELS[0]
        }
    }

    val overallProgress = remember(masteredWordsCount) {
        (masteredWordsCount.toFloat() / 2809f).coerceIn(0f, 1f)
    }

    val wordsToC1 = remember(masteredWordsCount) {
        (2809 - masteredWordsCount).coerceAtLeast(0)
    }

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

        // Contenedor flotante centrado estilo Acrílico HUD
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* no-op: interceptar toques */ }
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(22.dp))
                .testTag("proficiency_levels_tray_surface"),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(
                width = 1.5.dp,
                color = if (masteredWordsCount >= 2809) {
                    Color(0xFFFACC15).copy(alpha = 0.85f * pulseGlow)
                } else {
                    Color(0xFF38BDF8).copy(alpha = 0.5f)
                }
            )
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
                            color = Color(0xFFFACC15).copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, Color(0xFFFACC15))
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
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 0.8.sp,
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

                Spacer(modifier = Modifier.height(12.dp))

                // RESUMEN GENERAL: Estado actual y meta C1 (2809+ palabras)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DOMINIO ACTUAL",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "$masteredWordsCount",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = Color(0xFFF8FAFC)
                                    )
                                    Text(
                                        text = "/ 2809+ palabras",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "🏆",
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Badge de nivel actual alcanzado
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = currentLevel.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.2.dp, currentLevel.color)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Nivel ${currentLevel.code}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = currentLevel.color
                                    )
                                }
                            }
                        }

                        // Barra general hacia la meta C1 (2809+)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (masteredWordsCount >= 2809) {
                                        "🎉 ¡Meta C1 Alcanzada (2809+ palabras en dominio)!"
                                    } else {
                                        "Faltan $wordsToC1 palabras para C1"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (masteredWordsCount >= 2809) Color(0xFFFACC15) else Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "${(overallProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF8FAFC)
                                )
                            }

                            LinearProgressIndicator(
                                progress = { overallProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (masteredWordsCount >= 2809) Color(0xFFFACC15) else Color(0xFF38BDF8),
                                trackColor = Color(0xFF0F172A)
                            )
                        }
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
                            currentWords = masteredWordsCount,
                            pulseGlow = pulseGlow
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Tarjeta individual para cada nivel (A1, A2, B1, B2, C1).
 */
@Composable
private fun LevelCard(
    level: ProficiencyLevel,
    currentWords: Int,
    pulseGlow: Float
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
            isCompleted -> Color(0xFF0B2135).copy(alpha = 0.85f)
            isCurrent -> Color(0xFF0F2642).copy(alpha = 0.95f)
            else -> Color(0xFF0F172A).copy(alpha = 0.6f)
        },
        border = BorderStroke(
            width = if (isCurrent) 1.5.dp else 1.dp,
            color = when {
                isCompleted -> level.color.copy(alpha = 0.7f)
                isCurrent -> level.color.copy(alpha = 0.9f * pulseGlow)
                else -> Color(0xFF334155).copy(alpha = 0.4f)
            }
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
                    // Badge con código (A1, A2, B1, B2, C1)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = level.color.copy(alpha = if (isLocked) 0.12f else 0.25f),
                        border = BorderStroke(
                            1.dp,
                            if (isLocked) Color(0xFF475569) else level.color
                        )
                    ) {
                        Text(
                            text = level.code,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (isLocked) Color(0xFF94A3B8) else level.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = level.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isLocked) Color(0xFF94A3B8) else Color(0xFFF8FAFC)
                            )
                            if (level.code == "C1") {
                                Text(text = "👑", fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = level.subtitle,
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Estado (Completado / En curso / Bloqueado)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        isCompleted -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                        isCurrent -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = level.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, level.color)
                            ) {
                                Text(
                                    text = "EN CURSO ⚡",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    color = level.color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                                        fontWeight = FontWeight.SemiBold,
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
                        "Meta: 2809+ palabras en dominio"
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
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (isCompleted) Color(0xFF10B981) else level.color,
                    trackColor = Color(0xFF0F172A)
                )
            }
        }
    }
}
