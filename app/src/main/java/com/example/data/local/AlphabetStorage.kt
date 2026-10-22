package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * Gestor de persistencia del contador de uso y práctica del abecedario.
 * Almacena el número de veces que cada letra sube al lienzo de pruebas o se escucha su sonido.
 */
class AlphabetStorage(private val context: Context?) {
    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("alphabet_practice_prefs", Context.MODE_PRIVATE)

    private val memoryFallback = mutableMapOf<String, Int>()

    companion object {
        private const val KEY_ALPHABET_COUNTS_JSON = "key_alphabet_counts_json"
    }

    @Synchronized
    fun loadCounts(): Map<String, Int> {
        val sp = prefs ?: return memoryFallback.toMap()
        val jsonStr = sp.getString(KEY_ALPHABET_COUNTS_JSON, null) ?: return emptyMap()
        return try {
            val root = JSONObject(jsonStr)
            val map = mutableMapOf<String, Int>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key.uppercase()] = root.optInt(key, 0)
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Synchronized
    fun incrementCount(letter: String): Int {
        val cleanLetter = letter.trim().uppercase()
        val current = loadCounts().toMutableMap()
        val newCount = (current[cleanLetter] ?: 0) + 1
        current[cleanLetter] = newCount
        saveCounts(current)
        return newCount
    }

    @Synchronized
    fun saveCounts(counts: Map<String, Int>) {
        if (prefs == null) {
            memoryFallback.clear()
            counts.forEach { (k, v) -> memoryFallback[k.uppercase()] = v }
            return
        }
        val root = JSONObject()
        counts.forEach { (k, v) -> root.put(k.uppercase(), v) }
        prefs.edit().putString(KEY_ALPHABET_COUNTS_JSON, root.toString()).commit()
    }

    @Synchronized
    fun clearCounts() {
        if (prefs == null) {
            memoryFallback.clear()
            return
        }
        prefs.edit().remove(KEY_ALPHABET_COUNTS_JSON).commit()
    }
}
