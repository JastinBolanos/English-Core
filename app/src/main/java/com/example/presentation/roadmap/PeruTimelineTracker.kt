package com.example.presentation.roadmap

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Modelo de datos con la información calculada en tiempo real
 * según la zona horaria oficial de Perú (America/Lima, UTC-5).
 */
data class PeruTimelineStatus(
    val peruDateFormatted: String,          // Ej: "21 DE OCTUBRE DE 2026"
    val peruShortDate: String,              // Ej: "21 OCT 2026"
    val peruTimeFormatted: String,          // Ej: "21:58"
    val dayNumber: Int,                     // Ej: 1 (Día 1 de la misión)
    val totalDays: Int,                     // 244 días totales
    val totalHoursEstimated: Int,           // Horas acumuladas (día * 3h)
    val totalGoalHours: Int,                // 732 horas totales
    val activeMilestoneIndex: Int,          // Índice del hito activo actual (0 para Día 1)
    val activeMilestoneTitle: String,       // Ej: "DÍA 1: El Despegue"
    val daysRemainingToMilestones: List<Int> // Días restantes hacia cada hito
)

object PeruTimelineTracker {
    val PERU_ZONE: TimeZone = TimeZone.getTimeZone("America/Lima")

    /**
     * Calcula la posición exacta del usuario en la línea de tiempo de 8 meses
     * basándose en la fecha y hora oficial de Perú.
     */
    fun calculateStatus(milestones: List<MilestoneItem>): PeruTimelineStatus {
        val now = Calendar.getInstance(PERU_ZONE)

        val peruLocale = Locale.forLanguageTag("es-PE")
        val longDateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", peruLocale).apply {
            timeZone = PERU_ZONE
        }
        val shortDateFormat = SimpleDateFormat("dd MMM yyyy", peruLocale).apply {
            timeZone = PERU_ZONE
        }
        val timeFormat = SimpleDateFormat("HH:mm", peruLocale).apply {
            timeZone = PERU_ZONE
        }

        val peruDateFormatted = longDateFormat.format(now.time).uppercase(peruLocale)
        val peruShortDate = shortDateFormat.format(now.time).uppercase(peruLocale)
        val peruTimeFormatted = timeFormat.format(now.time)

