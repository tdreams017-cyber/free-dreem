package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LucidColorScheme = darkColorScheme(
  primary = DreamAccent,
  onPrimary = Color.White,
  primaryContainer = DreamCard,
  onPrimaryContainer = TextLight,
  secondary = DreamGlow,
  onSecondary = Color.White,
  tertiary = DreamCyan,
  onTertiary = DreamDark,
  background = DreamDark,
  onBackground = TextLight,
  surface = DreamCard,
  onSurface = TextLight,
  surfaceVariant = DreamCardBorder,
  onSurfaceVariant = TextMuted,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LucidColorScheme,
    typography = Typography,
    content = content
  )
}
