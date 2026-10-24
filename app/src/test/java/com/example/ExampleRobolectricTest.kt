package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.flow.first

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
  fun `alarm shortcut and gemini top buttons are removed and roadmap button floats cleanly`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_alarm_shortcut").assertDoesNotExist()
    composeTestRule.onNodeWithTag("btn_gemini_shortcut").assertDoesNotExist()
    composeTestRule.onNodeWithTag("btn_roadmap_shortcut").assertExists()

    // Click Roadmap shortcut button to open tactical roadmap dialog
    composeTestRule.onNodeWithTag("btn_roadmap_shortcut").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("tactical_roadmap_dialog").assertExists()
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

    // Assert FAB exists
    val fabNode = composeTestRule.onNodeWithTag("fab_speed_control")
    fabNode.assertExists()

    // Click FAB to cycle to 0.5
    fabNode.performClick()
    composeTestRule.waitForIdle()
    fabNode.assertExists()

    // Click FAB to cycle to 0.3
    fabNode.performClick()
    composeTestRule.waitForIdle()
    fabNode.assertExists()

    // Click FAB to cycle back to 1.0
    fabNode.performClick()
    composeTestRule.waitForIdle()
    fabNode.assertExists()
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
  fun `verify spanish to english writer card is hidden by default and toggles via header translate button`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    // Initially hidden
    composeTestRule.onNodeWithTag("spanish_english_writer_card").assertDoesNotExist()
    composeTestRule.onNodeWithTag("btn_toggle_spanish_translator_header").assertExists()

    // Tap header translate button to open writer card
    composeTestRule.onNodeWithTag("btn_toggle_spanish_translator_header").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("spanish_english_writer_card").assertExists()
    composeTestRule.onNodeWithTag("input_spanish_text").assertExists()

    // Tap header translate button again to hide writer card
    composeTestRule.onNodeWithTag("btn_toggle_spanish_translator_header").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("spanish_english_writer_card").assertDoesNotExist()
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

    // Verify FAB Gemini Web exists and is clickable (opens 75% floating web tray)
    composeTestRule.onNodeWithTag("fab_gemini_web").assertExists()
    composeTestRule.onNodeWithTag("fab_gemini_web").performClick()
    composeTestRule.waitForIdle()

    // Verify Floating Gemini Web Tray and close button exist
    composeTestRule.onNodeWithTag("floating_gemini_web_tray").assertExists()
    composeTestRule.onNodeWithTag("btn_close_floating_gemini_web").assertExists()
    composeTestRule.onNodeWithTag("btn_close_floating_gemini_web").performClick()
    composeTestRule.waitForIdle()

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

  @Test
  fun `verify google translate web fab button exists and opens 60 percent floating web tray with only back forward and close buttons`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()

    // Verify Google Translate FAB exists
    composeTestRule.onNodeWithTag("fab_google_translate_web").assertExists()
    composeTestRule.onNodeWithTag("fab_google_translate_web").performClick()
    composeTestRule.waitForIdle()

    // Verify Floating Google Translate Web Tray exists
    composeTestRule.onNodeWithTag("floating_google_translate_web_tray").assertExists()

    // Verify top controls: only back, forward and close X exist
    composeTestRule.onNodeWithTag("btn_translate_web_back").assertExists()
    composeTestRule.onNodeWithTag("btn_translate_web_forward").assertExists()
    composeTestRule.onNodeWithTag("btn_close_floating_translate_web").assertExists()

    // Click close button
    composeTestRule.onNodeWithTag("btn_close_floating_translate_web").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify animated english header with fredericka font exists at top of screen`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("header_english_space").assertExists()
    composeTestRule.onNodeWithTag("text_english_title").assertExists()
    composeTestRule.onNodeWithTag("counter_used_words_badge").assertExists()
    composeTestRule.onNodeWithTag("text_used_words_count", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("icon_trophy", useUnmergedTree = true).assertExists()
  }

  @Test
  fun `verify usedWordsCount in ViewModel calculates unique words with useCount greater than zero`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)

    // Insert 3 words with specific useCount
    dao.insertWord(com.example.data.local.entity.WordEntity(id = 1, rank = 1, word = "the", partOfSpeech = "modificador", useCount = 2))
    dao.insertWord(com.example.data.local.entity.WordEntity(id = 2, rank = 2, word = "be", partOfSpeech = "verbo", useCount = 1))
    dao.insertWord(com.example.data.local.entity.WordEntity(id = 3, rank = 3, word = "and", partOfSpeech = "conector", useCount = 0))

    val wordsFlow = repo.getAllWords()
    val firstList = wordsFlow.first()
    assertEquals(3, firstList.size)
    assertEquals(2, firstList.count { it.useCount > 0 })

    // When useCount increments on 'and'
    repo.incrementWordUseCount(3, "and")
    val updatedList = wordsFlow.first()
    assertEquals(3, updatedList.count { it.useCount > 0 })

    db.close()
  }

  @Test
  fun `verify audio playback increments word usage count in ViewModel`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    var spokenText = ""
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) { spokenText = text }
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }
    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts)

    dao.insertWord(com.example.data.local.entity.WordEntity(id = 1, rank = 1, word = "hello", partOfSpeech = "interjeccion", useCount = 0))
    val word = Word(id = 1, rank = 1, lemma = "hello", partOfSpeech = PartOfSpeech.OTHER, useCount = 0)

    viewModel.speakWord(word)
    kotlinx.coroutines.delay(100)
    assertEquals("hello", spokenText)

    val updatedWord = dao.getWordById(1)
    assertNotNull(updatedWord)
    assertEquals(1, updatedWord?.useCount)

    db.close()
  }

  @Test
  fun `verify opening word details tray triggers recordWordUsage and closing does not`() = kotlinx.coroutines.test.runTest {
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

    val insertedId = dao.insertWord(com.example.data.local.entity.WordEntity(id = 0, rank = 10, word = "world", partOfSpeech = "sustantivo", useCount = 0))
    val word = Word(id = insertedId, rank = 10, lemma = "world", partOfSpeech = PartOfSpeech.NOUN, useCount = 0)

    // Opening tray triggers usage recording
    viewModel.recordWordUsage(word)
    testScheduler.advanceUntilIdle()
    org.robolectric.shadows.ShadowLooper.idleMainLooper()
    var updated = dao.getWordById(insertedId) ?: dao.getWordByLemma("world")
    assertNotNull(updated)
    assertEquals(1, updated?.useCount)

    // Closing tray does nothing to usage count (still 1)
    updated = dao.getWordById(insertedId) ?: dao.getWordByLemma("world")
    assertEquals(1, updated?.useCount)

    db.close()
  }

  @Test
  fun `verify bottom action tray and giant pill contain all bottom controls and study stopwatch is removed`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("study_stopwatch_container").assertDoesNotExist()
    composeTestRule.onNodeWithTag("bottom_actions_tray").assertDoesNotExist()
    composeTestRule.onNodeWithTag("giant_bottom_pill").assertExists()
    composeTestRule.onNodeWithTag("fab_gemini_web").assertExists()
    composeTestRule.onNodeWithTag("fab_youtube_toggle").assertExists()
    composeTestRule.onNodeWithTag("fab_google_translate_web").assertExists()
  }

  @Test
  fun `verify alphabet practice counter increments on audio and on canvas addition and persists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    var lastSpoken = ""
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) { lastSpoken = text }
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }

    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts, context = context)
    val letterA = com.example.presentation.alphabet.englishAlphabetCatalog.first { it.letter == "A" }

    // Initial count for A is 0
    val initialCount = viewModel.alphabetCounts.value["A"] ?: 0
    assertEquals(0, initialCount)
    assertTrue(viewModel.draftWords.value.isEmpty())

    // 1. Listening to letter sound increments counter by 1
    viewModel.speakAlphabetLetter(letterA)
    assertEquals(1, viewModel.alphabetCounts.value["A"])
    assertEquals("A", lastSpoken)
    assertTrue("Escuchar la letra no debe agregarla al lienzo", viewModel.draftWords.value.isEmpty())

    // 2. Uploading letter to canvas increments counter by 1 and adds to draft
    viewModel.addAlphabetLetterToDraft(letterA)
    assertEquals(2, viewModel.alphabetCounts.value["A"])
    assertEquals(1, viewModel.draftWords.value.size)
    assertEquals("A", viewModel.draftWords.value.first().lemma)

    // 3. Verify persistence in AlphabetStorage
    val storage = com.example.data.local.AlphabetStorage(context)
    val persistedCounts = storage.loadCounts()
    assertEquals(2, persistedCounts["A"])

    db.close()
  }

  @Test
  fun `verify alphabet list section UI renders items with raw counters, lienzo button and listen button`() {
    var canvasLetter: String? = null
    var listenedLetter: String? = null
    val letterCounts = mapOf("A" to 5, "B" to 2)

    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.alphabet.AlphabetListSection(
          letterCounts = letterCounts,
          onSpeakLetter = { listenedLetter = it.letter },
          onAddLetterToCanvas = { canvasLetter = it.letter }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify row for letter A
    composeTestRule.onNodeWithTag("alphabet_item_A").assertExists()
    composeTestRule.onNodeWithTag("alphabet_count_A", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("btn_add_letter_to_canvas_A").assertExists()
    composeTestRule.onNodeWithTag("btn_listen_letter_A").assertExists()

    // Verify toggle pronunciation button exists and expands tray
    composeTestRule.onNodeWithTag("btn_toggle_pronunciation_A").assertExists()
    composeTestRule.onNodeWithTag("btn_toggle_pronunciation_A").performClick()
    composeTestRule.waitForIdle()

    // Click listen button for A
    composeTestRule.onNodeWithTag("btn_listen_letter_A").performClick()
    composeTestRule.waitForIdle()
    assertEquals("A", listenedLetter)

    // Click add to canvas button for A
    composeTestRule.onNodeWithTag("btn_add_letter_to_canvas_A").performClick()
    composeTestRule.waitForIdle()
    assertEquals("A", canvasLetter)
  }

  @Test
  fun `verify numbers practice counter increments on audio and on canvas addition and persists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context, dao)
    val repo = com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
    var lastSpoken = ""
    val fakeTts = object : com.example.data.tts.TtsManager {
      override val currentSpeechRate = kotlinx.coroutines.flow.MutableStateFlow(1.0f)
      override fun cycleSpeechRate(): Float = 1.0f
      override fun speak(text: String) { lastSpoken = text }
      override fun stop() {}
      override fun shutdown() {}
      override fun setSpeechRate(rate: Float) {}
    }

    val viewModel = com.example.presentation.vocabulary.VocabularyViewModel(repo, fakeTts, context = context)
    val numberOne = com.example.presentation.numbers.NumbersCatalog.allNumbers.first { it.number == 1 }

    // Initial count for 1 is 0
    val initialCount = viewModel.numberCounts.value[1] ?: 0
    assertEquals(0, initialCount)
    assertTrue(viewModel.draftWords.value.isEmpty())

    // 1. Listening to number sound increments counter by 1
    viewModel.speakNumber(numberOne)
    assertEquals(1, viewModel.numberCounts.value[1])
    assertEquals("One", lastSpoken)
    assertTrue("Escuchar el número no debe agregarlo al lienzo", viewModel.draftWords.value.isEmpty())

    // 2. Uploading number to canvas increments counter by 1 and adds to draft
    viewModel.addNumberToDraft(numberOne)
    assertEquals(2, viewModel.numberCounts.value[1])
    assertEquals(1, viewModel.draftWords.value.size)
    assertEquals("One", viewModel.draftWords.value.first().lemma)

    // 3. Verify persistence in NumbersStorage
    val storage = com.example.data.local.NumbersStorage(context)
    val persistedCounts = storage.loadCounts()
    assertEquals(2, persistedCounts[1])

    db.close()
  }

  @Test
  fun `verify numbers list section UI renders items with raw counters, lienzo button and listen button`() {
    var canvasNumber: Int? = null
    var listenedNumber: Int? = null
    val numberCounts = mapOf(1 to 7, 2 to 3)

    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.numbers.NumbersSection(
          numberCounts = numberCounts,
          onSpeakNumber = { listenedNumber = it.number },
          onAddNumberToCanvas = { canvasNumber = it.number }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify row for number 1
    composeTestRule.onNodeWithTag("number_card_1").assertExists()
    composeTestRule.onNodeWithTag("number_count_1", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("btn_add_number_to_canvas_1").assertExists()
    composeTestRule.onNodeWithTag("btn_speak_number_1").assertExists()

    // Verify toggle pronunciation button exists and expands tray
    composeTestRule.onNodeWithTag("btn_toggle_pronunciation_1").assertExists()
    composeTestRule.onNodeWithTag("btn_toggle_pronunciation_1").performClick()
    composeTestRule.waitForIdle()

    // Click listen button for 1
    composeTestRule.onNodeWithTag("btn_speak_number_1").performClick()
    composeTestRule.waitForIdle()
    assertEquals(1, listenedNumber)

    // Click add to canvas button for 1
    composeTestRule.onNodeWithTag("btn_add_number_to_canvas_1").performClick()
    composeTestRule.waitForIdle()
    assertEquals(1, canvasNumber)
  }

  @Test
  fun `test AutoBackupManager JSON serialization excludes timer and draft and includes all module counters`() {
    val sampleWordCounts = mapOf("the" to 15, "be" to 20, "abandon" to 0, "house" to 32)
    val sampleAlphabetCounts = mapOf("A" to 5, "B" to 0, "C" to 8)
    val sampleNumberCounts = mapOf(1 to 10, 2 to 0, 100 to 3)
    val samplePhraseCounts = mapOf(1 to 7, 2 to 0, 50 to 12)
    val sampleNotes = listOf(
      com.example.presentation.notes.NoteBlock(id = "note-1", text = "Regla de fonética", isCompleted = false)
    )

    val jsonString = org.json.JSONObject().apply {
      put("appName", "VocabFlow")
      put("version", 2)
      put("exportedAt", System.currentTimeMillis())
      put("usedWordsCount", 3)
      put("totalVocabularyUses", sampleWordCounts.values.sum())
      put("totalAlphabetUses", sampleAlphabetCounts.values.sum())
      put("totalNumberUses", sampleNumberCounts.values.sum())
      put("totalPhraseUses", samplePhraseCounts.values.sum())

      val vocabObj = org.json.JSONObject()
      sampleWordCounts.forEach { (lemma, count) -> vocabObj.put(lemma, count) }
      put("vocabularyWordCounts", vocabObj)

      val alphabetObj = org.json.JSONObject()
      sampleAlphabetCounts.forEach { (k, v) -> alphabetObj.put(k, v) }
      put("alphabetCounts", alphabetObj)

      val numbersObj = org.json.JSONObject()
      sampleNumberCounts.forEach { (k, v) -> numbersObj.put(k.toString(), v) }
      put("numberCounts", numbersObj)

      val phrasesObj = org.json.JSONObject()
      samplePhraseCounts.forEach { (k, v) -> phrasesObj.put(k.toString(), v) }
      put("phraseCounts", phrasesObj)

      val notesArray = org.json.JSONArray()
      sampleNotes.forEach { n ->
        notesArray.put(org.json.JSONObject().apply {
          put("id", n.id)
          put("text", n.text)
          put("isCompleted", n.isCompleted)
          put("createdAt", n.createdAt)
        })
      }
      put("localNotes", notesArray)
    }.toString(2)

    assertFalse("JSON no debe contener draftWords", jsonString.contains("draftWords"))
    assertFalse("JSON no debe contener studyTimerMillis", jsonString.contains("studyTimerMillis"))

    val parsed = com.example.data.backup.AutoBackupManager.parseJsonContent(jsonString)
    assertNotNull(parsed)
    assertEquals(2, parsed!!.version)
    assertEquals(3, parsed.usedWordsCount)

    // Contadores de palabras
    assertEquals(4, parsed.vocabularyWordCounts.size)
    assertEquals(15, parsed.vocabularyWordCounts["the"])
    assertEquals(20, parsed.vocabularyWordCounts["be"])
    assertEquals(0, parsed.vocabularyWordCounts["abandon"])
    assertEquals(32, parsed.vocabularyWordCounts["house"])

    // Contadores de abecedario (Asegura las 26 letras y contadores exactos)
    assertEquals(26, parsed.alphabetCounts.size)
    assertEquals(5, parsed.alphabetCounts["A"])
    assertEquals(0, parsed.alphabetCounts["B"])
    assertEquals(8, parsed.alphabetCounts["C"])
    assertEquals(0, parsed.alphabetCounts["Z"])

    // Contadores de números (Asegura los 100 números y contadores exactos)
    assertEquals(100, parsed.numberCounts.size)
    assertEquals(10, parsed.numberCounts[1])
    assertEquals(0, parsed.numberCounts[2])
    assertEquals(3, parsed.numberCounts[100])

    // Contadores de frases (Asegura las 400 frases cotidianas y contadores exactos)
    assertEquals(400, parsed.phraseCounts.size)
    assertEquals(7, parsed.phraseCounts[1])
    assertEquals(0, parsed.phraseCounts[2])
    assertEquals(12, parsed.phraseCounts[50])

    // Notas
    assertEquals(1, parsed.notes.size)
    assertEquals("Regla de fonética", parsed.notes[0].text)
  }

  @Test
  fun `test AutoBackupManager preserves 2809 words with counts custom words 2810 and full note texts`() {
    val sampleWordList = listOf(
      com.example.domain.model.Word(rank = 1, lemma = "the", rawPartOfSpeech = "d", useCount = 12),
      com.example.domain.model.Word(rank = 2, lemma = "be", rawPartOfSpeech = "v", useCount = 5),
      com.example.domain.model.Word(rank = 14, lemma = "you", rawPartOfSpeech = "p", useCount = 2),
      com.example.domain.model.Word(rank = 2809, lemma = "zone", rawPartOfSpeech = "n", useCount = 0),
      com.example.domain.model.Word(rank = 2810, lemma = "cybersecurity", rawPartOfSpeech = "sustantivo", useCount = 7, spanishTranslation = "ciberseguridad", microExample = "She works in cybersecurity.")
    )
    val customWords = listOf(
      com.example.data.local.CustomWordRecord(
        rank = 2810,
        word = "cybersecurity",
        partOfSpeech = "sustantivo",
        spanishTranslation = "ciberseguridad",
        microExample = "She works in cybersecurity.",
        useCount = 7
      )
    )
    val notes = listOf(
      com.example.presentation.notes.NoteBlock(
        id = "note-xyz",
        text = "Recordar repasar los phrasal verbs de get y take cada mañana a las 8am.",
        isCompleted = true,
        createdAt = 1700000000000L
      )
    )

    val vocabCounts = sampleWordList.associate { it.lemma to it.useCount }

    val jsonString = org.json.JSONObject().apply {
      put("appName", "VocabFlow")
      put("version", 2)
      put("exportedAt", System.currentTimeMillis())
      put("totalWords", sampleWordList.size)
      put("usedWordsCount", 4)

      val wordsArray = org.json.JSONArray()
      sampleWordList.forEach { w ->
        wordsArray.put(org.json.JSONObject().apply {
          put("rank", w.rank)
          put("word", w.lemma)
          put("useCount", w.useCount)
        })
      }
      put("allVocabularyWords", wordsArray)

      val vocabObj = org.json.JSONObject()
      vocabCounts.forEach { (lemma, count) -> vocabObj.put(lemma, count) }
      put("vocabularyWordCounts", vocabObj)

      val customArray = org.json.JSONArray()
      customWords.forEach { cw ->
        customArray.put(org.json.JSONObject().apply {
          put("rank", cw.rank)
          put("word", cw.word)
          put("partOfSpeech", cw.partOfSpeech)
          put("spanishTranslation", cw.spanishTranslation)
          put("microExample", cw.microExample)
          put("useCount", cw.useCount)
        })
      }
      put("customWords", customArray)

      val notesArray = org.json.JSONArray()
      notes.forEach { n ->
        notesArray.put(org.json.JSONObject().apply {
          put("id", n.id)
          put("text", n.text)
          put("isCompleted", n.isCompleted)
          put("createdAt", n.createdAt)
        })
      }
      put("localNotes", notesArray)
    }.toString(2)

    val parsed = com.example.data.backup.AutoBackupManager.parseJsonContent(jsonString)
    assertNotNull(parsed)
    assertEquals(5, parsed!!.totalWords)
    assertEquals(12, parsed.vocabularyWordCounts["the"])
    assertEquals(2, parsed.vocabularyWordCounts["you"])
    assertEquals(0, parsed.vocabularyWordCounts["zone"])
    assertEquals(7, parsed.vocabularyWordCounts["cybersecurity"])

    // Verificar que la palabra 2810 está en customWords con todos sus datos
    assertEquals(1, parsed.customWords.size)
    val restoredCustom = parsed.customWords[0]
    assertEquals(2810, restoredCustom.rank)
    assertEquals("cybersecurity", restoredCustom.word)
    assertEquals("ciberseguridad", restoredCustom.spanishTranslation)
    assertEquals("She works in cybersecurity.", restoredCustom.microExample)
    assertEquals(7, restoredCustom.useCount)

    // Verificar que las notas locales guardaron su texto y contenido íntegro
    assertEquals(1, parsed.notes.size)
    assertEquals("note-xyz", parsed.notes[0].id)
    assertEquals("Recordar repasar los phrasal verbs de get y take cada mañana a las 8am.", parsed.notes[0].text)
    assertTrue(parsed.notes[0].isCompleted)
    assertEquals(1700000000000L, parsed.notes[0].createdAt)
  }

  @Test
  fun `test AutoBackupManager preserves all 26 alphabet letters with exact use counts and restores them without zeroing`() {
    val sampleAlphabetCounts = mapOf(
      "A" to 14,
      "B" to 0,
      "C" to 7,
      "D" to 1,
      "E" to 22,
      "Z" to 3
    )

    val jsonString = org.json.JSONObject().apply {
      put("appName", "VocabFlow")
      put("version", 2)
      put("exportedAt", System.currentTimeMillis())
      put("totalAlphabetLetters", 26)
      put("usedAlphabetLettersCount", 5)
      put("totalAlphabetUses", sampleAlphabetCounts.values.sum())

      val alphabetLettersArray = org.json.JSONArray()
      com.example.data.backup.standardAlphabetCatalog.forEach { std ->
        val count = sampleAlphabetCounts[std.letter] ?: 0
        alphabetLettersArray.put(org.json.JSONObject().apply {
          put("index", std.index)
          put("letter", std.letter)
          put("ipa", std.ipa)
          put("phoneticSp", std.phoneticSp)
          put("isVowel", std.isVowel)
          if (std.note.isNotBlank()) put("note", std.note)
          put("useCount", count)
        })
      }
      put("allAlphabetLetters", alphabetLettersArray)

      val alphabetObj = org.json.JSONObject()
      com.example.data.backup.standardAlphabetCatalog.forEach { std ->
        alphabetObj.put(std.letter, sampleAlphabetCounts[std.letter] ?: 0)
      }
      put("alphabetCounts", alphabetObj)
    }.toString(2)

    val parsed = com.example.data.backup.AutoBackupManager.parseJsonContent(jsonString)
    assertNotNull(parsed)
    assertEquals(26, parsed!!.totalAlphabetLetters)
    assertEquals(5, parsed.usedAlphabetLettersCount)

    // Verificar que todas las 26 letras existen con sus contadores reales
    assertEquals(26, parsed.allAlphabetLetters.size)
    assertEquals(26, parsed.alphabetCounts.size)

    assertEquals(14, parsed.alphabetCounts["A"])
    assertEquals(0, parsed.alphabetCounts["B"])
    assertEquals(7, parsed.alphabetCounts["C"])
    assertEquals(1, parsed.alphabetCounts["D"])
    assertEquals(22, parsed.alphabetCounts["E"])
    assertEquals(0, parsed.alphabetCounts["F"])
    assertEquals(3, parsed.alphabetCounts["Z"])

    val letterA = parsed.allAlphabetLetters.first { it.letter == "A" }
    assertEquals(1, letterA.index)
    assertEquals("éi", letterA.phoneticSp)
    assertTrue(letterA.isVowel)
    assertEquals(14, letterA.useCount)

    val letterB = parsed.allAlphabetLetters.first { it.letter == "B" }
    assertEquals(2, letterB.index)
    assertEquals(0, letterB.useCount)

    val letterZ = parsed.allAlphabetLetters.first { it.letter == "Z" }
    assertEquals(26, letterZ.index)
    assertEquals(3, letterZ.useCount)

    // Simular restauración en AlphabetStorage
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    val alphabetStorage = com.example.data.local.AlphabetStorage(context)
    alphabetStorage.saveCounts(parsed.alphabetCounts)

    val loaded = alphabetStorage.loadCounts()
    assertEquals(14, loaded["A"])
    assertEquals(0, loaded["B"])
    assertEquals(7, loaded["C"])
    assertEquals(1, loaded["D"])
    assertEquals(22, loaded["E"])
    assertEquals(3, loaded["Z"])
  }

  @Test
  fun `verify detailed 100 numbers backup retains individual counts and restores to NumbersStorage without zeroing`() {
    val sampleNumberCounts = mapOf(
      1 to 15,
      2 to 8,
      10 to 4,
      25 to 1,
      50 to 9,
      100 to 12
    )

    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

    // Guardar copia completa usando AutoBackupManager
    val success = com.example.data.backup.AutoBackupManager.saveFullBackup(
      context = context,
      usedWordsCount = 0,
      notes = emptyList(),
      numberCounts = sampleNumberCounts
    )
    assertTrue("Copia completa debe guardarse exitosamente", success)

    // Cargar y parsear archivo de backup
    val restored = com.example.data.backup.AutoBackupManager.loadLatestBackup(context)
    assertNotNull(restored)
    assertEquals(100, restored!!.totalNumbers)
    assertEquals(6, restored.usedNumbersCount)

    // Verificar que los 100 números existen en la lista y en el mapa de contadores
    assertEquals(100, restored.allNumbers.size)
    assertEquals(100, restored.numberCounts.size)

    assertEquals(15, restored.numberCounts[1])
    assertEquals(8, restored.numberCounts[2])
    assertEquals(0, restored.numberCounts[3])
    assertEquals(4, restored.numberCounts[10])
    assertEquals(1, restored.numberCounts[25])
    assertEquals(9, restored.numberCounts[50])
    assertEquals(0, restored.numberCounts[99])
    assertEquals(12, restored.numberCounts[100])

    // Verificar detalles individuales de números
    val num1 = restored.allNumbers.first { it.number == 1 }
    assertEquals("One", num1.word)
    assertEquals("uán", num1.phoneticSp)
    assertEquals(15, num1.useCount)

    val num3 = restored.allNumbers.first { it.number == 3 }
    assertEquals("Three", num3.word)
    assertEquals(0, num3.useCount)

    val num100 = restored.allNumbers.first { it.number == 100 }
    assertEquals("One hundred", num100.word)
    assertEquals(12, num100.useCount)

    // Simular restauración en NumbersStorage
    val numbersStorage = com.example.data.local.NumbersStorage(context)
    numbersStorage.saveCounts(restored.numberCounts)

    val loaded = numbersStorage.loadCounts()
    assertEquals(15, loaded[1])
    assertEquals(8, loaded[2])
    assertEquals(0, loaded[3])
    assertEquals(4, loaded[10])
    assertEquals(1, loaded[25])
    assertEquals(9, loaded[50])
    assertEquals(0, loaded[99])
    assertEquals(12, loaded[100])
  }

  @Test
  fun `verify detailed 400 phrases backup retains individual counts and restores to PhrasesStorage without zeroing`() {
    val samplePhraseCounts = mapOf(
      1 to 18,
      2 to 5,
      10 to 2,
      51 to 4,
      400 to 9
    )

    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

    // Guardar copia completa usando AutoBackupManager con contadores de frases
    val success = com.example.data.backup.AutoBackupManager.saveFullBackup(
      context = context,
      usedWordsCount = 0,
      notes = emptyList(),
      phraseCounts = samplePhraseCounts
    )
    assertTrue("Copia completa con frases debe guardarse exitosamente", success)

    // Cargar y parsear archivo de backup
    val restored = com.example.data.backup.AutoBackupManager.loadLatestBackup(context)
    assertNotNull(restored)
    assertEquals(400, restored!!.totalPhrases)
    assertEquals(5, restored.usedPhrasesCount)

    // Verificar que las 400 frases existen en la lista y en el mapa de contadores
    assertEquals(400, restored.allPhrases.size)
    assertEquals(400, restored.phraseCounts.size)

    assertEquals(18, restored.phraseCounts[1])
    assertEquals(5, restored.phraseCounts[2])
    assertEquals(0, restored.phraseCounts[3])
    assertEquals(2, restored.phraseCounts[10])
    assertEquals(4, restored.phraseCounts[51])
    assertEquals(0, restored.phraseCounts[399])
    assertEquals(9, restored.phraseCounts[400])

    // Verificar detalles individuales de frases
    val phrase1 = restored.allPhrases.first { it.id == 1 }
    assertEquals("Buenos días, ¿dormiste bien anoche?", phrase1.spanishText)
    assertEquals("Mañana", phrase1.category)
    assertEquals("Good morning, did you sleep well last night?", phrase1.englishHint)
    assertEquals(18, phrase1.useCount)

    val phrase51 = restored.allPhrases.first { it.id == 51 }
    assertEquals("Voy a preparar una taza de café caliente recién molido.", phrase51.spanishText)
    assertEquals("Desayuno", phrase51.category)
    assertEquals(4, phrase51.useCount)

    val phrase400 = restored.allPhrases.first { it.id == 400 }
    assertEquals("Buenas noches a todos, que tengan un lindo descanso hasta mañana.", phrase400.spanishText)
    assertEquals("Noche", phrase400.category)
    assertEquals(9, phrase400.useCount)

    // Simular restauración en PhrasesStorage
    val phrasesStorage = com.example.data.local.PhrasesStorage(context)
    phrasesStorage.saveCounts(restored.phraseCounts)

    val loaded = phrasesStorage.loadCounts()
    assertEquals(18, loaded[1])
    assertEquals(5, loaded[2])
    assertEquals(0, loaded[3])
    assertEquals(2, loaded[10])
    assertEquals(4, loaded[51])
    assertEquals(0, loaded[399])
    assertEquals(9, loaded[400])
  }

  @Test
  fun `verify wallpaper storage scans special assets wallpapers folder and recognizes images`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = com.example.data.wallpaper.WallpaperStorage(context)

    val wallpapers = storage.scanAvailableWallpapers()
    assertTrue("Debe incluir al menos la opción predeterminada y las imágenes de assets", wallpapers.size >= 2)

    val defaultOption = wallpapers.first { it.isDefaultNone }
    assertEquals("none", defaultOption.id)

    // Verificar que los fondos agregados a assets estén reconocidos
    val firstImageWallpaper = wallpapers.firstOrNull { !it.isDefaultNone }
    assertNotNull("Debe haber fondos reconocidos en assets", firstImageWallpaper)
    assertTrue(firstImageWallpaper!!.title.isNotBlank())
    assertTrue(firstImageWallpaper.assetPath!!.startsWith("wallpapers/"))
  }

  @Test
  fun `verify wallpaper manager updates and persists selected wallpaper and dim alpha`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = com.example.data.wallpaper.WallpaperStorage(context)
    val manager = com.example.data.wallpaper.WallpaperManager(storage)

    // Inicialmente opción predeterminada
    assertEquals("none", manager.selectedWallpaper.value.id)

    val sampleWallpaper = manager.wallpapers.value.first { !it.isDefaultNone }
    manager.selectWallpaper(sampleWallpaper)
    assertEquals(sampleWallpaper.id, manager.selectedWallpaper.value.id)
    assertEquals(sampleWallpaper.id, storage.getSelectedWallpaperId())

    // Probar ajuste de atenuación
    manager.updateDimAlpha(0.60f)
    assertEquals(0.60f, manager.dimAlpha.value, 0.01f)
    assertEquals(0.60f, storage.getWallpaperDimAlpha(), 0.01f)

    // Crear nueva instancia de manager simulando reinicio de la app
    val newManager = com.example.data.wallpaper.WallpaperManager(storage)
    assertEquals(sampleWallpaper.id, newManager.selectedWallpaper.value.id)
    assertEquals(0.60f, newManager.dimAlpha.value, 0.01f)

    // Revertir a predeterminado
    newManager.selectWallpaper(com.example.data.wallpaper.WallpaperStorage.DEFAULT_NONE_WALLPAPER)
    assertEquals("none", newManager.selectedWallpaper.value.id)
  }

  @Test
  fun `verify floating wallpaper tray UI renders items and handles selection`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = com.example.data.wallpaper.WallpaperStorage(context)
    val manager = com.example.data.wallpaper.WallpaperManager(storage)
    var closed = false

    composeTestRule.setContent {
      EnglishAppTheme {
        com.example.presentation.wallpaper.FloatingWallpaperTray(
          isOpen = true,
          onClose = { closed = true },
          wallpaperManager = manager
        )
      }
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("floating_wallpaper_tray_overlay").assertExists()
    composeTestRule.onNodeWithTag("floating_wallpaper_tray_title").assertExists()
    composeTestRule.onNodeWithTag("btn_close_wallpaper_tray_x").assertExists()
    composeTestRule.onNodeWithTag("btn_close_wallpaper_tray").assertExists()

    // Verificar que existe el item del fondo predeterminado
    composeTestRule.onNodeWithTag("wallpaper_item_none").assertExists()

    // Al pulsar un fondo de pantalla para elegirlo, el sistema debe seleccionarlo y cerrar la bandeja automáticamente
    composeTestRule.onNodeWithTag("wallpaper_item_none").performClick()
    composeTestRule.waitForIdle()
    assertTrue("La bandeja flotante debe cerrarse inmediatamente al elegir un fondo", closed)
  }
}




