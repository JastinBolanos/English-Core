package com.example.presentation.wallpaper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.wallpaper.WallpaperItem

/**
 * Renderiza el fondo de pantalla general para toda la aplicación con una capa
 * de atenuación configurable para garantizar que las tarjetas y textos permanezcan
 * perfectamente nítidos y legibles.
 */
@Composable
fun AppWallpaperBackground(
  wallpaper: WallpaperItem?,
  dimAlpha: Float = 0.40f,
  modifier: Modifier = Modifier
) {
  if (wallpaper == null || wallpaper.isDefaultNone || wallpaper.assetPath == null) {
    return
  }

  val context = LocalContext.current
  val assetUrl = "file:///android_asset/${wallpaper.assetPath}"

  Box(modifier = modifier.fillMaxSize()) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(assetUrl)
        .crossfade(true)
        .build(),
      contentDescription = "Fondo de pantalla: ${wallpaper.title}",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Filtro oscuro (dim scrim) ajustable para legibilidad óptima de textos y tarjetas
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = dimAlpha))
    )
  }
}
