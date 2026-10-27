package com.example.presentation.notes

import android.content.Context
import com.example.data.backup.AutoBackupManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
 * Almacenamiento unificado de notas locales dentro del único archivo .json general de la app (vocabflow_backup.json),
 * con caché de acceso inmediato en SharedPreferences.
 * Centraliza todo en un solo archivo maestro para toda la aplicación.
 */
class LocalNotesStorage(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences("english_local_notes_prefs", Context.MODE_PRIVATE)
    }

    companion object {
        private const val KEY_NOTES_JSON = "key_local_notes_list_json"
    }

    init {
        // Migrar y limpiar archivos sueltos obsoletos si existen
        try {
            val legacyFile = File(context.filesDir, "local_notes.json")
            if (legacyFile.exists()) {
                val legacyText = legacyFile.readText().trim()
                if (legacyText.isNotBlank() && legacyText != "[]" && legacyText != "null") {
                    val list = parseJsonString(legacyText)
                    if (list.isNotEmpty()) {
                        saveNotes(list)
                    }
                }
                legacyFile.delete()
            }
            File(context.filesDir, "local_notes.json.tmp").delete()
        } catch (_: Exception) {}
    }

    @Synchronized
    fun loadNotes(): List<NoteBlock> {
        // 1. Cargar desde el archivo único maestro de toda la app
        val fromBackup = AutoBackupManager.loadLatestBackup(context)
        if (fromBackup != null) {
            return fromBackup.notes
        }

        // 2. Si aún no se ha generado la copia maestra, consultar el caché local
        val fromPrefs = prefs.getString(KEY_NOTES_JSON, null)?.trim()
        if (!fromPrefs.isNullOrBlank() && fromPrefs != "[]" && fromPrefs != "null") {
            val list = parseJsonString(fromPrefs)
            if (list.isNotEmpty()) {
                return list
            }
        }

        return emptyList()
    }

    @Synchronized
    fun saveNotes(notes: List<NoteBlock>) {
        try {
            val jsonArray = JSONArray()
            notes.forEach { note ->
                val obj = JSONObject().apply {
                    put("id", note.id.ifBlank { UUID.randomUUID().toString() })
                    put("text", note.text)
                    put("isCompleted", note.isCompleted)
                    put("createdAt", if (note.createdAt > 0) note.createdAt else System.currentTimeMillis())
                }
                jsonArray.put(obj)
            }
            val formattedJson = jsonArray.toString(2)

            // Caché rápido reactivo
            prefs.edit().putString(KEY_NOTES_JSON, formattedJson).commit()

            // Guardado centralizado en el ÚNICO archivo .json de toda la app
            AutoBackupManager.updateNotesInBackup(context, notes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun clearAll() {
        try {
            prefs.edit().remove(KEY_NOTES_JSON).commit()
            AutoBackupManager.updateNotesInBackup(context, emptyList())
        } catch (_: Exception) {}
    }

    private fun parseJsonString(rawJson: String): List<NoteBlock> {
        val list = mutableListOf<NoteBlock>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.opt(i)
                if (item is JSONObject) {
                    val id = item.optString("id", UUID.randomUUID().toString()).ifBlank { UUID.randomUUID().toString() }
                    val text = item.optString("text", "").ifBlank {
                        item.optString("content", "").ifBlank {
                            item.optString("note", "")
                        }
                    }
                    val isCompleted = item.optBoolean("isCompleted", false)
                    val createdAt = item.optLong("createdAt", System.currentTimeMillis())
                    list.add(
                        NoteBlock(
                            id = id,
                            text = text,
                            isCompleted = isCompleted,
                            createdAt = createdAt
                        )
                    )
                } else if (item is String && item.isNotBlank()) {
                    list.add(NoteBlock(text = item))
                }
            }
        } catch (_: Exception) {}
        return list
    }
}
