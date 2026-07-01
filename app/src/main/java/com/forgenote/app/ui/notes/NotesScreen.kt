package com.forgenote.app.ui.notes

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forgenote.app.data.room.NoteEntity
import com.forgenote.app.ui.theme.LocalNeumorphicTheme
import com.forgenote.app.ui.theme.neumorphicCard
import com.forgenote.app.ui.theme.neumorphicButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    onNavigateToEditor: (Long?) -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val notes by viewModel.allNotes.collectAsState()
    val theme = LocalNeumorphicTheme.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "FABPulse")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FABScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "ForgeNote Vault",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records found. Press + to forge.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        var itemPressed by remember { mutableStateOf(false) }
                        val cardScale by animateFloatAsState(
                            targetValue = if (itemPressed) 0.96f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                            label = "CardPress"
                        )

                        val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
                        val dateStr = sdf.format(Date(note.updatedAt))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(cardScale)
                                .neumorphicCard(theme)
                                .background(theme.surfaceColor, shape = MaterialTheme.shapes.medium)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch {
                                        itemPressed = true
                                        delay(80)
                                        itemPressed = false
                                        onNavigateToEditor(note.id)
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(end = 40.dp)) {
                                Text(
                                    text = if (note.title.isBlank()) "Untitled" else note.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteNote(note) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        var fabPressed by remember { mutableStateOf(false) }
        val animatedFabScale by animateFloatAsState(
            targetValue = if (fabPressed) 0.94f else fabScale,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
            label = "FABTouch"
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 8.dp)
                .size(64.dp)
                .scale(animatedFabScale)
                .neumorphicButton(theme, isPressed = fabPressed)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF63B4C2), Color(0xFF4F98A3), Color(0xFF2A6169))
                        } else {
                            listOf(Color(0xFF3B9CB0), Color(0xFF2A7A8C), Color(0xFF194C57))
                        }
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    scope.launch {
                        fabPressed = true
                        delay(100)
                        fabPressed = false
                        onNavigateToEditor(null)
                    }
                },
            contentAlignment = Alignment.Center
                ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Note",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
