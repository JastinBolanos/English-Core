package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EnglishAppTheme

enum class AppScreen {
  HOME,
  VOCABULARIO,
  ALARMAS
}

data class MenuTextItemData(
  val screen: AppScreen,
  val titleRes: Int,
  val testTag: String
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EnglishAppTheme {
        EnglishApp()
      }
    }
  }
}

@Composable
fun EnglishApp() {
  var currentScreen by rememberSaveable { mutableStateOf(AppScreen.VOCABULARIO) }
  val context = androidx.compose.ui.platform.LocalContext.current

  val repository = androidx.compose.runtime.remember(context) {
    com.example.domain.util.WordGrammarCatalog.loadGrammarTypesFromContext(context.applicationContext)
    com.example.domain.util.WordDictionary.loadTranslationsFromContext(context.applicationContext)
    val db = com.example.data.local.AppDatabase.getDatabase(context.applicationContext)
    val dao = db.wordDao()
    val initializer = com.example.data.local.DatabaseInitializer(context.applicationContext, dao)
    com.example.data.repository.VocabularyRepositoryImpl(dao, initializer)
  }

  val ttsManager = androidx.compose.runtime.remember(context) {
    com.example.di.TtsModule.provideTtsManager(context.applicationContext)
  }

  val sttManager = androidx.compose.runtime.remember(context) {
    com.example.di.SpeechToTextModule.provideSpeechToTextManager(context.applicationContext)
  }

  val vocabularyViewModel: com.example.presentation.vocabulary.VocabularyViewModel =
    androidx.lifecycle.viewmodel.compose.viewModel(
      factory = androidx.compose.runtime.remember(repository, ttsManager, sttManager) {
        com.example.presentation.vocabulary.VocabularyViewModel.Factory(repository, ttsManager, sttManager)
      }
    )

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
      if (targetState == AppScreen.ALARMAS) {
        slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
      } else {
        slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
      }
    },
    label = "ScreenTransition"
  ) { screen ->
    when (screen) {
      AppScreen.HOME,
      AppScreen.VOCABULARIO -> {
        com.example.presentation.vocabulary.VocabularyScreen(
          viewModel = vocabularyViewModel,
          onBack = { /* Primary screen */ }
        )
      }
      AppScreen.ALARMAS -> {
        com.example.presentation.alarm.AlarmsScreen(
          onBack = { currentScreen = AppScreen.VOCABULARIO }
        )
      }
    }
  }
}

@Composable
fun HomeScreen(
  onNavigate: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val menuItems = listOf(
    MenuTextItemData(
      screen = AppScreen.VOCABULARIO,
      titleRes = R.string.btn_vocabulario,
      testTag = "btn_vocabulario"
    ),
    MenuTextItemData(
      screen = AppScreen.ALARMAS,
      titleRes = R.string.btn_alarmas,
      testTag = "btn_alarmas"
    )
  )

  Scaffold(
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        menuItems.forEach { item ->
          AppMenuTextButton(
            item = item,
            onClick = { onNavigate(item.screen) }
          )
        }
      }
    }
  }
}

@Composable
fun AppMenuTextButton(
  item: MenuTextItemData,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag(item.testTag)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 22.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = stringResource(id = item.titleRes),
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Abrir ${stringResource(id = item.titleRes)}",
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlankScreen(
  testTag: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { /* Intentionally empty */ },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.back)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag(testTag)
    )
  }
}
