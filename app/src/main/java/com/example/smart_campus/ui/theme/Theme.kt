package com.example.smart_campus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.smart_campus.screen.AppThemeState

// ── Light color scheme ────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = Color(0xFF2E7D32),
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFE8F5E9),
    onPrimaryContainer   = Color(0xFF1B5E20),
    secondary            = Color(0xFF4CAF50),
    onSecondary          = Color.White,
    background           = Color(0xFFF2F4F7),
    onBackground         = Color(0xFF1A1A1A),
    surface              = Color(0xFFFFFFFF),
    onSurface            = Color(0xFF1A1A1A),
    surfaceVariant       = Color(0xFFF5F5F5),
    onSurfaceVariant     = Color(0xFF757575),
    outline              = Color(0xFFE0E0E0),
    error                = Color(0xFFD32F2F),
    onError              = Color.White,
)

// ── Dark color scheme ─────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFF66BB6A),
    onPrimary            = Color(0xFF1B5E20),
    primaryContainer     = Color(0xFF2E7D32),
    onPrimaryContainer   = Color(0xFFE8F5E9),
    secondary            = Color(0xFF4CAF50),
    onSecondary          = Color(0xFF1B5E20),
    background           = Color(0xFF121212),
    onBackground         = Color(0xFFE0E0E0),
    surface              = Color(0xFF1E1E1E),
    onSurface            = Color(0xFFE0E0E0),
    surfaceVariant       = Color(0xFF2C2C2C),
    onSurfaceVariant     = Color(0xFF9E9E9E),
    outline              = Color(0xFF424242),
    error                = Color(0xFFEF9A9A),
    onError              = Color(0xFF7F0000),
)

// ── Smart_campusTheme ─────────────────────────────────────────────────────────

@Composable
fun Smart_campusTheme(
    darkTheme: Boolean = AppThemeState.isDarkMode,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}