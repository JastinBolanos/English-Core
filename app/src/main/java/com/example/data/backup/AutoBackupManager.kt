package com.example.data.backup

import android.content.Context
import android.os.Environment
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
    val vocabularyWordCounts: Map<String, Int> = emptyMap(),
    val alphabetCounts: Map<String, Int> = emptyMap(),
    val numberCounts: Map<Int, Int> = emptyMap(),
    val phraseCounts: Map<Int, Int> = emptyMap(),
    val notes: List<NoteBlock> = emptyList()
)

/**
 * Gestor robusto de Copia de Seguridad Local y Automática anti-desinstalación.
 * Guarda en el directorio público Documentos/VocabFlow/ y en el almacenamiento interno.
 * Los archivos guardados en Documentos/VocabFlow/ NO se borran cuando el usuario desinstala la aplicación.
 *
 * Persiste los contadores de uso de:
 * 1. Vocabulario completo (cada palabra y su contador específico: 0, 20, 32...)
 * 2. Abecedario (26 letras y sus conteos)
 * 3. Números (1 al 100 y sus conteos)
 * 4. Frases cotidianas (50 frases y sus conteos)
 * 5. Notas locales
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
        vocabularyWordCounts: Map<String, Int> = emptyMap(),
        usedWordsCount: Int,
        notes: List<NoteBlock>,
        alphabetCounts: Map<String, Int> = emptyMap(),
        numberCounts: Map<Int, Int> = emptyMap(),
        phraseCounts: Map<Int, Int> = emptyMap()
    ): Boolean {
        return try {
            val rootJson = JSONObject().apply {
                put("appName", "VocabFlow")
                put("version", 2)
                put("exportedAt", System.currentTimeMillis())
                put("usedWordsCount", usedWordsCount)
                put("totalVocabularyUses", vocabularyWordCounts.values.sum())
                put("totalAlphabetUses", alphabetCounts.values.sum())
                put("totalNumberUses", numberCounts.values.sum())
                put("totalPhraseUses", phraseCounts.values.sum())

                // 1. Contadores completos de todas las palabras del vocabulario (palabra -> conteo: 0, 20, 32...)
                val vocabObj = JSONObject()
                vocabularyWordCounts.forEach { (lemma, count) ->
                    vocabObj.put(lemma, count)
                }
                put("vocabularyWordCounts", vocabObj)

                // 2. Contadores del abecedario
                val alphabetObj = JSONObject()
                alphabetCounts.forEach { (k, v) -> alphabetObj.put(k, v) }
                put("alphabetCounts", alphabetObj)

                // 3. Contadores de números
                val numbersObj = JSONObject()
                numberCounts.forEach { (k, v) -> numbersObj.put(k.toString(), v) }
                put("numberCounts", numbersObj)

                // 4. Contadores de frases cotidianas
                val phrasesObj = JSONObject()
                phraseCounts.forEach { (k, v) -> phrasesObj.put(k.toString(), v) }
                put("phraseCounts", phrasesObj)

                // 5. Notas locales
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
            val version = root.optInt("version", 2)
            val timestamp = root.optLong("exportedAt", System.currentTimeMillis())
            val usedWordsCount = root.optInt("usedWordsCount", 0)

            // Contadores de palabras del vocabulario
            val vocabCountsMap = mutableMapOf<String, Int>()
            if (root.has("vocabularyWordCounts")) {
                val vocabObj = root.getJSONObject("vocabularyWordCounts")
                val keys = vocabObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    vocabCountsMap[k] = vocabObj.optInt(k, 0)
                }
            }

            // Contadores del abecedario
            val alphabetMap = mutableMapOf<String, Int>()
            if (root.has("alphabetCounts")) {
                val alphabetObj = root.getJSONObject("alphabetCounts")
                val keys = alphabetObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    alphabetMap[k] = alphabetObj.optInt(k, 0)
                }
            }

            // Contadores de números
            val numbersMap = mutableMapOf<Int, Int>()
            if (root.has("numberCounts")) {
                val numbersObj = root.getJSONObject("numberCounts")
                val keys = numbersObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val num = k.toIntOrNull()
                    if (num != null) {
                        numbersMap[num] = numbersObj.optInt(k, 0)
                    }
                }
            }

            // Contadores de frases cotidianas
            val phrasesMap = mutableMapOf<Int, Int>()
            if (root.has("phraseCounts")) {
                val phrasesObj = root.getJSONObject("phraseCounts")
                val keys = phrasesObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val id = k.toIntOrNull()
                    if (id != null) {
                        phrasesMap[id] = phrasesObj.optInt(k, 0)
                    }
                }
            }

            // Notas locales
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
                vocabularyWordCounts = vocabCountsMap,
                alphabetCounts = alphabetMap,
                numberCounts = numbersMap,
                phraseCounts = phrasesMap,
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
