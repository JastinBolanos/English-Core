package com.example.presentation.roadmap

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Bandeja Flotante para la Ruta de 8 Meses para lograr el inglés avanzado (C1).
 * Reproduce todos los datos de la hoja de ruta con dibujos regentes minimalistas,
 * línea de tiempo conectada, rutina diaria y modo de edición persistente activable
 * desde el lápiz en la esquina superior.
 */
@Composable
fun FloatingRoadmap8MonthsTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storage = remember(context) { RoadmapStorage(context) }
    var config by remember { mutableStateOf(storage.loadRoadmap()) }

    // Recargar al abrir
    LaunchedEffect(isOpen) {
        if (isOpen) {
            config = storage.loadRoadmap()
        }
    }

    if (isOpen) {
        BackHandler {
            onClose()
        }
    }

    val animAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "roadmap_alpha"
    )

    val animScale by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0.94f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "roadmap_scale"
    )

    val isFullyClosed = !isOpen && animAlpha <= 0.01f
    val configuration = LocalConfiguration.current
    val trayHeight = (configuration.screenHeightDp * 0.65f).dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (isFullyClosed) Modifier.offset(x = 10000.dp) else Modifier)
            .graphicsLayer { alpha = animAlpha }
    ) {
        // Fondo semi-transparente para dar foco
        if (animAlpha > 0.05f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = animAlpha * 0.65f }
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = isOpen
                    ) {
                        onClose()
                    }
            )
        }

        // Tarjeta flotante principal con estética Slate/Cyberpunk oscura
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(trayHeight)
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = animAlpha
                    scaleX = animScale
                    scaleY = animScale
                }
                .testTag("floating_roadmap_8months_tray"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B132B),
            border = BorderStroke(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Barra Superior de la Bandeja Flotante (sin recargar ni lápiz de editar)
                RoadmapTopBar(
                    onClose = onClose
                )

                HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)

                // Vista Oficial: Todos los datos con los 244 días en fila y los hitos finales de cada mes
                RoadmapViewContent(
                    config = config,
                    onToggleMilestone = { /* no-op automático */ }
                )
            }
        }
    }
}

/**
 * Barra superior limpia con botón de volver y título
 */
@Composable
private fun RoadmapTopBar(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(32.dp)
                .testTag("btn_close_roadmap_tray")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Vista de lectura completa de la imagen con dibujos regentes minimalistas,
 * todos los 244 días de los 8 meses en fila y los hitos finales de cada mes.
 */
@Composable
private fun RoadmapViewContent(
    config: RoadmapConfig,
    onToggleMilestone: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val peruStatus = remember(config.milestones) { PeruTimelineTracker.calculateStatus(config.milestones) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ENCABEZADO DE LA MISIÓN (Estilo idéntico a la imagen)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = config.missionTitle,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 0.8.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFF8FAFC)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = config.dateRange,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFCBD5E1)
            )
            if (config.hoursSummary.isNotBlank() && !config.hoursSummary.contains("3 HORAS", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = config.hoursSummary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF38BDF8)
                )
            }
        }

        // LÍNEA DE TIEMPO CON TODOS LOS DÍAS EN FILA Y LOS HITOS FINALES DE CADA MES
        TimelineSection(
            milestones = config.milestones,
            peruStatus = peruStatus
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

/**
 * Sección de Línea de tiempo vertical con los 244 días en fila de los 8 meses,
 * sin hitos intermedios y dejando los hitos finales de cada mes.
 */
@Composable
private fun TimelineSection(
    milestones: List<MilestoneItem>,
    peruStatus: PeruTimelineStatus
) {
    var selectedMonthFilter by remember { mutableStateOf<Int?>(null) } // null = Todos los 244 días en fila
    val allDays = remember(milestones, peruStatus.dayNumber) {
        PeruTimelineTracker.generateAllDays(milestones, emptySet(), peruStatus.dayNumber)
    }

    val displayedDays = remember(allDays, selectedMonthFilter) {
        if (selectedMonthFilter == null) allDays else allDays.filter { it.monthNumber == selectedMonthFilter }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_active_milestone")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_active_glow"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Selector rápido de meses para recorrer los 244 días
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedMonthFilter == null,
                    onClick = { selectedMonthFilter = null },
                    label = { Text("Todos (244 días)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF131D33),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = BorderStroke(1.dp, if (selectedMonthFilter == null) Color(0xFF38BDF8) else Color(0xFF334155))
                )
            }
            items(8) { idx ->
                val monthNum = idx + 1
                val isSelected = selectedMonthFilter == monthNum
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedMonthFilter = monthNum },
                    label = { Text("Mes $monthNum", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF131D33),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                )
            }
        }

        // Resumen de días completados automáticamente por avance de fecha
        val autoCompletedDaysCount = (peruStatus.dayNumber - 1).coerceIn(0, 244)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Días practicados: $autoCompletedDaysCount de 244",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCBD5E1)
            )
        }

        // Lista de días en fila
        var lastSeenMonth = -1
        displayedDays.forEachIndexed { index, dayItem ->
            val showMonthHeader = dayItem.monthNumber != lastSeenMonth
            if (showMonthHeader) {
                lastSeenMonth = dayItem.monthNumber
                MonthSectionHeader(
                    monthLabel = dayItem.monthLabel,
                    monthDays = displayedDays.filter { it.monthNumber == dayItem.monthNumber }
                )
            }

            val isLastInView = index == displayedDays.lastIndex

            if (dayItem.isMonthEndMilestone && dayItem.milestoneItem != null) {
                // HITO FINAL DEL MES (o Meta 720h / Graduación)
                MonthEndMilestoneCard(
                    dayItem = dayItem,
                    milestone = dayItem.milestoneItem,
                    isLast = isLastInView,
                    pulseGlow = pulseGlow
                )
            } else {
                // DÍA REGULAR EN FILA (Sin hitos, limpio y conectado - Automático y no editable)
                RegularDayTimelineRow(
                    dayItem = dayItem,
                    isLast = isLastInView,
                    pulseGlow = pulseGlow
                )
            }
        }
    }
}

