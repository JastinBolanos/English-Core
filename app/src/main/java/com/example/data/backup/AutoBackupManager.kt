package com.example.data.backup

import android.content.Context
import android.os.Environment
import com.example.data.local.CustomWordRecord
import com.example.data.local.SavedDraftPhrase
import com.example.domain.model.EverydayPhrasesCatalog
import com.example.domain.model.Word
import com.example.presentation.notes.NoteBlock
import com.example.presentation.numbers.NumbersCatalog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

data class AlphabetLetterRecord(
    val index: Int,
    val letter: String,
    val ipa: String,
    val phoneticSp: String,
    val isVowel: Boolean,
    val note: String = "",
    val useCount: Int = 0
)

val standardAlphabetCatalog = listOf(
    AlphabetLetterRecord(index = 1, letter = "A", ipa = "/ei/", phoneticSp = "éi", isVowel = true),
    AlphabetLetterRecord(index = 2, letter = "B", ipa = "/biː/", phoneticSp = "bí", isVowel = false),
    AlphabetLetterRecord(index = 3, letter = "C", ipa = "/siː/", phoneticSp = "sí", isVowel = false),
    AlphabetLetterRecord(index = 4, letter = "D", ipa = "/diː/", phoneticSp = "dí", isVowel = false),
    AlphabetLetterRecord(index = 5, letter = "E", ipa = "/iː/", phoneticSp = "í", isVowel = true),
    AlphabetLetterRecord(index = 6, letter = "F", ipa = "/ef/", phoneticSp = "ef", isVowel = false),
    AlphabetLetterRecord(index = 7, letter = "G", ipa = "/dʒiː/", phoneticSp = "yí", isVowel = false),
    AlphabetLetterRecord(index = 8, letter = "H", ipa = "/eɪtʃ/", phoneticSp = "éich", isVowel = false),
    AlphabetLetterRecord(index = 9, letter = "I", ipa = "/aɪ/", phoneticSp = "ái", isVowel = true),
    AlphabetLetterRecord(index = 10, letter = "J", ipa = "/dʒeɪ/", phoneticSp = "yéi", isVowel = false),
    AlphabetLetterRecord(index = 11, letter = "K", ipa = "/keɪ/", phoneticSp = "kéi", isVowel = false),
    AlphabetLetterRecord(index = 12, letter = "L", ipa = "/el/", phoneticSp = "el", isVowel = false),
    AlphabetLetterRecord(index = 13, letter = "M", ipa = "/em/", phoneticSp = "em", isVowel = false),
    AlphabetLetterRecord(index = 14, letter = "N", ipa = "/en/", phoneticSp = "en", isVowel = false),
    AlphabetLetterRecord(index = 15, letter = "O", ipa = "/əʊ/", phoneticSp = "óu", isVowel = true),
    AlphabetLetterRecord(index = 16, letter = "P", ipa = "/piː/", phoneticSp = "pí", isVowel = false),
    AlphabetLetterRecord(index = 17, letter = "Q", ipa = "/kjuː/", phoneticSp = "kiú", isVowel = false),
    AlphabetLetterRecord(index = 18, letter = "R", ipa = "/ɑːr/", phoneticSp = "ar", isVowel = false),
    AlphabetLetterRecord(index = 19, letter = "S", ipa = "/es/", phoneticSp = "es", isVowel = false),
    AlphabetLetterRecord(index = 20, letter = "T", ipa = "/tiː/", phoneticSp = "tí", isVowel = false),
    AlphabetLetterRecord(index = 21, letter = "U", ipa = "/juː/", phoneticSp = "iú", isVowel = true),
    AlphabetLetterRecord(index = 22, letter = "V", ipa = "/viː/", phoneticSp = "ví", isVowel = false),
    AlphabetLetterRecord(index = 23, letter = "W", ipa = "/ˈdʌb.əl.juː/", phoneticSp = "dábal-iú", isVowel = false),
    AlphabetLetterRecord(index = 24, letter = "X", ipa = "/eks/", phoneticSp = "eks", isVowel = false),
    AlphabetLetterRecord(index = 25, letter = "Y", ipa = "/waɪ/", phoneticSp = "wái", isVowel = false),
    AlphabetLetterRecord(index = 26, letter = "Z", ipa = "/ziː/ · /zed/", phoneticSp = "zí / zed", isVowel = false, note = "zí en EE. UU. / zed en Reino Unido")
)

