package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.Locale

/**
 * Gestor de persistencia rápida y redundante para los contadores de uso de las palabras del vocabulario.
 * Asegura que los contadores no se pierdan y sirvan de respaldo inmediato para Room Database.
 */
class VocabularyStorage(private val context: Context?) {
    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("vocabulary_word_counts_prefs", Context.MODE_PRIVATE)

    private val memoryFallback = mutableMapOf<String, Int>()

    companion object {
        private const val KEY_VOCAB_COUNTS_JSON = "key_vocab_counts_json"
    }

    @Synchronized
    fun loadCounts(): Map<String, Int> {
        val sp = prefs ?: return memoryFallback.toMap()
        val jsonStr = sp.getString(KEY_VOCAB_COUNTS_JSON, null) ?: return emptyMap()
        return try {
            val root = JSONObject(jsonStr)
            val map = mutableMapOf<String, Int>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val count = root.optInt(key, 0)
                if (count > 0) {
                    map[key.lowercase(Locale.ROOT).trim()] = count
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @Synchronized
    fun incrementCount(word: String): Int {
        val clean = word.lowercase(Locale.ROOT).trim()
        if (clean.isBlank()) return 0
        val current = loadCounts().toMutableMap()
        val newCount = (current[clean] ?: 0) + 1
        current[clean] = newCount
        saveCounts(current)
        return newCount
    }

    @Synchronized
    fun saveCounts(counts: Map<String, Int>) {
        val active = counts.filter { it.key.isNotBlank() && it.value > 0 }
        if (prefs == null) {
            memoryFallback.clear()
            memoryFallback.putAll(active)
            return
        }
        val root = JSONObject()
        active.forEach { (k, v) ->
            root.put(k.lowercase(Locale.ROOT).trim(), v)
        }
        prefs.edit().putString(KEY_VOCAB_COUNTS_JSON, root.toString()).apply()
    }

    @Synchronized
    fun clearCounts() {
        if (prefs == null) {
            memoryFallback.clear()
            return
        }
        prefs.edit().remove(KEY_VOCAB_COUNTS_JSON).apply()
    }
}
