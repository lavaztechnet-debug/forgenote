package com.forgenote.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Dark Theme Neumorphic Configuration
val DarkNeumorphicTheme = NeumorphicTheme(
    surfaceColor = Color(0xFF121212),
    shadowColor = Color.Black.copy(alpha = 0.3f),
    lightShadowColor = Color.White.copy(alpha = 0.1f)
)

// Light Theme Neumorphic Configuration
val LightNeumorphicTheme = NeumorphicTheme(
    surfaceColor = Color(0xFFE0E0E0),
    shadowColor = Color.Black.copy(alpha = 0.2f),
    lightShadowColor = Color.White.copy(alpha = 0.5f)
)

// Material 3 Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentTeal,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

// Material 3 Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = LightAccentTeal,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightShadowHighlight,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

/**
 * Main Theme Composable for ForgeNote App
 * Applies Material 3 theming and Neumorphic design system
 * 
 * @param darkTheme Boolean indicating dark theme preference (defaults to system setting)
 * @param content The composable content to theme
 */
@Composable
fun ForgeNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val neumorphicTheme = if (darkTheme) DarkNeumorphicTheme else LightNeumorphicTheme

    CompositionLocalProvider(
        LocalNeumorphicTheme provides neumorphicTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
