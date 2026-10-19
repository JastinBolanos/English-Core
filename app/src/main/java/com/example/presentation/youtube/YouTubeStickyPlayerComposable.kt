package com.example.presentation.youtube

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * Sticky YouTube Player Composable con renderizado en vivo garantizado.
 * Inicializa explícitamente el IFramePlayer con controles y reproducción automática.
 * Incluye gestión de ciclo de vida y botón de cierre en la esquina superior derecha.
 */
@Composable
fun StickyYouTubePlayer(
    videoId: String?,
    isVisible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var youTubePlayerInstance by remember { mutableStateOf<YouTubePlayer?>(null) }
    var currentPlayerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    // Actualizar video cuando cambia el videoId mientras el reproductor ya está listo
    LaunchedEffect(videoId, isVisible, youTubePlayerInstance) {
        val player = youTubePlayerInstance
        val currentId = videoId
        if (isVisible && !currentId.isNullOrBlank() && player != null) {
            hasError = false
            player.loadVideo(currentId, 0f)
        }
    }

    AnimatedVisibility(
        visible = isVisible && !videoId.isNullOrBlank(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF000000))
        ) {
            // Reproductor nativo de YouTube mediante AndroidView
            AndroidView(
                factory = { context ->
                    val playerView = YouTubePlayerView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        // Desactivamos la inicialización automática para poder llamar a initialize() con opciones completas
                        enableAutomaticInitialization = false
                    }

                    // Registra la vista en el ciclo de vida de la actividad/pantalla
                    lifecycleOwner.lifecycle.addObserver(playerView)

                    // Opciones de IFrame: Controles activos, autoplay y origen estándar
                    val options = IFramePlayerOptions.Builder()
                        .controls(1)
                        .autoplay(1)
                        .rel(0)
                        .ivLoadPolicy(3)
                        .build()

                    // Inicialización explícita indispensable para que el WebView renderice el IFrame
                    playerView.initialize(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayerInstance = youTubePlayer
                                isPlayerReady = true
                                val targetId = videoId
                                if (isVisible && !targetId.isNullOrBlank()) {
                                    youTubePlayer.loadVideo(targetId, 0f)
                                }
                            }

                            override fun onError(
                                youTubePlayer: YouTubePlayer,
                                error: PlayerConstants.PlayerError
                            ) {
                                hasError = true
                                val targetId = videoId
                                if (!targetId.isNullOrBlank()) {
                                    // Intento de rescate con cueVideo si loadVideo da restricción
                                    youTubePlayer.cueVideo(targetId, 0f)
                                }
                            }
                        },
                        true,
                        options
                    )

                    currentPlayerView = playerView
                    playerView
                },
                update = { _ ->
                    val player = youTubePlayerInstance
                    val targetId = videoId
                    if (isVisible && !targetId.isNullOrBlank() && player != null) {
                        player.loadVideo(targetId, 0f)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("youtube_player_view")
            )

            // Spinner de carga sutil si el player aún está montando el WebView
            if (!isPlayerReady && !hasError) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Botón de cierre (id: btnClosePlayer) alineado en la esquina superior derecha
            Surface(
                shape = CircleShape,
                color = Color(0xCC0F172A),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
            ) {
                IconButton(
                    onClick = {
                        youTubePlayerInstance?.pause()
                        onClose()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_close_player")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar reproductor",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Limpieza de ciclo de vida al desmontar el composable
    DisposableEffect(lifecycleOwner) {
        onDispose {
            currentPlayerView?.let { view ->
                lifecycleOwner.lifecycle.removeObserver(view)
                view.release()
            }
            youTubePlayerInstance = null
            currentPlayerView = null
            isPlayerReady = false
        }
    }
}
