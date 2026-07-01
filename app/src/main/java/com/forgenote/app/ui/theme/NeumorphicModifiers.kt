package com.forgenote.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

data class NeumorphicTheme(
    val surfaceColor: Color,
    val shadowColor: Color,
    val lightShadowColor: Color
)

fun Modifier.neumorphic(): Modifier = this

fun Modifier.neumorphicSurface(theme: NeumorphicTheme = LocalNeumorphicTheme.current, isPressed: Boolean = false): Modifier = composed {
    this.shadow(elevation = if (isPressed) 2.dp else 8.dp, shape = RectangleShape)
}

fun Modifier.neumorphicButton(theme: NeumorphicTheme = LocalNeumorphicTheme.current, isPressed: Boolean = false): Modifier = composed {
    this.shadow(elevation = if (isPressed) 2.dp else 8.dp, shape = RectangleShape)
}

fun Modifier.neumorphicCard(theme: NeumorphicTheme = LocalNeumorphicTheme.current): Modifier = composed {
    this.shadow(elevation = 6.dp, shape = androidx.compose.material3.MaterialTheme.shapes.medium)
}
