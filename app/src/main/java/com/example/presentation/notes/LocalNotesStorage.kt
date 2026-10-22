package com.example.presentation.notes

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Representa un bloque de nota local en fila.
 */
data class NoteBlock(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Almacenamiento local persistente para las notas en SharedPreferences.
 * No requiere servicios en la nube ni conexión a internet.
 */
class LocalNotesStorage(context: Context) {
    private val prefs = context.getSharedPreferences("english_local_notes_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NOTES_JSON = "key_local_notes_list_json"
    }

    fun loadNotes(): List<NoteBlock> {
        val rawJson = prefs.getString(KEY_NOTES_JSON, null) ?: return emptyList()
        val list = mutableListOf<NoteBlock>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    NoteBlock(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        text = obj.optString("text", ""),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    fun saveNotes(notes: List<NoteBlock>) {
        try {
            val jsonArray = JSONArray()
            notes.forEach { note ->
                val obj = JSONObject().apply {
                    put("id", note.id)
                    put("text", note.text)
                    put("isCompleted", note.isCompleted)
                    put("createdAt", note.createdAt)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_NOTES_JSON, jsonArray.toString()).apply()
        } catch (_: Exception) {
            // Silencioso ante errores locales
        }
    }

    fun clearAll() {
        prefs.edit().remove(KEY_NOTES_JSON).apply()
    }
}
