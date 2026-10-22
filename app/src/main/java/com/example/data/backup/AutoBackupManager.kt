package com.example.data.backup

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.domain.model.Word
import com.example.presentation.notes.NoteBlock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

data class RestoredBackupData(
    val appName: String,
    val version: Int,
    val timestamp: Long,
    val usedWordsCount: Int,
    val studyTimerMillis: Long,
    val draftWords: List<Word>,
    val notes: List<NoteBlock>
)

/**
 * Gestor robusto de Copia de Seguridad Local y Automática anti-desinstalación.
 * Guarda en el directorio público Documentos/VocabFlow/ y en el almacenamiento interno.
 * Los archivos guardados en Documentos/VocabFlow/ NO se borran cuando el usuario desinstala la aplicación.
 */
object AutoBackupManager {

    private const val BACKUP_FILENAME = "vocabflow_backup.json"
    private const val FOLDER_NAME = "VocabFlow"

    /**
     * Guarda el estado completo en el archivo .json tanto en Documents/VocabFlow (público anti-borrado)
     * como en el almacenamiento interno para acceso rápido.
     */
    fun saveFullBackup(
        context: Context,
        usedWordsCount: Int,
        studyTimerMillis: Long,
        draftWords: List<Word>,
        notes: List<NoteBlock>
    ): Boolean {
        return try {
            val rootJson = JSONObject().apply {
                put("appName", "VocabFlow")
                put("version", 1)
                put("exportedAt", System.currentTimeMillis())
                put("usedWordsCount", usedWordsCount)
                put("studyTimerMillis", studyTimerMillis)

                val draftArray = JSONArray()
                draftWords.forEach { w ->
                    draftArray.put(JSONObject().apply {
                        put("lemma", w.lemma)
                        put("partOfSpeech", w.partOfSpeech)
                        put("spanishTranslation", w.spanishTranslation)
                        put("microExample", w.microExample)
                    })
                }
                put("draftWords", draftArray)

                val notesArray = JSONArray()
                notes.forEach { n ->
                    notesArray.put(JSONObject().apply {
                        put("id", n.id)
                        put("text", n.text)
                        put("isCompleted", n.isCompleted)
                        put("createdAt", n.createdAt)
                    })
                }
                put("localNotes", notesArray)
            }

            val jsonString = rootJson.toString(2)
            val jsonBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

            // 1. Guardar en almacenamiento interno
            val internalFile = File(context.filesDir, BACKUP_FILENAME)
            internalFile.writeText(jsonString, StandardCharsets.UTF_8)

            // 2. Guardar en Directorio Público de Documentos (Persiste tras desinstalar la app)
            saveToPublicDocuments(context, jsonBytes)

            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Escribe el archivo en Documentos/VocabFlow/vocabflow_backup.json para que sobreviva a la desinstalación.
     */
    private fun saveToPublicDocuments(context: Context, bytes: ByteArray) {
        try {
            val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val vocabFlowDir = File(docsDir, FOLDER_NAME)
            if (!vocabFlowDir.exists()) {
                vocabFlowDir.mkdirs()
            }
            val targetFile = File(vocabFlowDir, BACKUP_FILENAME)
            FileOutputStream(targetFile).use { fos ->
                fos.write(bytes)
                fos.flush()
            }
        } catch (_: Exception) {
            // Intento alternativo en Downloads si Documents tuviera restricción de acceso
            try {
                val dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetFile = File(File(dlDir, FOLDER_NAME).apply { mkdirs() }, BACKUP_FILENAME)
                FileOutputStream(targetFile).use { fos ->
                    fos.write(bytes)
                    fos.flush()
                }
            } catch (_: Exception) {
                // Silencioso, ya se guardó internamente
            }
        }
    }

    /**
     * Verifica si existe una copia previa persistida en el teléfono (ej. de antes de desinstalar).
     */
    fun hasSurvivingBackup(context: Context): Boolean {
        val docsFile = File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME), BACKUP_FILENAME)
        if (docsFile.exists() && docsFile.length() > 0) return true

        val dlFile = File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDER_NAME), BACKUP_FILENAME)
        if (dlFile.exists() && dlFile.length() > 0) return true

        val internalFile = File(context.filesDir, BACKUP_FILENAME)
        return internalFile.exists() && internalFile.length() > 0
    }

    /**
     * Carga los datos de la copia más reciente disponible (priorizando la pública que resiste desinstalaciones).
     */
    fun loadLatestBackup(context: Context): RestoredBackupData? {
        val candidates = listOf(
            File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME), BACKUP_FILENAME),
            File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDER_NAME), BACKUP_FILENAME),
            File(context.filesDir, BACKUP_FILENAME)
        )

        for (file in candidates) {
            if (file.exists() && file.length() > 0) {
                try {
                    val raw = file.readText(StandardCharsets.UTF_8)
                    val parsed = parseJsonContent(raw)
                    if (parsed != null) return parsed
                } catch (_: Exception) {
                    continue
                }
            }
        }
        return null
    }

    /**
     * Parsea la estructura JSON en un objeto de datos de restauración.
     */
    fun parseJsonContent(jsonString: String): RestoredBackupData? {
        return try {
            val root = JSONObject(jsonString)
            val appName = root.optString("appName", "VocabFlow")
            val version = root.optInt("version", 1)
            val timestamp = root.optLong("exportedAt", System.currentTimeMillis())
            val usedWordsCount = root.optInt("usedWordsCount", 0)
            val studyTimerMillis = root.optLong("studyTimerMillis", 0L)

            val draftList = mutableListOf<Word>()
            if (root.has("draftWords")) {
                val array = root.getJSONArray("draftWords")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val posRaw = obj.optString("partOfSpeech", "")
                    val pos = try {
                        com.example.domain.model.PartOfSpeech.valueOf(posRaw)
                    } catch (_: Exception) {
                        com.example.domain.model.PartOfSpeech.OTHER
                    }
                    draftList.add(
                        Word(
                            lemma = obj.optString("lemma", ""),
                            partOfSpeech = pos,
                            rank = i + 1,
                            rawPartOfSpeech = posRaw,
                            spanishTranslation = obj.optString("spanishTranslation", ""),
                            microExample = obj.optString("microExample", "")
                        )
                    )
                }
            }

            val notesList = mutableListOf<NoteBlock>()
            if (root.has("localNotes")) {
                val array = root.getJSONArray("localNotes")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    notesList.add(
                        NoteBlock(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            text = obj.optString("text", ""),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            RestoredBackupData(
                appName = appName,
                version = version,
                timestamp = timestamp,
                usedWordsCount = usedWordsCount,
                studyTimerMillis = studyTimerMillis,
                draftWords = draftList,
                notes = notesList
            )
        } catch (_: Exception) {
            null
        }
    }

    fun getPublicStoragePath(): String {
        return "Documentos/VocabFlow/$BACKUP_FILENAME"
    }
}
