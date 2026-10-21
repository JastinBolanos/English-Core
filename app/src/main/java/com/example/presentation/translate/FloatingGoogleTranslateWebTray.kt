package com.example.presentation.translate

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * URL directa a Google Traductor Web.
 */
const val GOOGLE_TRANSLATE_WEB_URL = "https://translate.google.com.pe/?sl=en&tl=es&op=translate"

/**
 * Bandeja Flotante In-App de Google Traductor Web (60% de la pantalla).
 * En la parte superior contiene ÚNICAMENTE la 'X' de cerrar, el retroceder y el avanzar.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FloatingGoogleTranslateWebTray(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var isLoadingPage by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    // Altura del 60% de la pantalla centrada al medio según requerimiento
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
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_ALWAYS
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            setOnTouchListener { view, event ->
                view.parent?.requestDisallowInterceptTouchEvent(true)
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.requestFocus()
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
                    view?.loadUrl(url)
                    return true
                }
            }

            loadUrl(GOOGLE_TRANSLATE_WEB_URL)
        }
    }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            val currentUrl = persistentWebView.url?.trimEnd('/')
            if (currentUrl.isNullOrBlank() || currentUrl == "about:blank") {
                persistentWebView.loadUrl(GOOGLE_TRANSLATE_WEB_URL)
            }
        }
    }

    if (isOpen) {
        BackHandler {
            if (persistentWebView.canGoBack()) {
                persistentWebView.goBack()
            } else {
                onClose()
            }
        }
    }

    val animAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "translate_tray_alpha"
    )

    val animScale by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0.93f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "translate_tray_scale"
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

        // Tarjeta flotante con altura del 60% de la pantalla centrada al medio
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalTrayHeight)
                .padding(horizontal = 8.dp)
                .align(Alignment.Center)
                .offset(y = (-36).dp)
                .graphicsLayer {
                    alpha = animAlpha
                    scaleX = animScale
                    scaleY = animScale
                }
                .testTag("floating_google_translate_web_tray"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0D1524),
            border = BorderStroke(1.2.dp, Color(0xFF1A73E8).copy(alpha = 0.85f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                // Barra superior de controles: ÚNICAMENTE retroceder, avanzar y la 'X' de cierre
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Controles de navegación: retroceder y avanzar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Botón Retroceder
                        IconButton(
                            onClick = {
                                if (persistentWebView.canGoBack()) {
                                    persistentWebView.goBack()
                                }
                            },
                            enabled = canGoBack,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_translate_web_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retroceder",
                                tint = if (canGoBack) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Botón Avanzar
                        IconButton(
                            onClick = {
                                if (persistentWebView.canGoForward()) {
                                    persistentWebView.goForward()
                                }
                            },
                            enabled = canGoForward,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_translate_web_forward")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Avanzar",
                                tint = if (canGoForward) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Botón 'X' de cierre en esquina derecha
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3B1212).copy(alpha = 0.9f),
                        border = BorderStroke(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.9f)),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onClose() }
                            .testTag("btn_close_floating_translate_web")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar traductor web",
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
                        color = Color(0xFF1A73E8),
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Contenedor del WebView in-app ocupando el resto de la bandeja
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
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
                            .testTag("webview_floating_translate_web")
                    )
                }
            }
        }
    }
}