data class NumberRecord(
    val number: Int,
    val word: String,
    val ipa: String,
    val phoneticSp: String,
    val isDecade: Boolean = false,
    val useCount: Int = 0
)

val standardNumbersCatalog: List<NumberRecord> by lazy {
    NumbersCatalog.allNumbers.map { entry ->
        NumberRecord(
            number = entry.number,
            word = entry.word,
            ipa = entry.ipa,
            phoneticSp = entry.phoneticSp,
            isDecade = entry.isDecade,
            useCount = 0
        )
    }
}

data class PhraseRecord(
    val id: Int,
    val spanishText: String,
    val category: String,
    val englishHint: String = "",
    val useCount: Int = 0
)

val standardPhrasesCatalog: List<PhraseRecord> by lazy {
    EverydayPhrasesCatalog.phrases.map { entry ->
        PhraseRecord(
            id = entry.id,
            spanishText = entry.spanishText,
            category = entry.category,
            englishHint = entry.englishHint,
            useCount = 0
        )
    }
}

data class RestoredBackupData(
    val appName: String,
    val version: Int,
    val timestamp: Long,
    val usedWordsCount: Int,
    val totalWords: Int = 2809,
    val totalVocabularyUses: Int = 0,
    val vocabularyWordCounts: Map<String, Int> = emptyMap(),
    val customWords: List<CustomWordRecord> = emptyList(),
    val totalAlphabetLetters: Int = 26,
    val usedAlphabetLettersCount: Int = 0,
    val allAlphabetLetters: List<AlphabetLetterRecord> = emptyList(),
    val alphabetCounts: Map<String, Int> = emptyMap(),
    val totalNumbers: Int = 100,
    val usedNumbersCount: Int = 0,
    val allNumbers: List<NumberRecord> = emptyList(),
    val numberCounts: Map<Int, Int> = emptyMap(),
    val totalPhrases: Int = 400,
    val usedPhrasesCount: Int = 0,
    val allPhrases: List<PhraseRecord> = emptyList(),
    val phraseCounts: Map<Int, Int> = emptyMap(),
    val notes: List<NoteBlock> = emptyList(),
    val savedDraftPhrases: List<SavedDraftPhrase> = emptyList(),
    val wallpaperId: String? = null,
    val wallpaperDimAlpha: Float? = null
)