        // Fecha de inicio de la misión de 8 meses: 21 OCT 2026 a las 00:00:00
        val startCal = Calendar.getInstance(PERU_ZONE).apply {
            set(2026, Calendar.OCTOBER, 21, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Fecha de fin de la misión (Graduación C1): 21 JUN 2027 a las 23:59:59
        val endCal = Calendar.getInstance(PERU_ZONE).apply {
            set(2027, Calendar.JUNE, 21, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }

        // Medianoche de hoy en Perú
        val todayMidnight = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Cálculo de días transcurridos desde el inicio
        val diffFromStartMillis = todayMidnight.timeInMillis - startCal.timeInMillis
        val daysElapsed = (diffFromStartMillis / (1000L * 60 * 60 * 24)).toInt()

        val totalDaysMillis = endCal.timeInMillis - startCal.timeInMillis
        val totalDays = (totalDaysMillis / (1000L * 60 * 60 * 24)).toInt() + 1 // 244 días

        // Si hoy es 21 OCT 2026, daysElapsed == 0 -> día 1
        val dayNumber = when {
            daysElapsed <= 0 -> 1
            daysElapsed >= totalDays -> totalDays
            else -> daysElapsed + 1
        }

        val totalHoursEstimated = dayNumber * 3

        // Parsear cada hito para calcular días restantes y determinar cuál es el hito activo
        val daysRemainingList = mutableListOf<Int>()
        val parsedDates = milestones.map { item ->
            val mCal = parseDateString(item.date, PERU_ZONE)
            if (mCal != null) {
                val diffDays = ((mCal.timeInMillis - todayMidnight.timeInMillis) / (1000L * 60 * 60 * 24)).toInt()
                daysRemainingList.add(diffDays)
            } else {
                daysRemainingList.add(0)
            }
            mCal
        }

        // Encontrar el hito activo actual:
        // Si hoy es <= fecha de inicio (21 OCT 2026), el hito 0 (Día 1: El Despegue) está activo.
        var activeIndex = 0
        if (daysElapsed > 0) {
            for (i in milestones.indices) {
                val mCal = parsedDates[i]
                if (mCal != null && !todayMidnight.before(mCal)) {
                    activeIndex = i
                }
            }
        }

        val activeTitle = milestones.getOrNull(activeIndex)?.title ?: "DÍA 1: El Despegue"

        return PeruTimelineStatus(
            peruDateFormatted = peruDateFormatted,
            peruShortDate = peruShortDate,
            peruTimeFormatted = peruTimeFormatted,
            dayNumber = dayNumber,
            totalDays = totalDays,
            totalHoursEstimated = totalHoursEstimated,
            totalGoalHours = 732,
            activeMilestoneIndex = activeIndex,
            activeMilestoneTitle = activeTitle,
            daysRemainingToMilestones = daysRemainingList
        )
    }

    private fun parseDateString(dateStr: String, timeZone: TimeZone): Calendar? {
        val tokens = dateStr.trim().split("\\s+".toRegex())
        if (tokens.size < 3) return null

        val day = tokens[0].toIntOrNull() ?: return null
        val monthStr = tokens[1].uppercase(Locale.ROOT)
        val year = tokens[2].toIntOrNull() ?: return null

        val month = when (monthStr) {
            "ENE" -> Calendar.JANUARY
            "FEB" -> Calendar.FEBRUARY
            "MAR" -> Calendar.MARCH
            "ABR" -> Calendar.APRIL
            "MAY" -> Calendar.MAY
            "JUN" -> Calendar.JUNE
            "JUL" -> Calendar.JULY
            "AGO" -> Calendar.AUGUST
            "SET", "SEP" -> Calendar.SEPTEMBER
            "OCT" -> Calendar.OCTOBER
            "NOV" -> Calendar.NOVEMBER
            "DIC" -> Calendar.DECEMBER
            else -> return null
        }

        return Calendar.getInstance(timeZone).apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    /**
     * Genera la lista completa de los 244 días en fila de los 8 meses (21 OCT 2026 - 21 JUN 2027),
     * sin hitos intermedios y únicamente con los hitos finales de cada mes.
     */
    fun generateAllDays(
        milestones: List<MilestoneItem>,
        completedDays: Set<Int>,
        currentDayNumber: Int
    ): List<DayTimelineItem> {
        val peruLocale = Locale.forLanguageTag("es-PE")
        val shortFormat = SimpleDateFormat("dd MMM", peruLocale).apply { timeZone = PERU_ZONE }
        val fullFormat = SimpleDateFormat("dd MMM yyyy", peruLocale).apply { timeZone = PERU_ZONE }
        val dayOfWeekFormat = SimpleDateFormat("EEE", peruLocale).apply { timeZone = PERU_ZONE }

        val cal = Calendar.getInstance(PERU_ZONE).apply {
            set(2026, Calendar.OCTOBER, 21, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val items = ArrayList<DayTimelineItem>(244)

        for (dayNumber in 1..244) {
            val dateStr = fullFormat.format(cal.time).uppercase(peruLocale)
            val shortDate = shortFormat.format(cal.time).uppercase(peruLocale)
            val dow = dayOfWeekFormat.format(cal.time).replace(".", "").replaceFirstChar { it.uppercase(peruLocale) }

            val monthNumber = when (dayNumber) {
                in 1..32 -> 1
                in 33..62 -> 2
                in 63..93 -> 3
                in 94..124 -> 4
                in 125..152 -> 5
                in 153..183 -> 6
                in 184..213 -> 7
                else -> 8
            }

            val monthLabel = when (monthNumber) {
                1 -> "MES 1: El Despegue y Ruta de Colores (Días 1–32)"
                2 -> "MES 2: Independencia del Español (Días 33–62)"
                3 -> "MES 3: Cobertura B1 100% (Días 63–93)"
                4 -> "MES 4: Velocidad de Respuesta <2s (Días 94–124)"
                5 -> "MES 5: Fluidez Real y Custom Words (Días 125–152)"
                6 -> "MES 6: Automatización y Autocorrección (Días 153–183)"
                7 -> "MES 7: Comprensión >95% y Fluidez (Días 184–213)"
                else -> "MES 8: Umbral C1 y Graduación (Días 214–244)"
            }

            val isStart = false
            val isMonthEnd = when (dayNumber) {
                32, 62, 93, 124, 152, 183, 213, 240, 244 -> true
                else -> false
            }

            val milestoneItem: MilestoneItem? = when (dayNumber) {
                32 -> milestones.getOrNull(1)
                62 -> milestones.getOrNull(2)
                93 -> milestones.getOrNull(3)
                124 -> milestones.getOrNull(4)
                152 -> milestones.getOrNull(5)
                183 -> milestones.getOrNull(6)
                213 -> milestones.getOrNull(7)
                240 -> milestones.getOrNull(8)
                244 -> milestones.getOrNull(9)
                else -> null
            }

            val isPast = dayNumber < currentDayNumber
            val isToday = dayNumber == currentDayNumber
            val isFuture = dayNumber > currentDayNumber
            val isCompleted = isPast

            items.add(
                DayTimelineItem(
                    dayNumber = dayNumber,
                    dateString = dateStr,
                    shortDateFormatted = shortDate,
                    dayOfWeek = dow,
                    monthNumber = monthNumber,
                    monthLabel = monthLabel,
                    isToday = isToday,
                    isPast = isPast,
                    isFuture = isFuture,
                    isCompleted = isCompleted,
                    isMonthEndMilestone = isMonthEnd,
                    isStartMilestone = isStart,
                    milestoneItem = milestoneItem
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return items
    }
}

/**
 * Modelo individual de un día dentro de los 244 días en fila de los 8 meses.
 */
data class DayTimelineItem(
    val dayNumber: Int,
    val dateString: String,
    val shortDateFormatted: String,
    val dayOfWeek: String,
    val monthNumber: Int,
    val monthLabel: String,
    val isToday: Boolean,
    val isPast: Boolean,
    val isFuture: Boolean,
    val isCompleted: Boolean,
    val isMonthEndMilestone: Boolean,
    val isStartMilestone: Boolean,
    val milestoneItem: MilestoneItem?
)
