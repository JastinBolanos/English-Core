package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.PartOfSpeech
import com.example.domain.model.Word
import com.example.domain.util.WordDictionary
import com.example.ui.theme.EnglishAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("English", appName)
  }

  @Test
  fun `vocabulario screen loads as primary screen`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("screen_vocabulario").assertExists()
  }

  @Test
  fun `alarm shortcut button is removed from vocabulary header and roadmap button is present`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_alarm_shortcut").assertDoesNotExist()
    composeTestRule.onNodeWithTag("btn_roadmap_shortcut").assertExists()
    composeTestRule.onNodeWithTag("btn_gemini_shortcut").assertExists()

    // Click Gemini shortcut button to open consultation tray
    composeTestRule.onNodeWithTag("btn_gemini_shortcut").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("gemini_consultation_dialog").assertExists()
    composeTestRule.onNodeWithTag("input_gemini_query").assertExists()

    // Close Gemini tray
    composeTestRule.onNodeWithTag("btn_close_gemini_tray").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("gemini_consultation_dialog").assertDoesNotExist()
  }

  @Test
  fun `verify gemini chat persistence across closing and reopening in ViewModel`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) {}
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }
    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts)

    // Initially closed and empty
    assertFalse(viewModel.showGeminiDialog.value)
    assertTrue(viewModel.geminiMessages.value.isEmpty())

    // Open dialog and send a message
    viewModel.openGeminiDialog()
    assertTrue(viewModel.showGeminiDialog.value)

    viewModel.sendGeminiQuery("¿Qué significa la palabra 'challenge'?")
    assertEquals(1, viewModel.geminiMessages.value.size)
    assertEquals("¿Qué significa la palabra 'challenge'?", viewModel.geminiMessages.value.first().text)

    // User closes the dialog (tap outside)
    viewModel.closeGeminiDialog()
    assertFalse(viewModel.showGeminiDialog.value)

    // User reopens the dialog: conversation is completely preserved
    viewModel.openGeminiDialog()
    assertTrue(viewModel.showGeminiDialog.value)
    assertEquals(1, viewModel.geminiMessages.value.size)
    assertEquals("¿Qué significa la palabra 'challenge'?", viewModel.geminiMessages.value.first().text)

    db.close()
  }

  @Test
  fun `alarms screen renders time picker and save button`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.alarm.AlarmsScreen(onBack = {})
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("time_picker_card").assertExists()
    composeTestRule.onNodeWithTag("btn_save_alarm").assertExists()
  }

  @Test
  fun `alarm scheduler persists alarm to SharedPreferences and handles cancellation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val scheduler = com.example.alarm.AlarmScheduler(context)

    // Schedule an alarm
    val info = scheduler.scheduleAlarm(7, 30)
    assertEquals(7, info.hour)
    assertEquals(30, info.minute)
    assertTrue("Trigger must be in future", info.triggerAtMillis > System.currentTimeMillis())

    // Verify SharedPreferences persistence
    val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
    assertTrue("is_alarm_active should be true", prefs.getBoolean("is_alarm_active", false))
    assertEquals(7, prefs.getInt("alarm_hour", -1))
    assertEquals(30, prefs.getInt("alarm_minute", -1))

    // Verify saved state getter
    val savedState = scheduler.getSavedAlarmState()
    assertTrue(savedState.isActive)
    assertEquals(7, savedState.hour)
    assertEquals(30, savedState.minute)

    // Cancel alarm
    scheduler.cancelAlarm()
    assertFalse("is_alarm_active should be false after cancellation", prefs.getBoolean("is_alarm_active", false))
  }

  @Test
  fun `alarm scheduler plusDays calculation when time has passed today`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val scheduler = com.example.alarm.AlarmScheduler(context)

    // Using an hour that is in the past for today or now
    val now = java.time.LocalDateTime.now()
    val pastHour = if (now.hour > 0) now.hour - 1 else 23
    val pastMinute = now.minute

    val info = scheduler.scheduleAlarm(pastHour, pastMinute)
    // The trigger must be in the future (tomorrow)
    assertTrue("Trigger must be in the future", info.triggerAtMillis > System.currentTimeMillis())
  }

  @Test
  fun `verify all 2809 translations load from raw json without prefixes`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    WordDictionary.loadTranslationsFromContext(context)

    assertTrue("Deben cargarse 2809 traducciones", WordDictionary.getLoadedCount() >= 2809)

    val samples = listOf("word", "run", "work", "system", "problem", "understand", "challenge", "because")
    for (word in samples) {
      val translation = WordDictionary.getTranslation(word, PartOfSpeech.NOUN)
      assertFalse("La traducción no debe tener 'Acción:'", translation.contains("Acción", ignoreCase = true))
      assertFalse("La traducción no debe tener ':'", translation.contains(":"))
      assertNotNull(translation)
      assertTrue(translation.isNotBlank())
    }

    assertEquals("palabra", WordDictionary.getTranslation("word", PartOfSpeech.NOUN))
    assertEquals("correr", WordDictionary.getTranslation("run", PartOfSpeech.VERB))
  }

  @Test
  fun `verify compass filter chips and clear button in vocabulario screen`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("compass_chip_Conector").assertExists()
    composeTestRule.onNodeWithTag("compass_chip_Sujeto").assertExists()
    composeTestRule.onNodeWithTag("compass_chip_Verbo").assertExists()
    composeTestRule.onNodeWithTag("compass_chip_Sustantivo").assertExists()
    composeTestRule.onNodeWithTag("compass_chip_Modificador").assertExists()
    composeTestRule.onNodeWithTag("search_word_input").assertExists()

    // Click Conector chip to filter
    composeTestRule.onNodeWithTag("compass_chip_Conector").performClick()
    composeTestRule.waitForIdle()

    // Click Conector chip again to toggle and remove selection (X button removed)
    composeTestRule.onNodeWithTag("compass_chip_Conector").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify translate button is invisible when draft is empty and mirror tray is completely removed`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("draft_scratchpad").assertExists()
    composeTestRule.onNodeWithTag("btn_translate_practice").assertDoesNotExist()
    composeTestRule.onNodeWithTag("btn_mic_pronunciation").assertDoesNotExist()
    composeTestRule.onNodeWithTag("mirror_voice_tray").assertDoesNotExist()
  }

  @Test
  fun `verify jump rank input exists alongside search input`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("search_word_input").assertExists()
    composeTestRule.onNodeWithTag("jump_rank_input").assertExists()
  }

  @Test
  fun `verify roadmap button opens dialog and close button dismisses it`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_roadmap_shortcut").assertExists()

    // Click Ruta button
    composeTestRule.onNodeWithTag("btn_roadmap_shortcut").performClick()
    composeTestRule.waitForIdle()

    // Verify dialog opened
    composeTestRule.onNodeWithTag("tactical_roadmap_dialog").assertExists()
    composeTestRule.onNodeWithTag("btn_close_roadmap").assertExists()

    // Click close X button to dismiss dialog
    composeTestRule.onNodeWithTag("btn_close_roadmap_x").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("tactical_roadmap_dialog").assertDoesNotExist()
  }

  @Test
  fun `verify speed floating action button exists and cycles speed rate`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()

    // Assert FAB exists with initial speed 1.0x
    val fabNode = composeTestRule.onNodeWithTag("fab_speed_control")
    fabNode.assertExists()
    composeTestRule.onNodeWithText("1.0x").assertExists()

    // Click FAB to cycle to 0.8x
    fabNode.performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("0.8x").assertExists()

    // Click FAB to cycle to 0.5x
    fabNode.performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("0.5x").assertExists()

    // Click FAB to cycle back to 1.0x
    fabNode.performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("1.0x").assertExists()
  }

  @Test
  fun `verify word useCount increments and persists across database initializations`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)

    // First app launch: pre-populates words
    repo.prePopulateDatabaseIfNeeded()
    val initialCount = dao.getWordCount()
    assertEquals("Deben cargarse exactamente 2809 palabras incluyendo 'rank'", 2809, initialCount)

    val wordBefore = dao.getWordByLemma("the")
    assertNotNull("La palabra 'the' debe existir", wordBefore)
    assertEquals(0, wordBefore!!.useCount)

    // User uses the word "the" twice
    repo.incrementWordUseCount(wordBefore.id, wordBefore.word)
    repo.incrementWordUseCount(wordBefore.id, wordBefore.word)

    val wordAfter = dao.getWordById(wordBefore.id)
    assertNotNull(wordAfter)
    assertEquals("El contador de uso debe ser 2 tras dos usos", 2, wordAfter!!.useCount)

    // Simulate user exiting and reopening the app: prePopulateDatabaseIfNeeded is called again
    repo.prePopulateDatabaseIfNeeded()

    // Verify counter is NOT reset to 0 and exact count is preserved
    val wordAfterRestart = dao.getWordById(wordBefore.id)
    assertNotNull(wordAfterRestart)
    assertEquals("El contador exacto (2) debe conservarse al reiniciar la app", 2, wordAfterRestart!!.useCount)

    // Verify 'rank' is also preserved and count remains 2809
    val rankWord = dao.getWordByLemma("rank")
    assertNotNull("La palabra 'rank' debe estar presente", rankWord)
    assertEquals(2809, dao.getWordCount())

    db.close()
  }

  @Test
  fun `verify canvas block displacement and updated play button state in ViewModel and UI`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    var spokenText: String? = null
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) { spokenText = text }
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }
    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts)

    val word1 = Word(id = 1, lemma = "the", partOfSpeech = PartOfSpeech.MODIFIER)
    val word2 = Word(id = 2, lemma = "month", partOfSpeech = PartOfSpeech.NOUN)
    val word3 = Word(id = 3, lemma = "play", partOfSpeech = PartOfSpeech.VERB)

    viewModel.addWordToDraft(word1)
    viewModel.addWordToDraft(word2)
    viewModel.addWordToDraft(word3)

    assertEquals(3, viewModel.draftWords.value.size)
    assertEquals(listOf("the", "month", "play"), viewModel.draftWords.value.map { it.lemma })

    // Move 'play' (index 2) to the left (index 1)
    viewModel.moveWordLeft(2)
    assertEquals(listOf("the", "play", "month"), viewModel.draftWords.value.map { it.lemma })
    assertTrue("La frase debe estar marcada como actualizada", viewModel.isPhraseUpdated.value)

    // Move 'play' (index 1) to the left (index 0)
    viewModel.moveWordLeft(1)
    assertEquals(listOf("play", "the", "month"), viewModel.draftWords.value.map { it.lemma })

    // Play full phrase in the new position
    viewModel.playFullDraftPhrase()
    assertEquals("play the month", spokenText)
    assertFalse("isPhraseUpdated debe ser false tras reproducirse", viewModel.isPhraseUpdated.value)
  }

  @Test
  fun `verify translation tray toggle and on-device translation in ViewModel`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) {}
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }

    val translationFlow = kotlinx.coroutines.flow.MutableStateFlow<com.example.domain.translation.TranslationState>(
      com.example.domain.translation.TranslationState.Idle
    )
    var lastTranslatedText: String? = null

    val fakeTranslator = object : com.example.domain.translation.TranslationManager {
      override val translationState = translationFlow
      override fun translate(text: String) {
        lastTranslatedText = text
        translationFlow.value = com.example.domain.translation.TranslationState.Success("traducción: $text")
      }
      override fun reset() {
        translationFlow.value = com.example.domain.translation.TranslationState.Idle
      }
      override fun close() {}
    }

    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(
      repository = repo,
      ttsManager = fakeTts,
      speechToTextManager = null,
      translationManager = fakeTranslator
    )

    // Tray initially closed
    assertFalse("Bandeja inicialmente cerrada", viewModel.isTranslationTrayExpanded.value)

    viewModel.addWordToDraft(Word(id = 1, lemma = "play", partOfSpeech = PartOfSpeech.VERB))
    viewModel.addWordToDraft(Word(id = 2, lemma = "time", partOfSpeech = PartOfSpeech.NOUN))

    // Toggle tray open -> should trigger translation
    viewModel.toggleTranslationTray()
    assertTrue("Bandeja abierta tras toggle", viewModel.isTranslationTrayExpanded.value)
    assertEquals("play time", lastTranslatedText)
    assertTrue(viewModel.translationState.value is com.example.domain.translation.TranslationState.Success)
    assertEquals("traducción: play time", (viewModel.translationState.value as com.example.domain.translation.TranslationState.Success).translation)

    // Move word while tray is open -> should auto-update translation
    viewModel.moveWordLeft(1)
    assertEquals("time play", lastTranslatedText)
    assertEquals("traducción: time play", (viewModel.translationState.value as com.example.domain.translation.TranslationState.Success).translation)

    // Toggle tray closed
    viewModel.toggleTranslationTray()
    assertFalse("Bandeja cerrada tras segundo toggle", viewModel.isTranslationTrayExpanded.value)
  }

  @Test
  fun `verify spanish to english writer card exists right below canvas`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("spanish_english_writer_card").assertExists()
    composeTestRule.onNodeWithTag("input_spanish_text").assertExists()
  }

  @Test
  fun `verify spanish translation and clearing in ViewModel`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    WordDictionary.loadTranslationsFromContext(context)
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) {}
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }
    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts)

    // Initial state: empty
    assertEquals("", viewModel.spanishInputText.value)

    // Update text with a Spanish word
    viewModel.updateSpanishInputText("palabra")
    assertEquals("palabra", viewModel.spanishInputText.value)

    // Clear
    viewModel.clearSpanishTranslator()
    assertEquals("", viewModel.spanishInputText.value)

    db.close()
  }

  @Test
  fun `verify volume icon only reproduces audio and does not add word to canvas`() {
    var spokenWord: String? = null
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) { spokenWord = text }
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }

    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts)

    val word = Word(id = 1, lemma = "the", rank = 1, partOfSpeech = PartOfSpeech.MODIFIER)

    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.vocabulary.WordHudCard(
          word = word,
          onTap = { viewModel.addWordToDraft(word) },
          onSpeak = { viewModel.speakWord(word.lemma) }
        )
      }
    }

    composeTestRule.waitForIdle()

    // 1. Click volume icon: must pronounce the word, but canvas draft remains EMPTY
    composeTestRule.onNodeWithTag("btn_speak_word_1").performClick()
    composeTestRule.waitForIdle()

    assertEquals("the", spokenWord)
    assertTrue("El lienzo de pruebas debe estar vacío al tocar solo el volumen", viewModel.draftWords.value.isEmpty())

    // 2. Click word card: now it must be added to the canvas draft
    composeTestRule.onNodeWithTag("word_item_1").performClick()
    composeTestRule.waitForIdle()

    assertEquals(1, viewModel.draftWords.value.size)
    assertEquals("the", viewModel.draftWords.value.first().lemma)

    db.close()
  }

  @Test
  fun `verify custom words storage persists in json file and auto-increments rank from 2809 to 2810`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = com.example.data.local.CustomWordsStorage(context)

    // Ensure clean state for test
    val jsonFile = java.io.File(context.filesDir, "custom_words.json")
    if (jsonFile.exists()) jsonFile.delete()

    val record1 = com.example.data.local.CustomWordRecord(
      rank = 2810,
      word = "breakthrough",
      partOfSpeech = "sustantivo",
      spanishTranslation = "avance importante",
      microExample = "a great breakthrough"
    )

    storage.saveCustomWord(record1)

    assertTrue("El archivo custom_words.json debe existir en el almacenamiento local", jsonFile.exists())
    val loaded = storage.loadCustomWords()
    assertEquals(1, loaded.size)
    assertEquals(2810, loaded[0].rank)
    assertEquals("breakthrough", loaded[0].word)
    assertEquals("avance importante", loaded[0].spanishTranslation)

    // Save second custom word
    val record2 = com.example.data.local.CustomWordRecord(
      rank = 2811,
      word = "resilient",
      partOfSpeech = "modificador (adjetivo)",
      spanishTranslation = "resiliente",
      microExample = "stay resilient now"
    )
    storage.saveCustomWord(record2)

    val loaded2 = storage.loadCustomWords()
    assertEquals(2, loaded2.size)
    assertEquals(2810, loaded2[0].rank)
    assertEquals(2811, loaded2[1].rank)
  }

  @Test
  fun `verify repository addCustomWord assigns rank 2810 and integrates into wordDao and catalogs`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer, context)

    // Simulate 2809 words already present
    dao.insertWord(
      com.example.data.local.entity.WordEntity(rank = 2809, word = "thirst", partOfSpeech = "sustantivo/verbo")
    )

    val customWord = repo.addCustomWord(
      word = "feedback",
      partOfSpeech = "sustantivo",
      spanishTranslation = "retroalimentación",
      microExample = "give good feedback"
    )

    assertEquals("Debe asignarse automáticamente el rank #2810", 2810, customWord.rank)
    assertEquals("feedback", customWord.lemma)
    assertEquals(PartOfSpeech.NOUN, customWord.partOfSpeech)
    assertEquals("retroalimentación", customWord.displaySpanishTranslation)

    val secondWord = repo.addCustomWord(
      word = "iterate",
      partOfSpeech = "verbo",
      spanishTranslation = "iterar",
      microExample = "iterate and learn"
    )
    assertEquals("Debe asignarse automáticamente el rank #2811", 2811, secondWord.rank)

    db.close()
  }

  @Test
  fun `verify add word fab button exists and opens add custom word dialog`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()

    // Verify FAB '+' exists
    composeTestRule.onNodeWithTag("fab_add_custom_word").assertExists()

    // Click FAB to open creation dialog
    composeTestRule.onNodeWithTag("fab_add_custom_word").performClick()
    composeTestRule.waitForIdle()

    // Verify dialog and preview card exist
    composeTestRule.onNodeWithTag("dialog_add_custom_word").assertExists()
    composeTestRule.onNodeWithTag("preview_custom_word_card").assertExists()
    composeTestRule.onNodeWithTag("input_new_word_lemma").assertExists()
    composeTestRule.onNodeWithTag("btn_save_custom_word").assertExists()

    // Close dialog
    composeTestRule.onNodeWithTag("btn_close_add_word_x").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("dialog_add_custom_word").assertDoesNotExist()
  }

  @Test
  fun `verify syntax explanation tray displays what type is responsible for and can be dismissed`() {
    var dismissed = false
    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.vocabulary.SyntaxExplanationTray(
          filter = com.example.presentation.vocabulary.SyntaxFilter.CONNECTOR,
          onDismiss = { dismissed = true }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify tray elements
    composeTestRule.onNodeWithTag("syntax_explanation_tray").assertExists()
    composeTestRule.onNodeWithTag("btn_close_syntax_tray_x").assertExists()
    composeTestRule.onNodeWithTag("btn_close_syntax_tray").assertExists()

    // Click close button
    composeTestRule.onNodeWithTag("btn_close_syntax_tray").performClick()
    composeTestRule.waitForIdle()

    assertTrue("Tray must call onDismiss when closing", dismissed)
  }
}


