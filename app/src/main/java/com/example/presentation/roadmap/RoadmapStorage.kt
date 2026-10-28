package com.example.presentation.roadmap

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class RoadmapStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("roadmap_8_months_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONFIG_JSON = "key_roadmap_config_json"
        private const val KEY_COMPLETED_DAYS = "key_completed_days_set"
    }

    fun loadRoadmap(): RoadmapConfig {
        val jsonStr = prefs.getString(KEY_CONFIG_JSON, null)
        if (jsonStr.isNullOrEmpty()) {
            return DefaultRoadmapData.getDefault()
        }
        return try {
            val root = JSONObject(jsonStr)
            val rawMissionTitle = root.optString("missionTitle", "MISIÓN: INGLÉS")
            val missionTitle = if (rawMissionTitle.contains("AVANZADO", ignoreCase = true)) "MISIÓN: INGLÉS" else rawMissionTitle
            val dateRange = root.optString("dateRange", "21 OCT 2026 – 21 JUN 2027")
            val defaultRoadmap = DefaultRoadmapData.getDefault()
            val rawHours = root.optString("hoursSummary", "")
            val hoursSummary = if (rawHours.isEmpty() || rawHours.contains("horas", ignoreCase = true) || rawHours.contains("3 HORAS", ignoreCase = true) || rawHours.contains("C2", ignoreCase = true)) {
                defaultRoadmap.hoursSummary
            } else {
                rawHours
            }
            val footerText = root.optString("footerText", "Fuerza Interna 95% / Reflejo en Vivo 5%")

            val milestones = mutableListOf<MilestoneItem>()
            val milestonesArray = root.optJSONArray("milestones") ?: JSONArray()
            for (i in 0 until milestonesArray.length()) {
                val itemObj = milestonesArray.getJSONObject(i)
                val rawDesc = itemObj.optString("description", "")
                val finalDesc = if (rawDesc == "C1 listo para liberar la app") "Nivel C1 logrado" else rawDesc
                milestones.add(
                    MilestoneItem(
                        id = itemObj.optString("id", "m_$i"),
                        date = itemObj.optString("date", ""),
                        title = itemObj.optString("title", ""),
                        description = finalDesc,
                        iconType = itemObj.optString("iconType", "star"),
                        isCompleted = itemObj.optBoolean("isCompleted", false)
                    )
                )
            }

            // Migración automática: Si los hitos guardados tienen horas legadas, +A1/+A2, C2 o textos largos, actualizar a la nueva ruta concisa
            val needsMigration = milestones.isEmpty() || milestones.any {
                it.description.contains("h,", ignoreCase = true) ||
                it.description.contains("horas", ignoreCase = true) ||
                it.title.contains("720h", ignoreCase = true) ||
                it.description.contains("96h", ignoreCase = true) ||
                it.description.contains("+A1") ||
                it.description.contains("+A2") ||
                it.description.length > 55 ||
                it.title.contains("C2", ignoreCase = true) ||
                it.description.contains("C2", ignoreCase = true)
            }
            val finalMilestones = if (needsMigration) defaultRoadmap.milestones else milestones

            val routines = mutableListOf<RoutineItem>()
            val routinesArray = root.optJSONArray("routines") ?: JSONArray()
            for (i in 0 until routinesArray.length()) {
                val itemObj = routinesArray.getJSONObject(i)
                routines.add(
                    RoutineItem(
                        id = itemObj.optString("id", "r_$i"),
                        iconType = itemObj.optString("iconType", "sun"),
                        title = itemObj.optString("title", ""),
                        description = itemObj.optString("description", "")
                    )
                )
            }
            val finalRoutines = if (routines.isEmpty() || routines.any { it.title.contains("(1h)") }) {
                defaultRoadmap.routines
            } else {
                routines
            }

            RoadmapConfig(
                missionTitle = missionTitle,
                dateRange = dateRange,
                hoursSummary = hoursSummary,
                milestones = finalMilestones,
                routines = finalRoutines,
                footerText = footerText
            )
        } catch (_: Exception) {
            DefaultRoadmapData.getDefault()
        }
    }

    fun saveRoadmap(config: RoadmapConfig) {
        val root = JSONObject()
        root.put("missionTitle", config.missionTitle)
        root.put("dateRange", config.dateRange)
        root.put("hoursSummary", config.hoursSummary)
        root.put("footerText", config.footerText)

        val milestonesArray = JSONArray()
        config.milestones.forEach { m ->
            val mObj = JSONObject()
            mObj.put("id", m.id)
            mObj.put("date", m.date)
            mObj.put("title", m.title)
            mObj.put("description", m.description)
            mObj.put("iconType", m.iconType)
            mObj.put("isCompleted", m.isCompleted)
            milestonesArray.put(mObj)
        }
        root.put("milestones", milestonesArray)

        val routinesArray = JSONArray()
        config.routines.forEach { r ->
            val rObj = JSONObject()
            rObj.put("id", r.id)
            rObj.put("iconType", r.iconType)
            rObj.put("title", r.title)
            rObj.put("description", r.description)
            routinesArray.put(rObj)
        }
        root.put("routines", routinesArray)

        prefs.edit().putString(KEY_CONFIG_JSON, root.toString()).apply()
    }

    fun resetToDefault(): RoadmapConfig {
        prefs.edit().remove(KEY_CONFIG_JSON).remove(KEY_COMPLETED_DAYS).apply()
        return DefaultRoadmapData.getDefault()
    }

    fun getCompletedDays(): Set<Int> {
        val set = prefs.getStringSet(KEY_COMPLETED_DAYS, null)
        return set?.mapNotNull { it.toIntOrNull() }?.toSet() ?: setOf(1)
    }

    fun toggleCompletedDay(dayNumber: Int): Set<Int> {
        val current = getCompletedDays().toMutableSet()
        if (current.contains(dayNumber)) {
            current.remove(dayNumber)
        } else {
            current.add(dayNumber)
        }
        prefs.edit().putStringSet(KEY_COMPLETED_DAYS, current.map { it.toString() }.toSet()).apply()
        return current
    }
}
