package com.forgenote.app.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class NeumorphicTheme(
    val lightShadow: Color,
    val darkShadow: Color,
    val surfaceColor: Color,
    val cornerRadius: Dp,
    val elevation: Dp
)

val LightNeumorphicTheme = NeumorphicTheme(
    lightShadow = Color(0xFFFFFFFF),
    darkShadow = Color(0xFFBEBEBE),
    surfaceColor = Color(0xFFEDF0F2),
    cornerRadius = 18.dp,
    elevation = 8.dp
)

val DarkNeumorphicTheme = NeumorphicTheme(
    lightShadow = Color(0xFF3A3A48),
    darkShadow = Color(0xFF0E0E14),
    surfaceColor = Color(0xFF252530),
    cornerRadius = 18.dp,
    elevation = 10.dp
)

fun Modifier.neumorphicSurface(theme: NeumorphicTheme, isPressed: Boolean): Modifier = this
    .graphicsLayer {
        clip = false
    }
    .drawBehind {
        val radiusPx = theme.cornerRadius.toPx()
        val elevationPx = theme.elevation.toPx()

        drawIntoCanvas { canvas ->
            val nativePaint = Paint().asFrameworkPaint()
            
            if (!isPressed) {
                // Render Shadow Layers Natively via Paint Shadow Matrix Engine
                nativePaint.color = theme.surfaceColor.value.toInt()
                
                // 1. Bottom-Right Dark Cast Shadow
                nativePaint.setShadowLayer(elevationPx, elevationPx, elevationPx, theme.darkShadow.value.toInt())
                canvas.nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height, radiusPx, radiusPx, nativePaint
                )
                
                // 2. Top-Left Light Ambient Glow
                nativePaint.setShadowLayer(elevationPx, -elevationPx, -elevationPx, theme.lightShadow.value.toInt())
                canvas.nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height, radiusPx, radiusPx, nativePaint
                )
            } else {
                // Inverted State Flat Inset Canvas Rendering Layout Execution
                nativePaint.clearShadowLayer()
                nativePaint.color = theme.surfaceColor.value.toInt()
                canvas.nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height, radiusPx, radiusPx, nativePaint
                )
                
                // Render Inner Frame Structural Inset Depth Simulation
                nativePaint.color = theme.darkShadow.copy(alpha = 0.4f).value.toInt()
                nativePaint.strokeWidth = 4f
                nativePaint.style = android.graphics.Paint.Style.STROKE
                canvas.nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height, radiusPx, radiusPx, nativePaint
                )
            }
        }
    }

fun Modifier.neumorphicButton(theme: NeumorphicTheme, isPressed: Boolean): Modifier = this
    .neumorphicSurface(
        theme = theme.copy(
            cornerRadius = 14.dp,
            elevation = if (isPressed) 4.dp else 12.dp
        ),
        isPressed = isPressed
    )

fun Modifier.neumorphicCard(theme: NeumorphicTheme): Modifier = this
    .neumorphicSurface(
        theme = theme.copy(cornerRadius = 18.dp),
        isPressed = false
    )
