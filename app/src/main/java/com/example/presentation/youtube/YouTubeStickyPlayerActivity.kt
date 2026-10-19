package com.example.presentation.youtube

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

/**
 * Actividad que implementa el Sticky Player de YouTube con ViewBinding y ConstraintLayout.
 * Cumple estrictamente con las instrucciones de integración:
 * 1. Inicialización con AbstractYouTubePlayerListener y guardado de instancia de YouTubePlayer.
 * 2. Registro en el ciclo de vida: lifecycle.addObserver(binding.youtubePlayerView).
 * 3. Función pública onVideoSelected(videoId: String) para mostrar playerContainer y llamar a loadVideo.
 * 4. Configuración de clic en btnClosePlayer para pausar y ocultar el playerContainer (View.GONE).
 * 5. Liberación de recursos con release() en onDestroy().
 */
class YouTubeStickyPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var youTubePlayerInstance: YouTubePlayer? = null
    private var pendingVideoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Registra la vista en el ciclo de vida
        lifecycle.addObserver(binding.youtubePlayerView)

        // 1. En la inicialización, inicializa con AbstractYouTubePlayerListener
        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayerInstance = youTubePlayer
                // Si había un video pendiente de carga antes de que el player estuviera listo:
                pendingVideoId?.let { videoId ->
                    youTubePlayer.loadVideo(videoId, 0f)
                    pendingVideoId = null
                }
            }
        })

        // 4. Configura el evento de clic para btnClosePlayer
        binding.btnClosePlayer.setOnClickListener {
            youTubePlayerInstance?.pause()
            binding.playerContainer.visibility = View.GONE
        }

        // Demo button opcional en el header para probar rápidamente
        binding.btnDemoVideo.setOnClickListener {
            // Video de ejemplo de aprendizaje de vocabulario y pronunciación en inglés
            onVideoSelected("d93m4s_Gf5A")
        }
    }

    /**
     * 3. Función pública que hace visible el playerContainer y carga el video.
     */
    fun onVideoSelected(videoId: String) {
        binding.playerContainer.visibility = View.VISIBLE
        val player = youTubePlayerInstance
        if (player != null) {
            player.loadVideo(videoId, 0f)
        } else {
            pendingVideoId = videoId
        }
    }

    /**
     * 5. Incluye la destrucción del reproductor en onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }
}
