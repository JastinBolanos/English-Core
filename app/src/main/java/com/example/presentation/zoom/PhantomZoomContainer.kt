package com.example.presentation.zoom

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.hypot

/**
 * Contenedor fantasma de zoom y paneo táctil con dos dedos.
 * No agrega ningún elemento a la interfaz ni altera botones o desplazamientos
 * con un solo dedo. Al detectar dos dedos expandiéndose o encogiéndose, hace zoom
 * focalizado exactamente en el lugar donde están los dedos.
 */
@Composable
fun PhantomZoomContainer(
  modifier: Modifier = Modifier,
  maxScale: Float = 5.0f,
  content: @Composable () -> Unit
) {
  val scope = rememberCoroutineScope()
  var containerWidth by remember { mutableFloatStateOf(1f) }
  var containerHeight by remember { mutableFloatStateOf(1f) }

  var scale by remember { mutableFloatStateOf(1.0f) }
  var offsetX by remember { mutableFloatStateOf(0.0f) }
  var offsetY by remember { mutableFloatStateOf(0.0f) }

  var resetJob by remember { mutableStateOf<Job?>(null) }
  var lastTwoFingerTapTime by remember { mutableLongStateOf(0L) }

  // Restablece suavemente la escala y posición a 1.0x (normal)
  fun animateToNormal() {
    resetJob?.cancel()
    resetJob = scope.launch {
      val startScale = scale
      val startX = offsetX
      val startY = offsetY
      androidx.compose.animation.core.animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = spring(
          stiffness = Spring.StiffnessMediumLow,
          dampingRatio = Spring.DampingRatioNoBouncy
        )
      ) { progress, _ ->
        val currentScale = (startScale + (1.0f - startScale) * progress).coerceAtLeast(1.0f)
        scale = currentScale
        val maxOffsetX = ((currentScale - 1f) * containerWidth / 2f).coerceAtLeast(0f)
        val maxOffsetY = ((currentScale - 1f) * containerHeight / 2f).coerceAtLeast(0f)
        offsetX = (startX + (0.0f - startX) * progress).coerceIn(-maxOffsetX, maxOffsetX)
        offsetY = (startY + (0.0f - startY) * progress).coerceIn(-maxOffsetY, maxOffsetY)
      }
      scale = 1.0f
      offsetX = 0.0f
      offsetY = 0.0f
    }
  }

  // Si está ampliado y el usuario pulsa atrás en Android, des-amplía primero
  BackHandler(enabled = scale > 1.02f) {
    animateToNormal()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .onSizeChanged { size ->
        containerWidth = size.width.toFloat().coerceAtLeast(1f)
        containerHeight = size.height.toFloat().coerceAtLeast(1f)
      }
      .pointerInput(Unit) {
        awaitEachGesture {
          // Permitir que los toques de 1 solo dedo pasen de inmediato a la lista sin esperas
          awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)

          var prevCentroid = Offset.Unspecified
          var prevDistance = 0f
          var isTwoFingerActive = false
          val gestureStartTime = System.currentTimeMillis()
          var totalMovement = 0f

          do {
            // Usar PointerEventPass.Main para que el scroll y toques fluyan directamente sin retrasos
            val event = awaitPointerEvent(pass = PointerEventPass.Main)
            val pressedPointers = event.changes.filter { it.pressed }

            if (pressedPointers.size >= 2) {
              // Dos dedos detectados en la pantalla
              if (!isTwoFingerActive) {
                resetJob?.cancel()
                isTwoFingerActive = true
              }

              val p1 = pressedPointers[0].position
              val p2 = pressedPointers[1].position
              val centroid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
              val distance = hypot(p1.x - p2.x, p1.y - p2.y)

              if (prevCentroid != Offset.Unspecified && prevDistance > 5f && distance > 5f) {
                val zoomFactor = distance / prevDistance
                val panDelta = centroid - prevCentroid
                totalMovement += hypot(panDelta.x, panDelta.y)

                val oldScale = scale
                // El zoom SOLO sirve para agrandar: escala mínima estricta de 1.0f (nunca achica la app)
                val newScale = (oldScale * zoomFactor).coerceIn(1.0f, maxScale)
                val scaleRatio = if (oldScale > 0.001f) newScale / oldScale else 1f

                val center = Offset(containerWidth / 2f, containerHeight / 2f)
                val targetOffsetX = (prevCentroid.x - center.x) * (1f - scaleRatio) + panDelta.x + (offsetX * scaleRatio)
                val targetOffsetY = (prevCentroid.y - center.y) * (1f - scaleRatio) + panDelta.y + (offsetY * scaleRatio)

                val maxOffsetX = ((newScale - 1f) * containerWidth / 2f).coerceAtLeast(0f)
                val maxOffsetY = ((newScale - 1f) * containerHeight / 2f).coerceAtLeast(0f)

                if (newScale <= 1.0001f) {
                  scale = 1.0f
                  offsetX = 0f
                  offsetY = 0f
                } else {
                  scale = newScale
                  offsetX = targetOffsetX.coerceIn(-maxOffsetX, maxOffsetX)
                  offsetY = targetOffsetY.coerceIn(-maxOffsetY, maxOffsetY)
                }
              }

              prevCentroid = centroid
              prevDistance = distance

              // Consumir el evento durante el gesto de 2 dedos para evitar clics accidentales debajo
              event.changes.forEach { it.consume() }
            } else if (isTwoFingerActive) {
              // Uno o ambos dedos se levantaron; seguir consumiendo hasta liberar para evitar clics fantasma
              event.changes.forEach { it.consume() }
              prevCentroid = Offset.Unspecified
              prevDistance = 0f
            }
          } while (event.changes.any { it.pressed })

          if (isTwoFingerActive) {
            val gestureDuration = System.currentTimeMillis() - gestureStartTime
            // Detección de doble toque con 2 dedos para resetear zoom al instante
            if (gestureDuration < 300L && totalMovement < 30f) {
              val now = System.currentTimeMillis()
              if (now - lastTwoFingerTapTime < 450L) {
                animateToNormal()
              }
              lastTwoFingerTapTime = now
            }

            // Al soltar los dedos: si quedó en o cerca de 1.0x, asegurar reset a 1.0x
            if (scale <= 1.02f) {
              animateToNormal()
            } else {
              // Ajustar a los límites máximos si quedó fuera
              val maxOffsetX = ((scale - 1f) * containerWidth / 2f).coerceAtLeast(0f)
              val maxOffsetY = ((scale - 1f) * containerHeight / 2f).coerceAtLeast(0f)
              val clampedX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
              val clampedY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
              if (clampedX != offsetX || clampedY != offsetY) {
                resetJob?.cancel()
                resetJob = scope.launch {
                  val startX = offsetX
                  val startY = offsetY
                  androidx.compose.animation.core.animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = spring(
                      stiffness = Spring.StiffnessMediumLow,
                      dampingRatio = Spring.DampingRatioNoBouncy
                    )
                  ) { progress, _ ->
                    offsetX = startX + (clampedX - startX) * progress
                    offsetY = startY + (clampedY - startY) * progress
                  }
                  offsetX = clampedX
                  offsetY = clampedY
                }
              }
            }
          }
        }
      }
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
        translationX = offsetX
        translationY = offsetY
      }
  ) {
    content()
  }
}