/**
 * Encabezado de mes para separar la fila de días
 */
@Composable
private fun MonthSectionHeader(
    monthLabel: String,
    monthDays: List<DayTimelineItem>
) {
    val completedInMonth = monthDays.count { it.isPast }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.8f),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthLabel,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            Text(
                text = "$completedInMonth/${monthDays.size} días",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * Fila de un día regular en fila (sin hitos intermedios).
 * Automático:
 * - Días pasados: check verde automático.
 * - Día presente: azul sólido, no vacío.
 * - Días futuros: vacíos (anillo gris oscuro sin rellenar).
 * - No editable por el usuario.
 */
@Composable
private fun RegularDayTimelineRow(
    dayItem: DayTimelineItem,
    isLast: Boolean,
    pulseGlow: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (dayItem.isToday) {
                    Modifier
                        .background(
                            color = Color(0xFF0284C7).copy(alpha = 0.15f * pulseGlow),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF38BDF8).copy(alpha = 0.75f * pulseGlow),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                } else {
                    Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna del nodo con línea conectora vertical
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Nodo del día
            Box(
                modifier = Modifier
                    .size(if (dayItem.isToday) 20.dp else 16.dp)
                    .background(
                        color = when {
                            dayItem.isPast -> Color(0xFF10B981) // Verde para días pasados
                            dayItem.isToday -> Color(0xFF38BDF8) // Azul sólido no vacío para día presente
                            else -> Color(0xFF0F172A) // Vacío para días futuros
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (dayItem.isToday) 1.5.dp else 1.2.dp,
                        color = when {
                            dayItem.isPast -> Color(0xFF10B981)
                            dayItem.isToday -> Color(0xFF38BDF8)
                            else -> Color(0xFF475569) // Anillo gris para días futuros
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (dayItem.isPast) {
                    // Check verde en días pasados
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Día pasado completado",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
                // Si es hoy: queda en azul sólido no vacío sin icono
            }

            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(18.dp)
                ) {
                    drawLine(
                        color = if (dayItem.isPast) Color(0xFF10B981) else Color(0xFF334155),
                        start = Offset(x = size.width / 2f, y = 0f),
                        end = Offset(x = size.width / 2f, y = size.height),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Número de Día
        Text(
            text = "Día ${dayItem.dayNumber}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = when {
                dayItem.isToday -> Color(0xFFF8FAFC)
                dayItem.isPast -> Color(0xFFE2E8F0)
                else -> Color(0xFF64748B)
            },
            modifier = Modifier.width(54.dp)
        )

        // Fecha corta y día de la semana
        Text(
            text = "${dayItem.shortDateFormatted} (${dayItem.dayOfWeek})",
            fontSize = 11.5.sp,
            color = when {
                dayItem.isToday -> Color(0xFF38BDF8)
                dayItem.isPast -> Color(0xFFCBD5E1)
                else -> Color(0xFF64748B)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Tarjeta destacada para el Hito Final de cada Mes
 */
@Composable
private fun MonthEndMilestoneCard(
    dayItem: DayTimelineItem,
    milestone: MilestoneItem,
    isLast: Boolean,
    pulseGlow: Float
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF13203C),
        border = BorderStroke(1.4.dp, if (dayItem.isCompleted) Color(0xFF10B981) else Color(0xFFFBBF24)),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MinimalistDrawing(
                iconType = milestone.iconType,
                isCompleted = dayItem.isCompleted,
                modifier = Modifier.size(36.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🏆 HITO FINAL MES ${dayItem.monthNumber}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.5.sp,
                        color = Color(0xFFFBBF24)
                    )
                    Text(
                        text = "• Día ${dayItem.dayNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = "${dayItem.dateString} | ${milestone.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Color(0xFFF8FAFC)
                )

                Text(
                    text = milestone.description,
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8)
                )
            }

            Icon(
                imageVector = if (dayItem.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (dayItem.isCompleted) Color(0xFF10B981) else Color(0xFF64748B),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Tarjeta individual para la Rutina Diaria (Mañana, Tarde, Noche)
 */
@Composable
private fun RoutineCardItem(routine: RoutineItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF131D33),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono / Dibujo minimalista de la rutina
            MinimalistRoutineDrawing(
                iconType = routine.iconType,
                modifier = Modifier.size(30.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = routine.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = routine.description,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * Pie de página con proporción de Fuerza Interna y Reflejo en Vivo
 */
@Composable
private fun FooterRatioCard(footerText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = footerText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Barra de balance visual minimalista 95% vs 5%
        Row(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight()
                    .background(Color(0xFF38BDF8).copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .weight(0.05f)
                    .fillMaxHeight()
                    .background(Color(0xFFF59E0B))
            )
        }
    }
}

/**
 * DIBUJOS REGENTES MINIMALISTAS
 * Renderizados por Canvas con trazos geométricos puros, limpios y elegantes
 * para cada uno de los 10 hitos de la ruta:
 * rocket, check, eye_off, globe, bolt, fire, gear, star, target, school
 */
@Composable
fun MinimalistDrawing(
    iconType: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val baseColor = if (isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Anillo base exterior sutil
            drawCircle(
                color = if (isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF1E293B),
                radius = radius,
                center = center
            )
            drawCircle(
                color = if (isCompleted) Color(0xFF10B981) else Color(0xFF334155),
                radius = radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Dibujo vectorial minimalista según el tipo
            when (iconType) {
                "rocket" -> drawMinimalistRocket(center, radius)
                "check" -> drawMinimalistCheck(center, radius)
                "eye_off" -> drawMinimalistEyeOff(center, radius)
                "globe" -> drawMinimalistGlobe(center, radius)
                "bolt" -> drawMinimalistBolt(center, radius)
                "fire" -> drawMinimalistFire(center, radius)
                "gear" -> drawMinimalistGear(center, radius)
                "star" -> drawMinimalistStar(center, radius)
                "target" -> drawMinimalistTarget(center, radius)
                "school" -> drawMinimalistCap(center, radius)
                else -> drawMinimalistStar(center, radius)
            }
        }
    }
}

// 1. Cohete minimalista (El Despegue)
private fun DrawScope.drawMinimalistRocket(center: Offset, radius: Float) {
    val r = radius * 0.55f
    val path = Path().apply {
        moveTo(center.x, center.y - r) // Punta
        cubicTo(
            center.x + r * 0.6f, center.y - r * 0.3f,
            center.x + r * 0.5f, center.y + r * 0.5f,
            center.x + r * 0.3f, center.y + r * 0.7f
        )
        lineTo(center.x - r * 0.3f, center.y + r * 0.7f)
        cubicTo(
            center.x - r * 0.5f, center.y + r * 0.5f,
            center.x - r * 0.6f, center.y - r * 0.3f,
            center.x, center.y - r
        )
        close()
    }
    drawPath(path, color = Color(0xFFF43F5E), style = Fill)

    // Fuego de propulsión minimalista
    val flamePath = Path().apply {
        moveTo(center.x - r * 0.2f, center.y + r * 0.7f)
        lineTo(center.x, center.y + r * 1.15f)
        lineTo(center.x + r * 0.2f, center.y + r * 0.7f)
        close()
    }
    drawPath(flamePath, color = Color(0xFFFBBF24), style = Fill)
}

// 2. Check minimalista (Mes 1)
private fun DrawScope.drawMinimalistCheck(center: Offset, radius: Float) {
    val r = radius * 0.5f
    val path = Path().apply {
        moveTo(center.x - r * 0.8f, center.y)
        lineTo(center.x - r * 0.2f, center.y + r * 0.6f)
        lineTo(center.x + r * 0.85f, center.y - r * 0.55f)
    }
    drawPath(
        path,
        color = Color(0xFF22C55E),
        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
    )
}

// 3. Ojo tachado minimalista (Independencia del español)
private fun DrawScope.drawMinimalistEyeOff(center: Offset, radius: Float) {
    val r = radius * 0.6f
    // Contorno del ojo
    val eyePath = Path().apply {
        moveTo(center.x - r, center.y)
        quadraticTo(center.x, center.y - r * 0.6f, center.x + r, center.y)
        quadraticTo(center.x, center.y + r * 0.6f, center.x - r, center.y)
        close()
    }
    drawPath(
        eyePath,
        color = Color(0xFF94A3B8),
        style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
    )
    drawCircle(color = Color(0xFF94A3B8), radius = r * 0.25f, center = center)

    // Línea diagonal que tacha
    drawLine(
        color = Color(0xFFEF4444),
        start = Offset(center.x - r * 0.9f, center.y - r * 0.8f),
        end = Offset(center.x + r * 0.9f, center.y + r * 0.8f),
        strokeWidth = 1.8.dp.toPx(),
        cap = StrokeCap.Round
    )
}

// 4. Globo terráqueo minimalista (Nivel B1)
private fun DrawScope.drawMinimalistGlobe(center: Offset, radius: Float) {
    val r = radius * 0.6f
    drawCircle(
        color = Color(0xFF0284C7),
        radius = r,
        center = center,
        style = Stroke(width = 1.4.dp.toPx())
    )
    // Línea del ecuador
    drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(center.x - r, center.y),
        end = Offset(center.x + r, center.y),
        strokeWidth = 1.2.dp.toPx()
    )
    // Meridiano elíptico
    val meridianPath = Path().apply {
        moveTo(center.x, center.y - r)
        cubicTo(center.x + r * 0.55f, center.y - r * 0.4f, center.x + r * 0.55f, center.y + r * 0.4f, center.x, center.y + r)
        cubicTo(center.x - r * 0.55f, center.y + r * 0.4f, center.x - r * 0.55f, center.y - r * 0.4f, center.x, center.y - r)
    }
    drawPath(
        meridianPath,
        color = Color(0xFF38BDF8),
        style = Stroke(width = 1.2.dp.toPx())
    )
}

// 5. Rayo minimalista (Velocidad < 2s)
private fun DrawScope.drawMinimalistBolt(center: Offset, radius: Float) {
    val r = radius * 0.65f
    val path = Path().apply {
        moveTo(center.x + r * 0.1f, center.y - r)
        lineTo(center.x - r * 0.5f, center.y + r * 0.05f)
        lineTo(center.x - r * 0.05f, center.y + r * 0.05f)
        lineTo(center.x - r * 0.25f, center.y + r)
        lineTo(center.x + r * 0.55f, center.y - r * 0.15f)
        lineTo(center.x + r * 0.1f, center.y - r * 0.15f)
        close()
    }
    drawPath(path, color = Color(0xFFFBBF24), style = Fill)
}

// 6. Llama de fuego minimalista (Fluidez real)
private fun DrawScope.drawMinimalistFire(center: Offset, radius: Float) {
    val r = radius * 0.65f
    val path = Path().apply {
        moveTo(center.x, center.y + r * 0.9f)
        cubicTo(center.x + r * 0.8f, center.y + r * 0.7f, center.x + r * 0.7f, center.y - r * 0.1f, center.x + r * 0.2f, center.y - r * 0.4f)
        cubicTo(center.x + r * 0.35f, center.y - r * 0.2f, center.x + r * 0.35f, center.y - r * 0.1f, center.x, center.y - r)
        cubicTo(center.x - r * 0.5f, center.y - r * 0.4f, center.x - r * 0.8f, center.y + r * 0.2f, center.x - r * 0.5f, center.y + r * 0.7f)
        close()
    }
    drawPath(path, color = Color(0xFFF97316), style = Fill)

    // Núcleo amarillo
    val innerPath = Path().apply {
        moveTo(center.x, center.y + r * 0.85f)
        cubicTo(center.x + r * 0.35f, center.y + r * 0.7f, center.x + r * 0.3f, center.y + r * 0.2f, center.x, center.y - r * 0.2f)
        cubicTo(center.x - r * 0.3f, center.y + r * 0.2f, center.x - r * 0.35f, center.y + r * 0.7f, center.x, center.y + r * 0.85f)
        close()
    }
    drawPath(innerPath, color = Color(0xFFFEF08A), style = Fill)
}

// 7. Engranaje minimalista (Automatización)
private fun DrawScope.drawMinimalistGear(center: Offset, radius: Float) {
    val rOuter = radius * 0.6f
    val rInner = radius * 0.42f
    val rHole = radius * 0.2f
    val numTeeth = 6

    val path = Path()
    for (i in 0 until numTeeth) {
        val angleStep = (2 * Math.PI / numTeeth).toFloat()
        val a1 = i * angleStep
        val a2 = a1 + angleStep * 0.3f
        val a3 = a1 + angleStep * 0.5f
        val a4 = a1 + angleStep * 0.8f

        val p1 = Offset(center.x + rInner * cos(a1), center.y + rInner * sin(a1))
        val p2 = Offset(center.x + rOuter * cos(a2), center.y + rOuter * sin(a2))
        val p3 = Offset(center.x + rOuter * cos(a3), center.y + rOuter * sin(a3))
        val p4 = Offset(center.x + rInner * cos(a4), center.y + rInner * sin(a4))

        if (i == 0) path.moveTo(p1.x, p1.y) else path.lineTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.lineTo(p4.x, p4.y)
    }
    path.close()
    drawPath(path, color = Color(0xFF94A3B8), style = Fill)
    drawCircle(color = Color(0xFF0B132B), radius = rHole, center = center)
}

// 8. Estrella minimalista (Comprensión >95%)
private fun DrawScope.drawMinimalistStar(center: Offset, radius: Float) {
    val rOuter = radius * 0.65f
    val rInner = radius * 0.3f
    val points = 5
    val path = Path()

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) rOuter else rInner
        val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = Color(0xFFFBBF24), style = Fill)
}

// 9. Diana / Meta minimalista (Meta 720h)
private fun DrawScope.drawMinimalistTarget(center: Offset, radius: Float) {
    val r = radius * 0.65f
    drawCircle(
        color = Color(0xFFEF4444),
        radius = r,
        center = center,
        style = Stroke(width = 1.8.dp.toPx())
    )
    drawCircle(
        color = Color(0xFFEF4444),
        radius = r * 0.55f,
        center = center,
        style = Stroke(width = 1.6.dp.toPx())
    )
    drawCircle(
        color = Color(0xFFEF4444),
        radius = r * 0.25f,
        center = center,
        style = Fill
    )
}

// 10. Birrete de graduación minimalista (Mes 8 / Graduación)
private fun DrawScope.drawMinimalistCap(center: Offset, radius: Float) {
    val r = radius * 0.65f
    // Rombo superior del birrete
    val capPath = Path().apply {
        moveTo(center.x, center.y - r * 0.65f)
        lineTo(center.x + r, center.y - r * 0.1f)
        lineTo(center.x, center.y + r * 0.35f)
        lineTo(center.x - r, center.y - r * 0.1f)
        close()
    }
    drawPath(capPath, color = Color(0xFF818CF8), style = Fill)

    // Base redondeada
    val basePath = Path().apply {
        moveTo(center.x - r * 0.55f, center.y + r * 0.15f)
        cubicTo(center.x - r * 0.55f, center.y + r * 0.75f, center.x + r * 0.55f, center.y + r * 0.75f, center.x + r * 0.55f, center.y + r * 0.15f)
        close()
    }
    drawPath(basePath, color = Color(0xFF6366F1), style = Fill)

    // Borla
    drawLine(
        color = Color(0xFFFBBF24),
        start = Offset(center.x + r * 0.7f, center.y - r * 0.05f),
        end = Offset(center.x + r * 0.9f, center.y + r * 0.6f),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Dibujos minimalistas para la rutina (Sol, Nube+Sol, Luna)
 */
@Composable
private fun MinimalistRoutineDrawing(iconType: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f

        when (iconType) {
            "sun" -> {
                // Sol radiante minimalista
                drawCircle(color = Color(0xFFF59E0B), radius = r * 0.45f, center = center)
                val rayCount = 8
                for (i in 0 until rayCount) {
                    val angle = (i * 2 * Math.PI / rayCount).toFloat()
                    val p1 = Offset(center.x + r * 0.62f * cos(angle), center.y + r * 0.62f * sin(angle))
                    val p2 = Offset(center.x + r * 0.88f * cos(angle), center.y + r * 0.88f * sin(angle))
                    drawLine(color = Color(0xFFFBBF24), start = p1, end = p2, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                }
            }
            "cloud_sun" -> {
                // Sol vespertino con nube
                drawCircle(color = Color(0xFFF59E0B), radius = r * 0.38f, center = Offset(center.x + r * 0.3f, center.y - r * 0.25f))
                // Silueta de nube
                val cloudPath = Path().apply {
                    moveTo(center.x - r * 0.6f, center.y + r * 0.5f)
                    lineTo(center.x + r * 0.5f, center.y + r * 0.5f)
                    cubicTo(center.x + r * 0.8f, center.y + r * 0.5f, center.x + r * 0.8f, center.y + r * 0.1f, center.x + r * 0.5f, center.y + r * 0.1f)
                    cubicTo(center.x + r * 0.45f, center.y - r * 0.35f, center.x - r * 0.05f, center.y - r * 0.35f, center.x - r * 0.2f, center.y + r * 0.05f)
                    cubicTo(center.x - r * 0.45f, center.y + r * 0.05f, center.x - r * 0.7f, center.y + r * 0.2f, center.x - r * 0.6f, center.y + r * 0.5f)
                    close()
                }
                drawPath(cloudPath, color = Color(0xFF94A3B8).copy(alpha = 0.85f), style = Fill)
            }
            "moon" -> {
                // Luna creciente elegante
                val moonPath = Path().apply {
                    moveTo(center.x, center.y - r * 0.75f)
                    cubicTo(center.x + r * 0.85f, center.y - r * 0.4f, center.x + r * 0.85f, center.y + r * 0.4f, center.x, center.y + r * 0.75f)
                    cubicTo(center.x + r * 0.4f, center.y + r * 0.45f, center.x + r * 0.4f, center.y - r * 0.45f, center.x, center.y - r * 0.75f)
                    close()
                }
                drawPath(moonPath, color = Color(0xFF38BDF8), style = Fill)
            }
            else -> {
                drawCircle(color = Color(0xFF38BDF8), radius = r * 0.5f, center = center)
            }
        }
    }
}

/**
 * Editor completo para la Ruta de 8 Meses
 * Permite editar título, fechas, horas, hitos y rutinas, con botones de guardar y cancelar.
 */
@Composable
private fun RoadmapEditorContent(
    draftTitle: String,
    onTitleChange: (String) -> Unit,
    draftDateRange: String,
    onDateRangeChange: (String) -> Unit,
    draftHoursSummary: String,
    onHoursSummaryChange: (String) -> Unit,
    draftMilestones: List<MilestoneItem>,
    onMilestonesChange: (List<MilestoneItem>) -> Unit,
    draftRoutines: List<RoutineItem>,
    onRoutinesChange: (List<RoutineItem>) -> Unit,
    draftFooter: String,
    onFooterChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Modificar Plan y Metas",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF38BDF8)
        )

        // 1. Cabecera
        OutlinedTextField(
            value = draftTitle,
            onValueChange = onTitleChange,
            label = { Text("Título de la Misión") },
            modifier = Modifier.fillMaxWidth(),
            colors = editorTextFieldColors()
        )

        OutlinedTextField(
            value = draftDateRange,
            onValueChange = onDateRangeChange,
            label = { Text("Rango de Fechas (Ej: 21 OCT 2026 – 21 JUN 2027)") },
            modifier = Modifier.fillMaxWidth(),
            colors = editorTextFieldColors()
        )

        OutlinedTextField(
            value = draftHoursSummary,
            onValueChange = onHoursSummaryChange,
            label = { Text("Horas Diarias y Totales") },
            modifier = Modifier.fillMaxWidth(),
            colors = editorTextFieldColors()
        )

        HorizontalDivider(color = Color(0xFF1E293B))

        // 2. Hitos de la Línea de Tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hitos (${draftMilestones.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFF1F5F9)
            )

            TextButton(
                onClick = {
                    val newIndex = draftMilestones.size + 1
                    val newItem = MilestoneItem(
                        id = "m_${System.currentTimeMillis()}",
                        date = "FECHA",
                        title = "NUEVO HITO $newIndex",
                        description = "Descripción y horas estimadas",
                        iconType = "star",
                        isCompleted = false
                    )
                    onMilestonesChange(draftMilestones + newItem)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir hito", fontSize = 12.sp)
            }
        }

        draftMilestones.forEachIndexed { index, milestone ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${index + 1} Hito",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8)
                        )
                        IconButton(
                            onClick = {
                                onMilestonesChange(draftMilestones.filterIndexed { i, _ -> i != index })
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = milestone.date,
                            onValueChange = { newDate ->
                                onMilestonesChange(draftMilestones.mapIndexed { i, m ->
                                    if (i == index) m.copy(date = newDate) else m
                                })
                            },
                            label = { Text("Fecha") },
                            modifier = Modifier.weight(0.45f),
                            colors = editorTextFieldColors()
                        )
                        OutlinedTextField(
                            value = milestone.title,
                            onValueChange = { newTitle ->
                                onMilestonesChange(draftMilestones.mapIndexed { i, m ->
                                    if (i == index) m.copy(title = newTitle) else m
                                })
                            },
                            label = { Text("Título / Mes") },
                            modifier = Modifier.weight(0.55f),
                            colors = editorTextFieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = milestone.description,
                        onValueChange = { newDesc ->
                            onMilestonesChange(draftMilestones.mapIndexed { i, m ->
                                if (i == index) m.copy(description = newDesc) else m
                            })
                        },
                        label = { Text("Detalle de horas / palabras / nivel") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editorTextFieldColors()
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF1E293B))

        // 3. Rutinas Diarias
        Text(
            text = "Rutinas Diarias (${draftRoutines.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFF1F5F9)
        )

        draftRoutines.forEachIndexed { index, routine ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = routine.title,
                        onValueChange = { newTitle ->
                            onRoutinesChange(draftRoutines.mapIndexed { i, r ->
                                if (i == index) r.copy(title = newTitle) else r
                            })
                        },
                        label = { Text("Horario y Enfoque") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editorTextFieldColors()
                    )

                    OutlinedTextField(
                        value = routine.description,
                        onValueChange = { newDesc ->
                            onRoutinesChange(draftRoutines.mapIndexed { i, r ->
                                if (i == index) r.copy(description = newDesc) else r
                            })
                        },
                        label = { Text("Actividad / Ejercicios") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = editorTextFieldColors()
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF1E293B))

        // 4. Pie de página
        OutlinedTextField(
            value = draftFooter,
            onValueChange = onFooterChange,
            label = { Text("Texto de pie (Fuerza Interna / Reflejo)") },
            modifier = Modifier.fillMaxWidth(),
            colors = editorTextFieldColors()
        )

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun editorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF38BDF8),
    unfocusedBorderColor = Color(0xFF334155),
    focusedLabelColor = Color(0xFF38BDF8),
    unfocusedLabelColor = Color(0xFF94A3B8),
    focusedTextColor = Color(0xFFF8FAFC),
    unfocusedTextColor = Color(0xFFE2E8F0)
)
