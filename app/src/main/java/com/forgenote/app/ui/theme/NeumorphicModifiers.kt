package com.forgenote.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

// Neumorphic Theme Data Class
data class NeumorphicTheme(
    val surfaceColor: Color,
    val shadowColor: Color,
    val lightShadowColor: Color
)

// Composition Local Provider for Neumorphic Theme - initialized with light theme defaults
val LocalNeumorphicTheme = staticCompositionLocalOf { 
    NeumorphicTheme(
        surfaceColor = Color(0xFFE0E0E0),
        shadowColor = Color.Black.copy(alpha = 0.2f),
        lightShadowColor = Color.White.copy(alpha = 0.5f)
    )
}

// Base Neumorphic Modifier - Non-composable version for use in non-composable contexts
fun Modifier.neumorphic(): Modifier = this

// Composable Surface Neumorphic Modifier with elevation and shadow
// Used for raised surfaces with press state
fun Modifier.neumorphicSurface(isPressed: Boolean = false): Modifier = composed {
    val theme = LocalNeumorphicTheme.current
    this.shadow(elevation = if (isPressed) 2.dp else 8.dp, shape = RectangleShape)
}

// Composable Button Neumorphic Modifier with press state
// Provides haptic-style elevation changes on interaction
fun Modifier.neumorphicButton(isPressed: Boolean = false): Modifier = composed {
    val theme = LocalNeumorphicTheme.current
    this.shadow(elevation = if (isPressed) 2.dp else 8.dp, shape = RectangleShape)
}

// Composable Card Neumorphic Modifier for card surfaces
// Provides consistent shadow for card-like containers
fun Modifier.neumorphicCard(): Modifier = composed {
    val theme = LocalNeumorphicTheme.current
    this.shadow(elevation = 6.dp, shape = MaterialTheme.shapes.medium)
}
