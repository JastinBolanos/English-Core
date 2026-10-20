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

    val configuration = LocalConfiguration.current
    val totalTrayHeight = (configuration.screenHeightDp * 0.75f).dp

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

            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        view.parent?.requestDisallowInterceptTouchEvent(true)
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
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                allowFileAccess = false
                allowContentAccess = false
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                userAgentString =
                    "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro Build/UD1A.230803.041; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/124.0.6367.82 Mobile Safari/537.36"
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
        label = "gemini_tray_alpha"
    )

    val animOffsetY by animateDpAsState(
        targetValue = if (isOpen) 0.dp else (-totalTrayHeight - 40.dp),
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "gemini_tray_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
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

        // Tarjeta flotante con altura de exactamente el 75% de la pantalla
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalTrayHeight)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .offset(y = animOffsetY)
                .graphicsLayer { alpha = animAlpha }
                .testTag("floating_gemini_web_tray"),
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
                // Barra superior de controles HUD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Controles izquierdos
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Ícono de Géminis: Círculo azul con destellos -> Recarga consulta inicial
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1A73E8),
                            modifier = Modifier
                                .size(26.dp)
                                .clickable {
                                    persistentWebView.loadUrl(GEMINI_WEB_QUERY_URL)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Recargar Géminis Web",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
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

                        // Botón Adelante
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

                        // Botón Inicio -> Restablecer a búsqueda inicial de Géminis
                        IconButton(
                            onClick = {
                                persistentWebView.loadUrl(GEMINI_WEB_QUERY_URL)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio Géminis",
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
                                contentDescription = "Recargar Página",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(17.dp)
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
                            .testTag("btn_close_floating_gemini_web")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar bandeja de Géminis",
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

                // Contenedor del WebView in-app ocupando el resto del 80% de la pantalla
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { persistentWebView },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("webview_floating_gemini_web")
                    )
                }
            }
        }
    }
}
