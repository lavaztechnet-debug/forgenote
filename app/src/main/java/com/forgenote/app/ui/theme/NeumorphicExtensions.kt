package com.forgenote.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

data class NeumorphicTheme(
    val surfaceColor: androidx.compose.ui.graphics.Color,
    val shadowColor: androidx.compose.ui.graphics.Color,
    val lightShadowColor: androidx.compose.ui.graphics.Color
)

val LocalNeumorphicTheme = androidx.compose.runtime.compositionLocalOf {
    NeumorphicTheme(
        surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        shadowColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f),
        lightShadowColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f)
    )
}

fun Modifier.neumorphic(): Modifier = this

fun Modifier.neumorphicButton(theme: NeumorphicTheme, isPressed: Boolean = false): Modifier {
    return if (isPressed) {
        this.shadow(elevation = 2.dp, shape = RectangleShape)
    } else {
        this.shadow(elevation = 8.dp, shape = RectangleShape)
    }
}

fun Modifier.neumorphicSurface(theme: NeumorphicTheme, isPressed: Boolean = false): Modifier {
    return if (isPressed) {
        this.shadow(elevation = 2.dp, shape = RectangleShape)
    } else {
        this.shadow(elevation = 8.dp, shape = RectangleShape)
    }
}

fun Modifier.neumorphicCard(theme: NeumorphicTheme): Modifier {
    return this.shadow(elevation = 6.dp, shape = androidx.compose.material3.MaterialTheme.shapes.medium)
}
