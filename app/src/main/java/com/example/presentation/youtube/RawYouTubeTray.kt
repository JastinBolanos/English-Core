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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

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
 * - Barra superior idéntica a la maqueta con botón de encogimiento/cierre en la esquina superior derecha.
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
    val configuration = LocalConfiguration.current
    val expandedHeight = (configuration.screenHeightDp * 0.65f).dp

    val context = LocalContext.current
    val persistentWebView = remember(context) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isNestedScrollingEnabled = true
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            // Permitir scroll libre dentro del WebView al interactuar con YouTube
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                loadWithOverviewMode = true
                useWideViewPort = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    loadProgress = newProgress / 100f
                    isLoadingPage = newProgress < 100
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoadingPage = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoadingPage = false
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString().orEmpty()
                    if (url.startsWith("intent://") || url.startsWith("vnd.youtube:")) {
                        val vid = when {
                            url.contains("videoId=") -> url.substringAfter("videoId=").substringBefore("&")
                            url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
                            else -> ""
                        }
                        if (vid.isNotBlank()) {
                            view?.loadUrl("https://m.youtube.com/watch?v=$vid")
                            return true
                        }
                        return true
                    }
                    return false
                }
            }

            loadUrl("https://m.youtube.com")
        }
    }

    // Interceptar el botón Back de Android para navegar atrás en YouTube antes de cerrar la bandeja
    BackHandler(enabled = isOpen) {
        if (persistentWebView.canGoBack()) {
            persistentWebView.goBack()
        } else {
            onClose()
        }
    }

    // Animación suave de apertura y cierre (deslizamiento y opacidad)
    val animOffsetY by animateDpAsState(
        targetValue = if (isOpen) 0.dp else (-1000).dp,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "floating_youtube_anim_offset"
    )
    val animAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "floating_youtube_anim_alpha"
    )

    // Contenedor que flota sobre el contenido
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrim semitransparente táctico que permite cerrar al tocar fuera
        if (animAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = animAlpha * 0.55f }
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = isOpen
                    ) {
                        onClose()
                    }
            )
        }

        // Tarjeta flotante con altura de 65% de la pantalla
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .offset(y = animOffsetY)
                .graphicsLayer { alpha = animAlpha }
                .testTag("floating_youtube_tray"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.65f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                // Barra superior de navegación web (estilo HUD con controles y botón encoger en esquina derecha)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Controles izquierdos: Ícono YouTube + Atrás + Adelante + Inicio + Recargar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Ícono de YouTube: círculo rojo con play blanco
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF0000),
                            modifier = Modifier
                                .size(26.dp)
                                .clickable {
                                    persistentWebView.loadUrl("https://m.youtube.com")
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "YouTube Inicio",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        // Botón Atrás
                        IconButton(
                            onClick = {
                                if (persistentWebView.canGoBack()) {
                                    persistentWebView.goBack()
                                }
                            },
                            enabled = canGoBack,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = if (canGoBack) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Botón Adelante (Actualización hacia adelante)
                        IconButton(
                            onClick = {
                                if (persistentWebView.canGoForward()) {
                                    persistentWebView.goForward()
                                }
                            },
                            enabled = canGoForward,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Adelante",
                                tint = if (canGoForward) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Botón Inicio
                        IconButton(
                            onClick = {
                                persistentWebView.loadUrl("https://m.youtube.com")
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Botón Recargar
                        IconButton(
                            onClick = { persistentWebView.reload() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recargar",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Botón de encoger/cerrar en esquina derecha con recuadro y borde rojizo (ícono ^)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3B1212).copy(alpha = 0.85f),
                        border = BorderStroke(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.85f)),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onClose() }
                            .testTag("btn_close_floating_youtube")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Ocultar bandeja de YouTube",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Línea de progreso de carga de página
                if (isLoadingPage) {
                    LinearProgressIndicator(
                        progress = { loadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF38BDF8),
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Contenedor del WebView con altura del 65% de la pantalla
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(expandedHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { persistentWebView },
                        update = { _ -> },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("raw_youtube_webview")
                    )
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
