package com.example.presentation.youtube

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Atajos de búsqueda rápida en YouTube.
 */
data class YouTubeSearchShortcut(
    val label: String,
    val query: String
)

enum class SearchCategory(val title: String) {
    ALL("⭐ Todo en Uno"),
    CONNECTED_SPEECH("🎬 Habla Pegada"),
    SLANG_PHRASALS("🔥 Jerga Callejera"),
    SPEED_REFLEX("⚡ Velocidad y Reflejo")
}

/**
 * Búsqueda directa por defecto para evitar distracciones en el feed de inicio de YouTube.
 * Lleva directamente a una selección de conversación en inglés simple y cotidiana.
 */
const val DEFAULT_CONVERSATION_SEARCH_QUERY = "simple everyday english conversation"
val DEFAULT_CONVERSATION_SEARCH_URL: String = "https://m.youtube.com/results?search_query=" +
    URLEncoder.encode(DEFAULT_CONVERSATION_SEARCH_QUERY, StandardCharsets.UTF_8.toString())

val ALL_SHORTCUTS = listOf(
    YouTubeSearchShortcut("💬 Conversación Simple", DEFAULT_CONVERSATION_SEARCH_QUERY),
    YouTubeSearchShortcut("🎬 Habla Pegada", "american english connected speech practice"),
    YouTubeSearchShortcut("🍿 Escenas con Subs", "learn english with movie scenes english subtitles"),
    YouTubeSearchShortcut("🗣️ Reducciones Rápidas", "fast english reductions gonna wanna gotta"),
    YouTubeSearchShortcut("🎙️ Podcast sin Guion", "casual english podcast unscripted"),
    YouTubeSearchShortcut("🔥 Slang Callejero", "daily american slang and phrasal verbs"),
    YouTubeSearchShortcut("🔗 Phrasal Verbs", "everyday english connectors in conversation"),
    YouTubeSearchShortcut("🎮 Streams IRL", "IRL stream highlights english chat"),
    YouTubeSearchShortcut("🎤 Entrevistas Calle", "street interviews english subtitles"),
    YouTubeSearchShortcut("⚡ Shadowing en Vivo", "real life english conversation shadowing"),
    YouTubeSearchShortcut("🧠 Narrar mi Día", "narrate your day in english vlog"),
    YouTubeSearchShortcut("🔀 Conectores Nativos", "everyday english connectors in conversation")
)

val CONNECTED_SPEECH_SHORTCUTS = listOf(
    YouTubeSearchShortcut("🎬 Connected Speech", "connected speech american english"),
    YouTubeSearchShortcut("🗣️ Reducciones didja whaddaya", "fast english reductions didja whaddaya"),
    YouTubeSearchShortcut("🍿 Movie Clips con Subs", "movie clips with english subtitles"),
    YouTubeSearchShortcut("📺 TV Series con Subs", "tv series scenes english subtitles"),
    YouTubeSearchShortcut("⚡ How Natives Speak Fast", "how natives speak fast english"),
    YouTubeSearchShortcut("🔗 Linking Sounds Practice", "english linking sounds practice"),
    YouTubeSearchShortcut("🎧 Real Listening Subs", "real english listening with subtitles")
)

val SLANG_PHRASALS_SHORTCUTS = listOf(
    YouTubeSearchShortcut("🎙️ Unscripted Podcast", "unscripted english podcast conversation"),
    YouTubeSearchShortcut("☕ Casual Podcast Clips", "casual american podcast clips"),
    YouTubeSearchShortcut("🔥 Street Slang & Phrasals", "american street slang and phrasal verbs"),
    YouTubeSearchShortcut("💬 Everyday Phrasal Verbs", "everyday phrasal verbs in context"),
    YouTubeSearchShortcut("🎮 Twitch IRL Highlights", "twitch IRL stream highlights english"),
    YouTubeSearchShortcut("🎤 Candid Street Interviews", "street interviews candid english"),
    YouTubeSearchShortcut("👥 Informal Native Chat", "informal english conversation native speakers")
)

val SPEED_REFLEX_SHORTCUTS = listOf(
    YouTubeSearchShortcut("🧠 Narrate Your Day", "narrate your day in english"),
    YouTubeSearchShortcut("📅 Daily Routine Vlog", "daily routine vlog natural english"),
    YouTubeSearchShortcut("🔀 Conectores Nativos", "english conversation connectors native"),
    YouTubeSearchShortcut("⚡ Shadowing Practice", "shadowing english conversation practice"),
    YouTubeSearchShortcut("💡 Think in English", "think in english without translating"),
    YouTubeSearchShortcut("🗣️ Spontaneous Speaking", "spontaneous english speaking practice"),
    YouTubeSearchShortcut("⏱️ Fast Q&A Questions", "real time english Q&A fast questions")
)

