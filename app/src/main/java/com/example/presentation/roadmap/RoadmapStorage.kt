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
            val missionTitle = root.optString("missionTitle", "MISIÓN: INGLÉS AVANZADO")
            val dateRange = root.optString("dateRange", "21 OCT 2026 – 21 JUN 2027")
            val hoursSummary = root.optString("hoursSummary", "3 HORAS DIARIAS • 732 HORAS TOTALES")
            val footerText = root.optString("footerText", "Fuerza Interna 95% / Reflejo en Vivo 5%")

            val milestones = mutableListOf<MilestoneItem>()
            val milestonesArray = root.optJSONArray("milestones") ?: JSONArray()
            for (i in 0 until milestonesArray.length()) {
                val itemObj = milestonesArray.getJSONObject(i)
                milestones.add(
                    MilestoneItem(
                        id = itemObj.optString("id", "m_$i"),
                        date = itemObj.optString("date", ""),
                        title = itemObj.optString("title", ""),
                        description = itemObj.optString("description", ""),
                        iconType = itemObj.optString("iconType", "star"),
                        isCompleted = itemObj.optBoolean("isCompleted", false)
                    )
                )
            }

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

            RoadmapConfig(
                missionTitle = missionTitle,
                dateRange = dateRange,
                hoursSummary = hoursSummary,
                milestones = if (milestones.isNotEmpty()) milestones else DefaultRoadmapData.getDefault().milestones,
                routines = if (routines.isNotEmpty()) routines else DefaultRoadmapData.getDefault().routines,
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
