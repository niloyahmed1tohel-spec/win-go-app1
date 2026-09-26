package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WinGoDarkColorScheme = darkColorScheme(
    primary = WinGoGreen,
    onPrimary = Color(0xFF022C19),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = WinGoGreenLight,
    secondary = WinGoCyan,
    onSecondary = Color(0xFF083344),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = WinGoCyanLight,
    tertiary = WinGoViolet,
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF581C87),
    onTertiaryContainer = WinGoVioletLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = WinGoRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = WinGoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