/**
 * Bandeja Flotante de YouTube Oficial.
 * Vive activada desde el botón flotante inferior de YouTube.
 * Se desliza como una bandeja flotante con altura del 65% de la pantalla.
 *
 * Características críticas:
 * - Anti-reseteo permanente: El WebView se mantiene vivo y conectado en memoria;
 *   al cerrar o cambiar de vista NUNCA se recarga, no se destruye y el audio
 *   continúa reproduciéndose sin cortes.
 * - Navegación completa: Retroceso (Atrás), Avance (Adelante), Inicio y Recarga.
 * - Botón de cierre 'X' en la esquina superior derecha en vez de flecha.
 * - Bandeja lateral de búsquedas directas justo abajo de la pantalla de YouTube
 *   con términos organizados y un solo toque para buscar inmediatamente.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FloatingYouTubeTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var isLoadingPage by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf(SearchCategory.ALL) }
    var activeQuery by remember { mutableStateOf<String?>(DEFAULT_CONVERSATION_SEARCH_QUERY) }
    var isFullscreen by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val totalTrayHeight = (configuration.screenHeightDp * 0.75f).dp
    // Calculamos el alto del webview para dar cabida perfecta a la barra de atajos inferior (~70dp)
    val webViewHeight = (totalTrayHeight - 110.dp).coerceAtLeast(280.dp)

    val context = LocalContext.current
    val persistentWebView = remember(context) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = true
            isFocusableInTouchMode = true
            isNestedScrollingEnabled = false
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setLayerType(View.LAYER_TYPE_NONE, null)

            setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        view.parent?.requestDisallowInterceptTouchEvent(true)
                        view.requestFocus()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                @Suppress("DEPRECATION")
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                allowFileAccess = false
                allowContentAccess = false
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                userAgentString =
                    "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36"
            }

            try {
                val cookieManager = android.webkit.CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
            } catch (_: Exception) {}

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    loadProgress = newProgress / 100f
                    isLoadingPage = newProgress < 100
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoadingPage = true
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoadingPage = false
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                    view?.evaluateJavascript(
                        "(function() { if (!window.__scroll_optimized) { window.__scroll_optimized = true; var style = document.createElement('style'); style.innerHTML = 'html, body { -webkit-overflow-scrolling: touch !important; touch-action: pan-y !important; }'; document.head.appendChild(style); } })();",
                        null
                    )
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    if (url.startsWith("intent:") || url.startsWith("vnd.youtube:")) {
                        view?.loadUrl(DEFAULT_CONVERSATION_SEARCH_URL)
                        return true
                    }
                    // Protección anti-distracción: evitar recaer en el feed de inicio algorítmico de YouTube
                    val normalized = url.trimEnd('/')
                    if (normalized == "https://m.youtube.com" || normalized == "https://www.youtube.com" ||
                        normalized == "https://m.youtube.com/#" || normalized == "https://www.youtube.com/#") {
                        view?.loadUrl(DEFAULT_CONVERSATION_SEARCH_URL)
                        return true
                    }
                    return false
                }
            }

            // Búsqueda directa inicial en inglés sin pasar por la pantalla de inicio distractora
            loadUrl(DEFAULT_CONVERSATION_SEARCH_URL)
        }
    }

    // Al abrir la bandeja, verificar que no esté en una pantalla vacía o de inicio genérico
    androidx.compose.runtime.LaunchedEffect(isOpen) {
        if (isOpen) {
            val currentUrl = persistentWebView.url?.trimEnd('/')
            if (currentUrl.isNullOrBlank() || currentUrl == "about:blank" ||
                currentUrl == "https://m.youtube.com" || currentUrl == "https://www.youtube.com") {
                activeQuery = DEFAULT_CONVERSATION_SEARCH_QUERY
                persistentWebView.loadUrl(DEFAULT_CONVERSATION_SEARCH_URL)
            }
        } else {
            isFullscreen = false
        }
    }

    if (isOpen) {
        BackHandler {
            if (isFullscreen) {
                isFullscreen = false
            } else if (persistentWebView.canGoBack()) {
                persistentWebView.goBack()
            } else {
                onClose()
            }
        }
    }

    val currentShortcuts = when (selectedCategory) {
        SearchCategory.ALL -> ALL_SHORTCUTS
        SearchCategory.CONNECTED_SPEECH -> CONNECTED_SPEECH_SHORTCUTS
        SearchCategory.SLANG_PHRASALS -> SLANG_PHRASALS_SHORTCUTS
        SearchCategory.SPEED_REFLEX -> SPEED_REFLEX_SHORTCUTS
    }

    val animAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "youtube_tray_alpha"
    )

    val isVisible = isOpen || animAlpha > 0.01f

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isVisible) {
                    Modifier.graphicsLayer { alpha = animAlpha }
                } else {
                    Modifier.offset(x = 10000.dp)
                }
            )
            .background(Color.Black.copy(alpha = 0.65f * animAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = isOpen
            ) {
                onClose()
            },
        contentAlignment = Alignment.Center
    ) {
        // Bandeja flotante de YouTube (diseño flotante tipo tarjeta centrada con bordes redondeados y sombra)
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isFullscreen) 1f else 0.94f)
                .fillMaxHeight(if (isFullscreen) 1f else 0.82f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 22.dp))
                .testTag("floating_youtube_tray"),
            shape = RoundedCornerShape(if (isFullscreen) 0.dp else 22.dp),
            border = BorderStroke(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.50f)),
            shadowElevation = 20.dp,
            color = Color(0xFF0F172A).copy(alpha = 0.98f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                // Contenedor del WebView con controles semitransparentes flotantes superpuestos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(10.dp))
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = {
                            (persistentWebView.parent as? ViewGroup)?.removeView(persistentWebView)
                            persistentWebView
                        },
                        update = { webView ->
                            webView.visibility = View.VISIBLE
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("raw_youtube_webview")
                    )

                    // Línea de progreso de carga de página superpuesta en el borde superior
                    if (isLoadingPage) {
                        LinearProgressIndicator(
                            progress = { loadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter),
                            color = Color(0xFF38BDF8),
                            trackColor = Color.Transparent
                        )
                    }

                    // Botón 'X' de cierre flotante semitransparente en la esquina superior izquierda
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(34.dp)
                            .clickable { onClose() }
                            .testTag("btn_close_floating_youtube")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar bandeja de YouTube",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Botón Expandir / Achicar (Pantalla Completa) semitransparente en la esquina superior derecha
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(34.dp)
                            .clickable { isFullscreen = !isFullscreen }
                            .testTag("btn_fullscreen_youtube")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullscreen) "Achicar pantalla" else "Pantalla completa",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Botón Flecha Atrás semitransparente en la esquina inferior izquierda
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .size(34.dp)
                            .clickable(enabled = canGoBack) {
                                if (persistentWebView.canGoBack()) {
                                    persistentWebView.goBack()
                                }
                            }
                            .testTag("btn_youtube_back")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = if (canGoBack) Color.White else Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Botón Flecha Adelante semitransparente en la esquina inferior derecha
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(34.dp)
                            .clickable(enabled = canGoForward) {
                                if (persistentWebView.canGoForward()) {
                                    persistentWebView.goForward()
                                }
                            }
                            .testTag("btn_youtube_forward")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Adelante",
                                tint = if (canGoForward) Color.White else Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (!isFullscreen) {
                    Spacer(modifier = Modifier.height(6.dp))

                    // BANDEJA LATERAL DE BÚSQUEDAS AUTOMÁTICAS (Justo abajo de la bandeja de YouTube)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF020617).copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                            .testTag("youtube_search_shortcuts_tray")
                    ) {
                        // 1. Selector de categorías (Pestañas horizontales compactas)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchCategory.entries.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFFDC2626) else Color(0xFF1E293B),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFEF4444) else Color(0xFF334155)
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedCategory = cat }
                                        .testTag("category_tab_${cat.name}")
                                ) {
                                    Text(
                                        text = cat.title,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 2. Fila lateral de botones de búsqueda (LazyRow con desplazamiento lateral fluido)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(currentShortcuts) { shortcut ->
                                val isCurrentActive = activeQuery == shortcut.query
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isCurrentActive) Color(0xFF7F1D1D) else Color(0xFF1E293B),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isCurrentActive) Color(0xFFEF4444) else Color(0xFF38BDF8).copy(alpha = 0.45f)
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            activeQuery = shortcut.query
                                            val encoded = URLEncoder.encode(shortcut.query, StandardCharsets.UTF_8.toString())
                                            persistentWebView.loadUrl("https://m.youtube.com/results?search_query=$encoded")
                                        }
                                        .testTag("shortcut_btn_${shortcut.label.take(10).replace(" ", "_")}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = if (isCurrentActive) Color(0xFFFCA5A5) else Color(0xFF38BDF8),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = shortcut.label,
                                            color = if (isCurrentActive) Color(0xFFFEF2F2) else Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sobrecarga de compatibilidad para RawYouTubeTray.
 */
@Composable
fun RawYouTubeTray(
    modifier: Modifier = Modifier
) {
    FloatingYouTubeTray(
        isOpen = true,
        onClose = {},
        modifier = modifier
    )
}