/**
 * Gestor robusto de Copia de Seguridad Local y Automática anti-desinstalación.
 * Guarda en el directorio público Documentos/VocabFlow/ y en el almacenamiento interno.
 * Los archivos guardados en Documentos/VocabFlow/ NO se borran cuando el usuario desinstala la aplicación.
 *
 * Persiste los datos completos para evitar cualquier pérdida de progreso:
 * 1. Lista de las 2809 palabras de frecuencia con el contador exacto de cada una (ej. you=2, the=12, etc.)
 * 2. Nuevas palabras personalizadas añadidas después de la 2809 (2810, 2811...) con sus datos y contador
 * 3. Catálogo completo de las 26 letras del abecedario (A a la Z) con fonética IPA y su contador individual exacto
 * 4. Notas locales con su contenido íntegro (texto, estado, id, fecha de creación)
 * 5. Contadores de Números (1 al 100)
 * 6. Contadores de Frases Cotidianas (400 frases en 8 módulos de 50)
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
        usedWordsCount: Int = 0,
        notes: List<NoteBlock> = emptyList(),
        alphabetCounts: Map<String, Int> = emptyMap(),
        allAlphabetLettersDetails: List<AlphabetLetterRecord> = emptyList(),
        numberCounts: Map<Int, Int> = emptyMap(),
        allNumbersDetails: List<NumberRecord> = emptyList(),
        phraseCounts: Map<Int, Int> = emptyMap(),
        allPhrasesDetails: List<PhraseRecord> = emptyList(),
        savedDraftPhrases: List<SavedDraftPhrase> = emptyList(),
        wallpaperId: String? = null,
        wallpaperDimAlpha: Float? = null
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

                // 0. Fondo de pantalla activo y tonalidad / atenuación
                val wpStorage = com.example.data.wallpaper.WallpaperStorage(context)
                val effectiveWallpaperId = wallpaperId ?: wpStorage.getSelectedWallpaperId()
                val effectiveDimAlpha = wallpaperDimAlpha ?: wpStorage.getWallpaperDimAlpha()
                put("wallpaperId", effectiveWallpaperId)
                put("wallpaperDimAlpha", effectiveDimAlpha.toDouble())

                val activeWpObj = JSONObject().apply {
                    put("id", effectiveWallpaperId)
                    val foundItem = wpStorage.scanAvailableWallpapers().firstOrNull { it.id == effectiveWallpaperId }
                    if (foundItem != null) {
                        put("title", foundItem.title)
                        if (foundItem.fileName != null) put("fileName", foundItem.fileName)
                        if (foundItem.assetPath != null) put("assetPath", foundItem.assetPath)
                    }
                    put("dimAlpha", effectiveDimAlpha.toDouble())
                    put("dimPercentage", (effectiveDimAlpha * 100).toInt())
                }
                put("activeWallpaper", activeWpObj)

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

                // 4.1 Frases armadas guardadas desde el lienzo de pruebas
                val savedPhrasesArray = JSONArray()
                savedDraftPhrases.forEach { sp ->
                    savedPhrasesArray.put(JSONObject().apply {
                        put("id", sp.id)
                        put("phraseText", sp.phraseText)
                        val wArr = JSONArray()
                        sp.words.forEach { wArr.put(it) }
                        put("words", wArr)
                        put("spanishTranslation", sp.spanishTranslation)
                        put("createdAt", sp.createdAt)
                    })
                }
                put("savedDraftPhrases", savedPhrasesArray)

                // 5. Array detallado con las 26 letras del abecedario y sus contadores exactos (A a la Z)
                val alphabetLettersArray = JSONArray()
                val completeAlphabetCounts = mutableMapOf<String, Int>()

                val baseAlphabet = if (allAlphabetLettersDetails.isNotEmpty()) {
                    allAlphabetLettersDetails
                } else {
                    standardAlphabetCatalog.map { std ->
                        std.copy(useCount = alphabetCounts[std.letter] ?: 0)
                    }
                }

                baseAlphabet.forEach { letterRecord ->
                    val cleanLetter = letterRecord.letter.uppercase().trim()
                    completeAlphabetCounts[cleanLetter] = letterRecord.useCount
                    val obj = JSONObject().apply {
                        put("index", letterRecord.index)
                        put("letter", cleanLetter)
                        put("ipa", letterRecord.ipa)
                        put("phoneticSp", letterRecord.phoneticSp)
                        put("isVowel", letterRecord.isVowel)
                        if (letterRecord.note.isNotBlank()) {
                            put("note", letterRecord.note)
                        }
                        put("useCount", letterRecord.useCount)
                    }
                    alphabetLettersArray.put(obj)
                }

                // Si hubiese letras personalizadas o adicionales en alphabetCounts
                alphabetCounts.forEach { (k, v) ->
                    val cleanKey = k.uppercase().trim()
                    if (!completeAlphabetCounts.containsKey(cleanKey)) {
                        completeAlphabetCounts[cleanKey] = v
                        val obj = JSONObject().apply {
                            put("index", 26 + completeAlphabetCounts.size)
                            put("letter", cleanKey)
                            put("ipa", "")
                            put("phoneticSp", "")
                            put("isVowel", false)
                            put("useCount", v)
                        }
                        alphabetLettersArray.put(obj)
                    }
                }
                put("allAlphabetLetters", alphabetLettersArray)

                // Metadatos específicos del abecedario
                put("totalAlphabetLetters", completeAlphabetCounts.size)
                put("usedAlphabetLettersCount", completeAlphabetCounts.values.count { it > 0 })
                put("totalAlphabetUses", completeAlphabetCounts.values.sum())

                // Diccionario completo de contadores del abecedario (A -> conteo, B -> conteo...)
                val alphabetObj = JSONObject()
                completeAlphabetCounts.forEach { (k, v) -> alphabetObj.put(k, v) }
                put("alphabetCounts", alphabetObj)

                // 6. Array detallado con los 100 números (1 al 100) y sus contadores exactos
                val numbersArray = JSONArray()
                val completeNumbersCounts = mutableMapOf<Int, Int>()

                val baseNumbers = if (allNumbersDetails.isNotEmpty()) {
                    allNumbersDetails
                } else {
                    standardNumbersCatalog.map { std ->
                        std.copy(useCount = numberCounts[std.number] ?: 0)
                    }
                }

                baseNumbers.forEach { numRecord ->
                    completeNumbersCounts[numRecord.number] = numRecord.useCount
                    val obj = JSONObject().apply {
                        put("number", numRecord.number)
                        put("word", numRecord.word)
                        put("ipa", numRecord.ipa)
                        put("phoneticSp", numRecord.phoneticSp)
                        put("isDecade", numRecord.isDecade)
                        put("useCount", numRecord.useCount)
                    }
                    numbersArray.put(obj)
                }

                // Si hubiese números adicionales en numberCounts
                numberCounts.forEach { (k, v) ->
                    if (!completeNumbersCounts.containsKey(k)) {
                        completeNumbersCounts[k] = v
                        val obj = JSONObject().apply {
                            put("number", k)
                            put("word", k.toString())
                            put("ipa", "")
                            put("phoneticSp", "")
                            put("isDecade", (k % 10 == 0))
                            put("useCount", v)
                        }
                        numbersArray.put(obj)
                    }
                }
                put("allNumbers", numbersArray)

                // Metadatos específicos de los números
                put("totalNumbers", completeNumbersCounts.size)
                put("usedNumbersCount", completeNumbersCounts.values.count { it > 0 })
                put("totalNumberUses", completeNumbersCounts.values.sum())

                // Diccionario completo de contadores de números ("1" -> conteo, "2" -> conteo... "100" -> conteo)
                val numbersObj = JSONObject()
                completeNumbersCounts.forEach { (k, v) -> numbersObj.put(k.toString(), v) }
                put("numberCounts", numbersObj)

                // 7. Array detallado con las 400 frases cotidianas (1 al 400) y sus contadores exactos
                val phrasesArray = JSONArray()
                val completePhrasesCounts = mutableMapOf<Int, Int>()

                val basePhrases = if (allPhrasesDetails.isNotEmpty()) {
                    allPhrasesDetails
                } else {
                    standardPhrasesCatalog.map { std ->
                        std.copy(useCount = phraseCounts[std.id] ?: 0)
                    }
                }

                basePhrases.forEach { phraseRecord ->
                    completePhrasesCounts[phraseRecord.id] = phraseRecord.useCount
                    val obj = JSONObject().apply {
                        put("id", phraseRecord.id)
                        put("spanishText", phraseRecord.spanishText)
                        put("category", phraseRecord.category)
                        put("englishHint", phraseRecord.englishHint)
                        put("useCount", phraseRecord.useCount)
                    }
                    phrasesArray.put(obj)
                }

                // Si hubiese frases adicionales en phraseCounts
                phraseCounts.forEach { (k, v) ->
                    if (!completePhrasesCounts.containsKey(k)) {
                        completePhrasesCounts[k] = v
                        val obj = JSONObject().apply {
                            put("id", k)
                            put("spanishText", "")
                            put("category", "General")
                            put("englishHint", "")
                            put("useCount", v)
                        }
                        phrasesArray.put(obj)
                    }
                }
                put("allPhrases", phrasesArray)

                // Metadatos específicos de las frases cotidianas
                put("totalPhrases", completePhrasesCounts.size)
                put("usedPhrasesCount", completePhrasesCounts.values.count { it > 0 })
                put("totalPhraseUses", completePhrasesCounts.values.sum())

                // Diccionario completo de contadores de frases ("1" -> conteo, "2" -> conteo... "50" -> conteo)
                val phrasesObj = JSONObject()
                completePhrasesCounts.forEach { (k, v) -> phrasesObj.put(k.toString(), v) }
                put("phraseCounts", phrasesObj)
            }

            // Guardar también en el almacenamiento rápido VocabularyStorage
            if (vocabularyWordCounts.isNotEmpty()) {
                com.example.data.local.VocabularyStorage(context).saveCounts(vocabularyWordCounts)
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
     * Sincroniza y actualiza la escucha y uso de frases en el archivo .json persistido.
     */
    fun updatePhraseCountsInBackup(context: Context, updatedPhraseCounts: Map<Int, Int>) {
        try {
            val existing = loadLatestBackup(context)
            if (existing != null) {
                saveFullBackup(
                    context = context,
                    vocabularyWordCounts = existing.vocabularyWordCounts,
                    customWords = existing.customWords,
                    usedWordsCount = existing.usedWordsCount,
                    notes = existing.notes,
                    alphabetCounts = existing.alphabetCounts,
                    numberCounts = existing.numberCounts,
                    phraseCounts = updatedPhraseCounts
                )
            } else {
                saveFullBackup(
                    context = context,
                    phraseCounts = updatedPhraseCounts
                )
            }
        } catch (_: Exception) {}
    }

    /**
     * Escribe el archivo en Documentos/VocabFlow/vocabflow_backup.json para que sobreviva a la desinstalación.
     * Soporta MediaStore en Android 10+ (Q+) para compatibilidad con Scoped Storage sin fallos.
     */
    private fun saveToPublicDocuments(context: Context, bytes: ByteArray) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val queryUri = android.provider.MediaStore.Files.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val projection = arrayOf(android.provider.MediaStore.MediaColumns._ID)
                val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
                val selectionArgs = arrayOf(BACKUP_FILENAME, "%$FOLDER_NAME%")

                var existingUri: android.net.Uri? = null
                resolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns._ID))
                        existingUri = android.content.ContentUris.withAppendedId(queryUri, id)
                    }
                }

                val targetUri = existingUri ?: run {
                    val values = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, BACKUP_FILENAME)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/json")
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/$FOLDER_NAME")
                    }
                    resolver.insert(queryUri, values)
                }

                if (targetUri != null) {
                    resolver.openOutputStream(targetUri, "rwt")?.use { fos ->
                        fos.write(bytes)
                        fos.flush()
                    } ?: resolver.openOutputStream(targetUri)?.use { fos ->
                        fos.write(bytes)
                        fos.flush()
                    }
                    return
                }
            } catch (_: Exception) {}
        }

        try {
            val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val vocabFlowDir = File(docsDir, FOLDER_NAME)
            if (!vocabFlowDir.exists()) vocabFlowDir.mkdirs()
            val targetFile = File(vocabFlowDir, BACKUP_FILENAME)
            FileOutputStream(targetFile).use { fos ->
                fos.write(bytes)
                fos.flush()
            }
            return
        } catch (_: Exception) {}

        try {
            val dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val targetFile = File(File(dlDir, FOLDER_NAME).apply { mkdirs() }, BACKUP_FILENAME)
            FileOutputStream(targetFile).use { fos ->
                fos.write(bytes)
                fos.flush()
            }
        } catch (_: Exception) {}
    }

    /**
     * Verifica si existe una copia previa persistida en el teléfono (ej. de antes de desinstalar).
     */
    fun hasSurvivingBackup(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val queryUri = android.provider.MediaStore.Files.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val projection = arrayOf(android.provider.MediaStore.MediaColumns._ID)
                val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
                val selectionArgs = arrayOf(BACKUP_FILENAME, "%$FOLDER_NAME%")
                resolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
                    if (cursor.moveToFirst()) return true
                }
            } catch (_: Exception) {}
        }

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val queryUri = android.provider.MediaStore.Files.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val projection = arrayOf(
                    android.provider.MediaStore.MediaColumns._ID,
                    android.provider.MediaStore.MediaColumns.DATE_MODIFIED
                )
                val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
                val selectionArgs = arrayOf(BACKUP_FILENAME, "%$FOLDER_NAME%")

                resolver.query(queryUri, projection, selection, selectionArgs, "${android.provider.MediaStore.MediaColumns.DATE_MODIFIED} DESC")?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns._ID))
                        val fileUri = android.content.ContentUris.withAppendedId(queryUri, id)
                        resolver.openInputStream(fileUri)?.bufferedReader(StandardCharsets.UTF_8)?.use { reader ->
                            val parsed = parseJsonContent(reader.readText())
                            if (parsed != null) return parsed
                        }
                    }
                }
            } catch (_: Exception) {}
        }

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

            // Contadores y lista detallada de las 26 letras del abecedario
            val alphabetMap = mutableMapOf<String, Int>()
            val alphabetList = mutableListOf<AlphabetLetterRecord>()

            if (root.has("allAlphabetLetters")) {
                val array = root.getJSONArray("allAlphabetLetters")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val letter = obj.optString("letter", "").uppercase().trim()
                    val count = obj.optInt("useCount", 0)
                    if (letter.isNotBlank()) {
                        alphabetMap[letter] = count
                        alphabetList.add(
                            AlphabetLetterRecord(
                                index = obj.optInt("index", i + 1),
                                letter = letter,
                                ipa = obj.optString("ipa", ""),
                                phoneticSp = obj.optString("phoneticSp", ""),
                                isVowel = obj.optBoolean("isVowel", false),
                                note = obj.optString("note", ""),
                                useCount = count
                            )
                        )
                    }
                }
            }

            if (root.has("alphabetCounts")) {
                val alphabetObj = root.getJSONObject("alphabetCounts")
                val keys = alphabetObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val cleanKey = k.uppercase().trim()
                    val count = alphabetObj.optInt(k, 0)
                    alphabetMap[cleanKey] = count
                }
            }

            // Asegurar que las 26 letras estándar existen en alphabetMap (con 0 si no se practicaron aún)
            standardAlphabetCatalog.forEach { std ->
                if (!alphabetMap.containsKey(std.letter)) {
                    alphabetMap[std.letter] = 0
                }
                if (alphabetList.none { it.letter == std.letter }) {
                    alphabetList.add(std.copy(useCount = alphabetMap[std.letter] ?: 0))
                }
            }
            alphabetList.sortBy { it.index }

            val totalAlphabetLetters = root.optInt("totalAlphabetLetters", maxOf(26, alphabetMap.size))
            val usedAlphabetLettersCount = root.optInt("usedAlphabetLettersCount", alphabetMap.values.count { it > 0 })

            // Contadores y lista detallada de los 100 números (1 al 100)
            val numbersMap = mutableMapOf<Int, Int>()
            val numbersList = mutableListOf<NumberRecord>()

            if (root.has("allNumbers")) {
                val array = root.getJSONArray("allNumbers")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val num = obj.optInt("number", 0)
                    val count = obj.optInt("useCount", 0)
                    if (num > 0) {
                        numbersMap[num] = count
                        numbersList.add(
                            NumberRecord(
                                number = num,
                                word = obj.optString("word", ""),
                                ipa = obj.optString("ipa", ""),
                                phoneticSp = obj.optString("phoneticSp", ""),
                                isDecade = obj.optBoolean("isDecade", false),
                                useCount = count
                            )
                        )
                    }
                }
            }

            if (root.has("numberCounts")) {
                val numbersObj = root.getJSONObject("numberCounts")
                val keys = numbersObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val num = k.toIntOrNull()
                    if (num != null) {
                        val count = numbersObj.optInt(k, 0)
                        numbersMap[num] = count
                    }
                }
            }

            // Asegurar que todos los 100 números estándar existen en numbersMap (con 0 si no se practicaron aún)
            standardNumbersCatalog.forEach { std ->
                if (!numbersMap.containsKey(std.number)) {
                    numbersMap[std.number] = 0
                }
                if (numbersList.none { it.number == std.number }) {
                    numbersList.add(std.copy(useCount = numbersMap[std.number] ?: 0))
                }
            }
            numbersList.sortBy { it.number }

            val totalNumbers = root.optInt("totalNumbers", maxOf(100, numbersMap.size))
            val usedNumbersCount = root.optInt("usedNumbersCount", numbersMap.values.count { it > 0 })

            // Contadores y lista detallada de las 50 frases cotidianas (1 al 50)
            val phrasesMap = mutableMapOf<Int, Int>()
            val phrasesList = mutableListOf<PhraseRecord>()

            if (root.has("allPhrases")) {
                val array = root.getJSONArray("allPhrases")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.optInt("id", 0)
                    val count = obj.optInt("useCount", 0)
                    if (id > 0) {
                        phrasesMap[id] = count
                        phrasesList.add(
                            PhraseRecord(
                                id = id,
                                spanishText = obj.optString("spanishText", ""),
                                category = obj.optString("category", ""),
                                englishHint = obj.optString("englishHint", ""),
                                useCount = count
                            )
                        )
                    }
                }
            }

            if (root.has("phraseCounts")) {
                val phrasesObj = root.getJSONObject("phraseCounts")
                val keys = phrasesObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val id = k.toIntOrNull()
                    if (id != null) {
                        val count = phrasesObj.optInt(k, 0)
                        phrasesMap[id] = count
                    }
                }
            }

            // Asegurar que todas las 400 frases estándar existen en phrasesMap (con 0 si no se practicaron aún)
            standardPhrasesCatalog.forEach { std ->
                if (!phrasesMap.containsKey(std.id)) {
                    phrasesMap[std.id] = 0
                }
                if (phrasesList.none { it.id == std.id }) {
                    phrasesList.add(std.copy(useCount = phrasesMap[std.id] ?: 0))
                }
            }
            phrasesList.sortBy { it.id }

            val totalPhrases = root.optInt("totalPhrases", maxOf(400, phrasesMap.size))
            val usedPhrasesCount = root.optInt("usedPhrasesCount", phrasesMap.values.count { it > 0 })

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

            // Frases armadas guardadas desde el lienzo de pruebas
            val savedDraftPhrasesList = mutableListOf<SavedDraftPhrase>()
            if (root.has("savedDraftPhrases")) {
                val array = root.getJSONArray("savedDraftPhrases")
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val wordsList = mutableListOf<String>()
                    if (obj.has("words")) {
                        val wArr = obj.getJSONArray("words")
                        for (wIdx in 0 until wArr.length()) {
                            wordsList.add(wArr.getString(wIdx))
                        }
                    }
                    val phraseText = obj.optString("phraseText", wordsList.joinToString(" "))
                    val id = obj.optString("id", java.util.UUID.randomUUID().toString())
                    val spanishTranslation = obj.optString("spanishTranslation", "")
                    val createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    if (phraseText.isNotBlank() || wordsList.isNotEmpty()) {
                        savedDraftPhrasesList.add(
                            SavedDraftPhrase(
                                id = id,
                                phraseText = phraseText,
                                words = wordsList,
                                spanishTranslation = spanishTranslation,
                                createdAt = createdAt
                            )
                        )
                    }
                }
            }

            val effectiveUsedWords = if (usedWordsCount > 0) usedWordsCount else vocabCountsMap.values.count { it > 0 }
            val totalVocabUses = root.optInt("totalVocabularyUses", vocabCountsMap.values.sum())

            // Fondo de pantalla activo y nivel de atenuación / tonalidad
            var parsedWallpaperId: String? = null
            var parsedWallpaperDimAlpha: Float? = null

            if (root.has("activeWallpaper")) {
                val wpObj = root.optJSONObject("activeWallpaper")
                if (wpObj != null) {
                    val id = wpObj.optString("id", "").trim()
                    if (id.isNotBlank()) parsedWallpaperId = id
                    if (wpObj.has("dimAlpha")) parsedWallpaperDimAlpha = wpObj.optDouble("dimAlpha").toFloat()
                }
            }
            if (parsedWallpaperId == null && root.has("wallpaperId")) {
                val id = root.optString("wallpaperId", "").trim()
                if (id.isNotBlank()) parsedWallpaperId = id
            }
            if (parsedWallpaperDimAlpha == null && root.has("wallpaperDimAlpha")) {
                parsedWallpaperDimAlpha = root.optDouble("wallpaperDimAlpha").toFloat()
            }

            RestoredBackupData(
                appName = appName,
                version = version,
                timestamp = timestamp,
                usedWordsCount = effectiveUsedWords,
                totalWords = totalWords,
                totalVocabularyUses = totalVocabUses,
                vocabularyWordCounts = vocabCountsMap,
                customWords = customWordsList,
                totalAlphabetLetters = totalAlphabetLetters,
                usedAlphabetLettersCount = usedAlphabetLettersCount,
                allAlphabetLetters = alphabetList,
                alphabetCounts = alphabetMap,
                totalNumbers = totalNumbers,
                usedNumbersCount = usedNumbersCount,
                allNumbers = numbersList,
                numberCounts = numbersMap,
                totalPhrases = totalPhrases,
                usedPhrasesCount = usedPhrasesCount,
                allPhrases = phrasesList,
                phraseCounts = phrasesMap,
                notes = notesList,
                savedDraftPhrases = savedDraftPhrasesList,
                wallpaperId = parsedWallpaperId,
                wallpaperDimAlpha = parsedWallpaperDimAlpha
            )
        } catch (_: Exception) {
            null
        }
    }

    fun getPublicStoragePath(): String {
        return "Documentos/VocabFlow/$BACKUP_FILENAME"
    }
}
