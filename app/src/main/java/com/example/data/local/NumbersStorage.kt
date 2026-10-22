package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * Gestor de persistencia del contador de uso y práctica de los números (1 al 100).
 * Almacena el número de veces que cada número sube al lienzo de pruebas o se escucha su pronunciación.
 */
class NumbersStorage(private val context: Context?) {
    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("numbers_practice_prefs", Context.MODE_PRIVATE)

    private val memoryFallback = mutableMapOf<Int, Int>()

    companion object {
        private const val KEY_NUMBERS_COUNTS_JSON = "key_numbers_counts_json"
    }

    @Synchronized
    fun loadCounts(): Map<Int, Int> {
        val sp = prefs ?: return memoryFallback.toMap()
        val jsonStr = sp.getString(KEY_NUMBERS_COUNTS_JSON, null) ?: return emptyMap()
        return try {
            val root = JSONObject(jsonStr)
            val map = mutableMapOf<Int, Int>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val num = key.toIntOrNull()
                if (num != null) {
                    map[num] = root.optInt(key, 0)
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Synchronized
    fun incrementCount(number: Int): Int {
        val current = loadCounts().toMutableMap()
        val newCount = (current[number] ?: 0) + 1
        current[number] = newCount
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
        prefs.edit().putString(KEY_NUMBERS_COUNTS_JSON, root.toString()).apply()
    }

    @Synchronized
    fun clearCounts() {
        if (prefs == null) {
            memoryFallback.clear()
            return
        }
        prefs.edit().remove(KEY_NUMBERS_COUNTS_JSON).apply()
    }
}
