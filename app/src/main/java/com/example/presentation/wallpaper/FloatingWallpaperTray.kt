package com.example.presentation.wallpaper

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.wallpaper.WallpaperItem
import com.example.data.wallpaper.WallpaperManager

/**
 * Bandeja flotante para cambiar el fondo de pantalla de la aplicación.
 * Muestra todos los fondos disponibles en la carpeta 'app/src/main/assets/wallpapers/',
 * permitiendo seleccionar uno como fondo general de toda la app.
 */
@Composable
fun FloatingWallpaperTray(
  isOpen: Boolean,
  onClose: () -> Unit,
  wallpaperManager: WallpaperManager,
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  BackHandler { onClose() }

  androidx.compose.runtime.LaunchedEffect(isOpen) {
    if (isOpen) {
      wallpaperManager.refresh()
    }
  }

  val wallpapers by wallpaperManager.wallpapers.collectAsStateWithLifecycle()
  val selectedWallpaper by wallpaperManager.selectedWallpaper.collectAsStateWithLifecycle()
  val dimAlpha by wallpaperManager.dimAlpha.collectAsStateWithLifecycle()

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("floating_wallpaper_tray_overlay"),
    contentAlignment = Alignment.Center
  ) {
    // Fondo oscurecido para enfocar la bandeja flotante y cerrar al tocar fuera
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.65f))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) { onClose() }
    )

    // Contenedor flotante centrado con estética cyber-oscura
    AnimatedVisibility(
      visible = isOpen,
      enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .fillMaxHeight(0.78f)
          .shadow(16.dp, shape = RoundedCornerShape(20.dp))
          .testTag("floating_wallpaper_dialog"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(
          1.2.dp,
          Brush.verticalGradient(
            listOf(
              Color(0xFFE879F9).copy(alpha = 0.8f),
              Color(0xFF8B5CF6).copy(alpha = 0.4f)
            )
          )
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Barra superior con Icono, Título y Botón Cerrar X
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(Color(0xFFE879F9).copy(alpha = 0.15f), shape = CircleShape)
                  .border(1.dp, Color(0xFFE879F9).copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Wallpaper,
                  contentDescription = "Icono Fondo de Pantalla",
                  tint = Color(0xFFE879F9),
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = "Fondos de Pantalla",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFF8FAFC)
                  ),
                  modifier = Modifier.testTag("floating_wallpaper_tray_title")
                )
                Text(
                  text = "Elige el fondo general de toda la app",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                  )
                )
              }
            }

            IconButton(
              onClick = onClose,
              modifier = Modifier
                .size(36.dp)
                .testTag("btn_close_wallpaper_tray_x")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Lista virtualizada con LazyColumn para soportar todos los 60+ fondos fluidamente
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Control de Atenuación (Dimming Slider)
            if (!selectedWallpaper.isDefaultNone) {
              item(key = "wallpaper_dim_slider_card") {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                  border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Atenuación del fondo",
                        style = MaterialTheme.typography.labelMedium.copy(
                          color = Color(0xFFF1F5F9),
                          fontWeight = FontWeight.SemiBold
                        )
                      )
                      Text(
                        text = "${(dimAlpha * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                          color = Color(0xFFE879F9),
                          fontWeight = FontWeight.Bold
                        )
                      )
                    }
                    Slider(
                      value = dimAlpha,
                      onValueChange = { wallpaperManager.updateDimAlpha(it) },
                      valueRange = 0.10f..0.85f,
                      colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE879F9),
                        activeTrackColor = Color(0xFFE879F9),
                        inactiveTrackColor = Color(0xFF334155)
                      ),
                      modifier = Modifier.testTag("slider_wallpaper_dim")
                    )
                  }
                }
              }
            }

            item(key = "wallpaper_section_header") {
              Text(
                text = "Fondos disponibles (${wallpapers.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp, bottom = 4.dp)
              )
            }

            items(
              items = wallpapers,
              key = { it.id }
            ) { wallpaper ->
              val isSelected = selectedWallpaper.id == wallpaper.id ||
                (!wallpaper.isDefaultNone && selectedWallpaper.fileName == wallpaper.fileName)
              WallpaperCardItem(
                wallpaper = wallpaper,
                isSelected = isSelected,
                onClick = {
                  wallpaperManager.selectWallpaper(wallpaper)
                  onClose()
                }
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Botón Cerrar Bandeja Flotante
          Button(
            onClick = onClose,
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("btn_close_wallpaper_tray"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFE879F9),
              contentColor = Color.Black
            )
          ) {
            Text(
              text = "Listo",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }
      }
    }
  }
}

/**
 * Tarjeta individual para previsualizar y elegir un fondo de pantalla.
 */
@Composable
private fun WallpaperCardItem(
  wallpaper: WallpaperItem,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val borderColor = if (isSelected) Color(0xFFE879F9) else Color(0xFF334155)
  val backgroundColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF161F30)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("wallpaper_item_${wallpaper.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Miniatura / Preview
      Box(
        modifier = Modifier
          .size(width = 56.dp, height = 76.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF0B1120))
          .border(1.dp, if (isSelected) Color(0xFFE879F9).copy(alpha = 0.5f) else Color(0xFF334155), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        if (wallpaper.isDefaultNone || wallpaper.assetPath == null) {
          // Previsualización de tema estándar oscuro
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Image,
              contentDescription = null,
              tint = Color(0xFF64748B),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Oscuro",
              fontSize = 9.sp,
              color = Color(0xFF64748B),
              fontWeight = FontWeight.SemiBold
            )
          }
        } else {
          // Carga miniatura desde assets con tamaño acotado para alta fluidez
          val assetUrl = "file:///android_asset/${wallpaper.assetPath}"
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(assetUrl)
              .crossfade(true)
              .size(160, 220)
              .build(),
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      // Información del fondo
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = wallpaper.title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFFF8FAFC) else Color(0xFFE2E8F0),
            fontSize = 14.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = wallpaper.fileName ?: "Tema predeterminado del sistema",
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (isSelected) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE879F9).copy(alpha = 0.18f),
            border = BorderStroke(1.dp, Color(0xFFE879F9).copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFFE879F9),
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "Fondo Activo",
                fontSize = 10.sp,
                color = Color(0xFFE879F9),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Botón de acción / selección
      if (!isSelected) {
        OutlinedButton(
          onClick = onClick,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF38BDF8)
          ),
          border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
          modifier = Modifier
            .height(34.dp)
            .testTag("btn_select_wallpaper_${wallpaper.id}")
        ) {
          Text(text = "Elegir", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
