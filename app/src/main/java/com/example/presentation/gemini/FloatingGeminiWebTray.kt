package com.example.presentation.gemini

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

/**
 * URL de consulta en directo para Géminis Web (Google AI Search / Gemini Mode udm=50).
 */
const val GEMINI_WEB_QUERY_URL =
    "https://www.google.com/search?client=ms-android-zte&hs=8eqq&sca_esv=f805f121b9f8e54d&sxsrf=APpeQnsqh0udAtTBjMevx4L_BM-liBl99w%3A1792494801229&udm=50&fbs=ABfTbFVyMZGZf1hfvX9uKjN_-G8c4u0nXx4bEIpwm1lnNH832VTJOOCxW_fyN-Q_ezyf8gKKYi6DYGSj2Ng7gxGZN3oaoqNkvqcw0gjT8z58Q3rjcd-DOsrGjYtbRDd9MfcP_OXc30v1Zb9LanikIgCzV0VsqA2nqB9np11qGHG-trzG67oLJE90WZyf8oIateP_ijZNgZeza-znfZx1_bnEMEQw6UX1Dg&vsint=&ntc=1&cs=1&sa=X&ved=0CAEQoo4PahcKEwjQ7NXAu8iXAxUAAAAAHQAAAAAQGg&biw=423&bih=834&dpr=1.7&zs=1"

/**
 * Bandeja Flotante In-App de Géminis Web (75% de la pantalla).
 * Permite realizar cualquier consulta o aclaración a la IA en vivo dentro de la misma aplicación,
 * flotando con controles web (Atrás, Adelante, Home, Recargar, Cerrar X) sin expulsar al usuario
 * a navegadores externos.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FloatingGeminiWebTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var isLoadingPage by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val totalTrayHeight = (configuration.screenHeightDp * 0.60f).dp

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
                val cookieManager = CookieManager.getInstance()
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
                    if (url.startsWith("intent:")) {
                        return true
                    }
                    // Mantener la navegación completa dentro del WebView in-app sin expulsar al usuario
                    view?.loadUrl(url)
                    return true
                }
            }

            loadUrl(GEMINI_WEB_QUERY_URL)
        }
    }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            val currentUrl = persistentWebView.url?.trimEnd('/')
            if (currentUrl.isNullOrBlank() || currentUrl == "about:blank") {
                persistentWebView.loadUrl(GEMINI_WEB_QUERY_URL)
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

    val animAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "gemini_tray_alpha"
    )

    val animScale by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0.93f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "gemini_tray_scale"
    )

    val isFullyClosed = !isOpen && animAlpha <= 0.01f

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isFullyClosed) {
                    Modifier.offset(x = 10000.dp)
                } else {
                    Modifier
                }
            )
            .graphicsLayer {
                alpha = animAlpha
            }
    ) {
        // Fondo semi-transparente para dar foco a la tarjeta flotante
        if (animAlpha > 0.05f) {
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

        // Tarjeta flotante con altura de exactamente el 60% de la pantalla centrada en el medio (o pantalla completa si isFullscreen)
        Surface(
            modifier = (
                if (isFullscreen) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(totalTrayHeight)
                        .padding(horizontal = 8.dp)
                        .align(Alignment.Center)
                        .offset(y = (-36).dp)
                }
            )
            .graphicsLayer {
                alpha = animAlpha
                scaleX = animScale
                scaleY = animScale
            }
            .testTag("floating_gemini_web_tray"),
            shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(16.dp),
            color = Color(0xFF0D1524),
            border = if (isFullscreen) null else BorderStroke(1.2.dp, Color(0xFF1A73E8).copy(alpha = 0.85f)),
            shadowElevation = if (isFullscreen) 0.dp else 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isFullscreen) PaddingValues(0.dp) else PaddingValues(horizontal = 6.dp, vertical = 6.dp))
            ) {
                // Contenedor del WebView con controles semitransparentes flotantes superpuestos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(10.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = {
                            (persistentWebView.parent as? ViewGroup)?.removeView(persistentWebView)
                            persistentWebView
                        },
                        update = { webView ->
                            webView.visibility = if (isFullyClosed) View.INVISIBLE else View.VISIBLE
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("webview_floating_gemini_web")
                    )

                    // Línea de progreso de carga de página superpuesta en el borde superior
                    if (isLoadingPage) {
                        LinearProgressIndicator(
                            progress = { loadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter),
                            color = Color(0xFF1A73E8),
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
                            .testTag("btn_close_floating_gemini_web")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar bandeja de Géminis",
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
                            .testTag("btn_fullscreen_gemini_web")
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
                            .testTag("btn_gemini_back")
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
                            .testTag("btn_gemini_forward")
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
            }
        }
    }
}
