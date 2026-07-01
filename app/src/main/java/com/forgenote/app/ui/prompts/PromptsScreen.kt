package com.forgenote.app.ui.prompts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forgenote.app.data.room.PromptEntity
import com.forgenote.app.ui.theme.LocalNeumorphicTheme
import com.forgenote.app.ui.theme.neumorphicButton
import com.forgenote.app.ui.theme.neumorphicCard
import com.forgenote.app.ui.theme.neumorphicSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PromptsScreen(
    onSendToChat: (String) -> Unit,
    viewModel: PromptsViewModel = viewModel()
) {
    val prompts by viewModel.allPrompts.collectAsState()
    val theme = LocalNeumorphicTheme.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var inputTitle by remember { mutableStateOf("") }
    var inputBody by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Code") }
    val tags = listOf("Code", "Creative", "Analysis")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Prompt Engineering Lab",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Form Container Input Block (Raised Panel Frame)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphicCard(theme)
                .background(theme.surfaceColor, shape = MaterialTheme.shapes.medium)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Forge Custom Template",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Title Input (Carved Well)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .neumorphicSurface(theme, isPressed = true)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputTitle.isEmpty()) {
                    Text("Template title...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BasicTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Body Input (Carved Well)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .neumorphicSurface(theme, isPressed = true)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (inputBody.isEmpty()) {
                    Text("Enter raw prompt metadata structure...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BasicTextField(
                    value = inputBody,
                    onValueChange = { inputBody = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Tag Select Execution Platform
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { tag ->
                    val isTagSelected = selectedTag == tag
                    val tagScale by animateFloatAsState(if (isTagSelected) 0.96f else 1.0f, label = "TagScale")
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .scale(tagScale)
                            .neumorphicSurface(theme, isPressed = isTagSelected)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedTag = tag
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isTagSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Forge Action Execute Execution Deck
            var forgePressed by remember { mutableStateOf(false) }
            val forgeScale by animateFloatAsState(if (forgePressed) 0.95f else 1.0f, label = "ForgeScale")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .scale(forgeScale)
                    .neumorphicButton(theme, isPressed = forgePressed)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            forgePressed = true
                            delay(80)
                            forgePressed = false
                            if (inputTitle.isNotBlank() && inputBody.isNotBlank()) {
                                viewModel.addPrompt(inputTitle, inputBody, selectedTag)
                                inputTitle = ""
                                inputBody = ""
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FORGE TEMPLATE",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prompts Scrolling Display Stream
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(prompts, key = { it.id }) { prompt ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicCard(theme)
                        .background(theme.surfaceColor, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = prompt.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Tag: ${prompt.tag}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = { viewModel.deletePrompt(prompt) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Prompt",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prompt.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Operational Deck Controls Layout Frame
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            var copyPressed by remember { mutableStateOf(false) }
                            val copyScale by animateFloatAsState(if (copyPressed) 0.94f else 1.0f, label = "CopyAction")
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .scale(copyScale)
                                    .neumorphicButton(theme, isPressed = copyPressed)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        scope.launch {
                                            copyPressed = true
                                            delay(80)
                                            copyPressed = false
                                            clipboardManager.setText(AnnotatedString(prompt.body))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                                    Text("Copy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            var sendPressed by remember { mutableStateOf(false) }
                            val sendScale by animateFloatAsState(if (sendPressed) 0.94f else 1.0f, label = "SendAction")
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .scale(sendScale)
                                    .neumorphicButton(theme, isPressed = sendPressed)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        scope.launch {
                                            sendPressed = true
                                            delay(80)
                                            sendPressed = false
                                            onSendToChat(prompt.body)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text("Send to AI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
