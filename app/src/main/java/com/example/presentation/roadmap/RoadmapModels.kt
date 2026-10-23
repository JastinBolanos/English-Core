package com.example.presentation.roadmap

data class MilestoneItem(
    val id: String,
    val date: String,
    val title: String,
    val description: String,
    val iconType: String,
    val isCompleted: Boolean = false
)

data class RoutineItem(
    val id: String,
    val iconType: String,
    val title: String,
    val description: String
)

data class RoadmapConfig(
    val missionTitle: String,
    val dateRange: String,
    val hoursSummary: String,
    val milestones: List<MilestoneItem>,
    val routines: List<RoutineItem>,
    val footerText: String
)

object DefaultRoadmapData {
    fun getDefault(): RoadmapConfig {
        return RoadmapConfig(
            missionTitle = "MISIÓN: INGLÉS AVANZADO",
            dateRange = "21 OCT 2026 – 21 JUN 2027",
            hoursSummary = "3 HORAS DIARIAS • 732 HORAS TOTALES",
            milestones = listOf(
                MilestoneItem(
                    id = "m1",
                    date = "21 OCT 2026",
                    title = "DÍA 1: El Despegue",
                    description = "3h, primeras palabras",
                    iconType = "rocket",
                    isCompleted = true
                ),
                MilestoneItem(
                    id = "m2",
                    date = "21 NOV 2026",
                    title = "MES 1",
                    description = "96h, 900 palabras y ruta de colores",
                    iconType = "check",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m3",
                    date = "21 DIC 2026",
                    title = "MES 2",
                    description = "186h, independencia del español",
                    iconType = "eye_off",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m4",
                    date = "21 ENE 2027",
                    title = "MES 3",
                    description = "279h, cobertura total 100%, nivel B1",
                    iconType = "globe",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m5",
                    date = "21 FEB 2027",
                    title = "MES 4",
                    description = "372h, oído y velocidad de respuesta <2s",
                    iconType = "bolt",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m6",
                    date = "21 MAR 2027",
                    title = "MES 5",
                    description = "456h, fluidez real, custom words #2810+",
                    iconType = "fire",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m7",
                    date = "21 ABR 2027",
                    title = "MES 6",
                    description = "549h, automatización y autocorrección",
                    iconType = "gear",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m8",
                    date = "21 MAY 2027",
                    title = "MES 7",
                    description = "639h, comprensión >95% y fluidez",
                    iconType = "star",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m9",
                    date = "17 JUN 2027",
                    title = "META 720h",
                    description = "Umbral de nivel avanzado C1",
                    iconType = "target",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m10",
                    date = "21 JUN 2027",
                    title = "MES 8 / GRADUACIÓN",
                    description = "Nivel C1 logrado",
                    iconType = "school",
                    isCompleted = false
                )
            ),
            routines = listOf(
                RoutineItem(
                    id = "r1",
                    iconType = "sun",
                    title = "MAÑANA (1h) - Construcción y Sintaxis",
                    description = "45 min Lienzo + 15 min Reflejo en Voz Alta"
                ),
                RoutineItem(
                    id = "r2",
                    iconType = "cloud_sun",
                    title = "TARDE (1h) - Oído Real y Velocidad",
                    description = "Ver videos, usar velocímetro para buscar palabras clave"
                ),
                RoutineItem(
                    id = "r3",
                    iconType = "moon",
                    title = "NOCHE (1h) - Expansión y Velocidad",
                    description = "Añadir palabras nuevas (#2810+) y responder rápido sin pensar"
                )
            ),
            footerText = "Fuerza Interna 95% / Reflejo en Vivo 5%"
        )
    }
}
