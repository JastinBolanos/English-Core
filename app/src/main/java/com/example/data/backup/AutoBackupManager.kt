package com.example.data.backup

import android.content.Context
import android.os.Environment
import com.example.data.local.CustomWordRecord
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
    val totalWords: Int = 2809,
    val vocabularyWordCounts: Map<String, Int> = emptyMap(),
    val customWords: List<CustomWordRecord> = emptyList(),
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
 * Persiste los datos completos para evitar cualquier pérdida de progreso:
 * 1. Lista de las 2809 palabras de frecuencia con el contador exacto de cada una (ej. you=2, the=12, etc.)
 * 2. Nuevas palabras personalizadas añadidas después de la 2809 (2810, 2811...) con sus datos y contador
 * 3. Notas locales con su contenido íntegro (texto, estado, id, fecha de creación)
 * 4. Contadores del Abecedario (26 letras)
 * 5. Contadores de Números (1 al 100)
 * 6. Contadores de Frases Cotidianas (50 frases)
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
        allWordsDetails: List<Word> = emptyList(),
        customWords: List<CustomWordRecord> = emptyList(),
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
                val effectiveTotalWords = if (allWordsDetails.isNotEmpty()) {
                    allWordsDetails.size
                } else {
                    maxOf(2809, vocabularyWordCounts.size)
                }
                put("totalWords", effectiveTotalWords)
                put("usedWordsCount", usedWordsCount)
                put("totalVocabularyUses", vocabularyWordCounts.values.sum())
                put("totalAlphabetUses", alphabetCounts.values.sum())
                put("totalNumberUses", numberCounts.values.sum())
                put("totalPhraseUses", phraseCounts.values.sum())

                // 1. Array con todas las 2809+ palabras del vocabulario y sus contadores exactos
                val wordsArray = JSONArray()
                if (allWordsDetails.isNotEmpty()) {
                    allWordsDetails.forEach { w ->
                        val obj = JSONObject().apply {
                            put("rank", w.rank)
                            put("word", w.lemma)
                            put("useCount", w.useCount)
                        }
                        wordsArray.put(obj)
                    }
                } else {
                    vocabularyWordCounts.forEach { (lemma, count) ->
                        val obj = JSONObject().apply {
                            put("word", lemma)
                            put("useCount", count)
                        }
                        wordsArray.put(obj)
                    }
                }
                put("allVocabularyWords", wordsArray)

                // 2. Diccionario directo palabra -> contador
                val vocabObj = JSONObject()
                vocabularyWordCounts.forEach { (lemma, count) ->
                    vocabObj.put(lemma, count)
                }
                put("vocabularyWordCounts", vocabObj)

                // 3. Palabras personalizadas añadidas después de la 2809 (2810, 2811...)
                val customArray = JSONArray()
                val effectiveCustomWords = if (customWords.isNotEmpty()) {
                    customWords
                } else {
                    allWordsDetails.filter { it.rank > 2809 }.map { w ->
                        CustomWordRecord(
                            rank = w.rank,
                            word = w.lemma,
                            partOfSpeech = w.rawPartOfSpeech.ifBlank { w.partOfSpeech.name },
                            spanishTranslation = w.spanishTranslation,
                            microExample = w.microExample,
                            useCount = w.useCount
                        )
                    }
                }
                effectiveCustomWords.forEach { cw ->
                    val obj = JSONObject().apply {
                        put("rank", cw.rank)
                        put("word", cw.word)
                        put("partOfSpeech", cw.partOfSpeech)
                        put("spanishTranslation", cw.spanishTranslation)
                        put("microExample", cw.microExample)
                        put("useCount", cw.useCount)
                    }
                    customArray.put(obj)
                }
                put("customWords", customArray)

                // 4. Notas locales completas (guardando su texto y contenido íntegro)
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

                // 5. Contadores del abecedario
                val alphabetObj = JSONObject()
                alphabetCounts.forEach { (k, v) -> alphabetObj.put(k, v) }
                put("alphabetCounts", alphabetObj)

                // 6. Contadores de números
                val numbersObj = JSONObject()
                numberCounts.forEach { (k, v) -> numbersObj.put(k.toString(), v) }
                put("numberCounts", numbersObj)

                // 7. Contadores de frases cotidianas
                val phrasesObj = JSONObject()
                phraseCounts.forEach { (k, v) -> phrasesObj.put(k.toString(), v) }
                put("phraseCounts", phrasesObj)
            }

            val jsonString = rootJson.toString(2)
            val jsonBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

            // Guardar en almacenamiento interno
            val internalFile = File(context.filesDir, BACKUP_FILENAME)
            internalFile.writeText(jsonString, StandardCharsets.UTF_8)

            // Guardar en Directorio Público de Documentos (Persiste tras desinstalar la app)
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
            val totalWords = root.optInt("totalWords", 2809)

            // Contadores de palabras del vocabulario
            val vocabCountsMap = mutableMapOf<String, Int>()
            if (root.has("vocabularyWordCounts")) {
                val vocabObj = root.getJSONObject("vocabularyWordCounts")
                val keys = vocabObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    vocabCountsMap[k.lowercase().trim()] = vocabObj.optInt(k, 0)
                }
            }
            if (root.has("allVocabularyWords")) {
                val array = root.getJSONArray("allVocabularyWords")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val w = obj.optString("word", "").lowercase().trim()
                    val c = obj.optInt("useCount", 0)
                    if (w.isNotBlank()) {
                        vocabCountsMap[w] = c
                    }
                }
            }

            // Palabras personalizadas añadidas después de la 2809 (2810, 2811...)
            val customWordsList = mutableListOf<CustomWordRecord>()
            if (root.has("customWords")) {
                val array = root.getJSONArray("customWords")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val word = obj.optString("word", "").trim()
                    if (word.isNotBlank()) {
                        val wordLower = word.lowercase().trim()
                        val count = obj.optInt("useCount", vocabCountsMap[wordLower] ?: 0)
                        customWordsList.add(
                            CustomWordRecord(
                                rank = obj.optInt("rank", 2810 + i),
                                word = word,
                                partOfSpeech = obj.optString("partOfSpeech", "sustantivo"),
                                spanishTranslation = obj.optString("spanishTranslation", ""),
                                microExample = obj.optString("microExample", ""),
                                useCount = count
                            )
                        )
                        vocabCountsMap[wordLower] = count
                    }
                }
            }

            // Contadores del abecedario
            val alphabetMap = mutableMapOf<String, Int>()
            if (root.has("alphabetCounts")) {
                val alphabetObj = root.getJSONObject("alphabetCounts")
                val keys = alphabetObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    alphabetMap[k.uppercase().trim()] = alphabetObj.optInt(k, 0)
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

            // Notas locales con su contenido íntegro y verificado
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
                totalWords = totalWords,
                vocabularyWordCounts = vocabCountsMap,
                customWords = customWordsList,
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
