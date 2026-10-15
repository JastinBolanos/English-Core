package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.PartOfSpeech
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

    // Click clear button to remove selection
    composeTestRule.onNodeWithTag("btn_clear_compass_selection").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify mic button and mirror tray are invisible when draft is empty`() {
    composeTestRule.setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("draft_scratchpad").assertExists()
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
}


