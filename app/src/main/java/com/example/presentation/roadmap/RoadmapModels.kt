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
            hoursSummary = "2,809+ Palabras • Niveles A1 a C2",
            milestones = listOf(
                MilestoneItem(
                    id = "m1",
                    date = "21 OCT 2026",
                    title = "El Despegue",
                    description = "Primeras palabras y combinación inicial en el lienzo.",
                    iconType = "rocket",
                    isCompleted = true
                ),
                MilestoneItem(
                    id = "m2",
                    date = "21 NOV 2026",
                    title = "Mes 1",
                    description = "Primeras combinaciones y frases de supervivencia.",
                    iconType = "check",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m3",
                    date = "21 DIC 2026",
                    title = "Mes 2",
                    description = "Habla cotidiana sin depender del español.",
                    iconType = "eye_off",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m4",
                    date = "21 ENE 2027",
                    title = "Mes 3",
                    description = "Uso de conectores y oraciones estructuradas.",
                    iconType = "globe",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m5",
                    date = "21 FEB 2027",
                    title = "Mes 4",
                    description = "Respuestas rápidas y conversación continua.",
                    iconType = "bolt",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m6",
                    date = "21 MAR 2027",
                    title = "Mes 5",
                    description = "Expresión espontánea y narración de historias.",
                    iconType = "fire",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m7",
                    date = "21 ABR 2027",
                    title = "Mes 6",
                    description = "Fluidez operativa y autocorrección al hablar.",
                    iconType = "gear",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m8",
                    date = "21 MAY 2027",
                    title = "Mes 7",
                    description = "Dominio de toda la lista y comprensión total.",
                    iconType = "star",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m9",
                    date = "17 JUN 2027",
                    title = "Meta 2,809 Palabras",
                    description = "Cobertura total del vocabulario base de la app.",
                    iconType = "target",
                    isCompleted = false
                ),
                MilestoneItem(
                    id = "m10",
                    date = "21 JUN 2027",
                    title = "Graduación C2",
                    description = "Maestría bilingüe y conversación fluida nativa.",
                    iconType = "school",
                    isCompleted = false
                )
            ),
            routines = listOf(
                RoutineItem(
                    id = "r1",
                    iconType = "sun",
                    title = "MAÑANA - Construcción y Sintaxis",
                    description = "Lienzo de palabras + Reflejo en Voz Alta y combinación activa"
                ),
                RoutineItem(
                    id = "r2",
                    iconType = "cloud_sun",
                    title = "TARDE - Oído Real y Velocidad",
                    description = "Escucha activa, velocímetro para identificar vocabulario clave"
                ),
                RoutineItem(
                    id = "r3",
                    iconType = "moon",
                    title = "NOCHE - Expansión y Fluidez",
                    description = "Dominar nuevas palabras (#2810+) y responder rápido sin traducir mentalmente"
                )
            ),
            footerText = "Fuerza Interna 95% / Reflejo en Vivo 5%"
        )
    }
}
