package com.forgenote.app.ui.notes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forgenote.app.ui.theme.LocalNeumorphicTheme
import com.forgenote.app.ui.theme.neumorphicButton
import com.forgenote.app.ui.theme.neumorphicSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NoteEditorScreen(
    noteId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val context = LocalContext.current
    val theme = LocalNeumorphicTheme.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != null && !isLoaded) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) {
                title = note.title
                body = note.body
            }
            isLoaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var backPressed by remember { mutableStateOf(false) }
            val backScale by animateFloatAsState(if (backPressed) 0.94f else 1.0f, label = "back")
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(backScale)
                    .neumorphicButton(theme, isPressed = backPressed)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            backPressed = true
                            delay(80)
                            backPressed = false
                            onNavigateBack()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                var htmlPressed by remember { mutableStateOf(false) }
                val htmlScale by animateFloatAsState(if (htmlPressed) 0.94f else 1.0f, label = "html")
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .scale(htmlScale)
                        .neumorphicButton(theme, isPressed = htmlPressed)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                htmlPressed = true
                                delay(80)
                                htmlPressed = false
                                viewModel.exportNoteAsHtml(context, title, body)
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("HTML", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }

                var mdPressed by remember { mutableStateOf(false) }
                val mdScale by animateFloatAsState(if (mdPressed) 0.94f else 1.0f, label = "md")
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .scale(mdScale)
                        .neumorphicButton(theme, isPressed = mdPressed)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                mdPressed = true
                                delay(80)
                                mdPressed = false
                                viewModel.exportNoteAsMarkdown(context, title, body)
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MD", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }

                var savePressed by remember { mutableStateOf(false) }
                val saveScale by animateFloatAsState(if (savePressed) 0.94f else 1.0f, label = "save")
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .scale(saveScale)
                        .neumorphicButton(theme, isPressed = savePressed)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                savePressed = true
                                delay(80)
                                savePressed = false
                                viewModel.saveNote(noteId ?: 0L, title, body) {
                                    onNavigateBack()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save Note", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .neumorphicSurface(theme, isPressed = true)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (title.isEmpty()) {
                Text("Note Title...", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .neumorphicSurface(theme, isPressed = true)
                .background(theme.surfaceColor, shape = MaterialTheme.shapes.medium)
                .drawBehind {
                    val lineColor = if (isDark) Color(0xFF2E2E3A) else Color(0xFFD0CED4)
                    val lineSpacing = 36.dp.toPx()
                    var currentY = lineSpacing
                    while (currentY < size.height) {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, currentY),
                            end = Offset(size.width, currentY),
                            strokeWidth = 1.dp.toPx()
                        )
                        currentY += lineSpacing
                    }
                }
                .padding(16.dp)
        ) {
            if (body.isEmpty()) {
                Text(
                    text = "Begin writing your raw thoughts...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            BasicTextField(
                value = body,
                onValueChange = { body = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 36.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
