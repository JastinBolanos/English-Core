package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * Gestor de persistencia del contador de uso y práctica de las Frases Cotidianas.
 * Almacena el número de veces que cada frase sube al lienzo de pruebas, se escucha o se traduce.
 */
class PhrasesStorage(private val context: Context?) {
    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("phrases_practice_prefs", Context.MODE_PRIVATE)

    private val memoryFallback = mutableMapOf<Int, Int>()

    companion object {
        private const val KEY_PHRASES_COUNTS_JSON = "key_phrases_counts_json"
    }

    @Synchronized
    fun loadCounts(): Map<Int, Int> {
        val sp = prefs ?: return memoryFallback.toMap()
        val jsonStr = sp.getString(KEY_PHRASES_COUNTS_JSON, null) ?: return emptyMap()
        return try {
            val root = JSONObject(jsonStr)
            val map = mutableMapOf<Int, Int>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val id = key.toIntOrNull()
                if (id != null) {
                    map[id] = root.optInt(key, 0)
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Synchronized
    fun incrementCount(phraseId: Int): Int {
        val current = loadCounts().toMutableMap()
        val newCount = (current[phraseId] ?: 0) + 1
        current[phraseId] = newCount
        saveCounts(current)
        return newCount
    }

    @Synchronized
    fun saveCounts(counts: Map<Int, Int>) {
        if (prefs == null) {
            memoryFallback.clear()
            memoryFallback.putAll(counts)
            return
        }
        val root = JSONObject()
        counts.forEach { (k, v) -> root.put(k.toString(), v) }
        prefs.edit().putString(KEY_PHRASES_COUNTS_JSON, root.toString()).apply()
    }

    @Synchronized
    fun clearCounts() {
        if (prefs == null) {
            memoryFallback.clear()
            return
        }
        prefs.edit().remove(KEY_PHRASES_COUNTS_JSON).apply()
    }
}
