package com.forgenote.app.ui.gemini

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forgenote.app.ui.theme.LocalNeumorphicTheme
import com.forgenote.app.ui.theme.neumorphicButton
import com.forgenote.app.ui.theme.neumorphicCard
import com.forgenote.app.ui.theme.neumorphicSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GeminiScreen(
    initialPrompt: String?,
    viewModel: GeminiViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val theme = LocalNeumorphicTheme.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var textInput by remember { mutableStateOf("") }
    var inputTokenProcessed by remember { mutableStateOf(false) }

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank() && !inputTokenProcessed) {
            inputTokenProcessed = true
            viewModel.sendMessage(initialPrompt)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Gemini Flash Terminal",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Conversation Stream Layout Core
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                val isUser = message.isUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .wrapContentWidth(align = if (isUser) Alignment.End else Alignment.Start)
                            // User messages are raised neumorphic pills; AI are flat inset style
                            .neumorphicSurface(theme = theme, isPressed = !isUser)
                            .background(theme.surfaceColor, shape = MaterialTheme.shapes.medium)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isUser) "Operator" else "Gemini Intelligence",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = message.text.ifBlank { "Forging thought matrix stream..." },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        if (isGenerating && messages.lastOrNull()?.text?.isBlank() == true) {
            Text(
                text = "⚡ Realtime compute cycles active...",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        // Input Control Processing Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Input Structural Well
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .neumorphicSurface(theme, isPressed = true)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textInput.isEmpty()) {
                    Text(
                        text = "Transmit instructions...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Command Send Button Execution Deck
            var sendPressed by remember { mutableStateOf(false) }
            val sendScale by animateFloatAsState(if (sendPressed) 0.94f else 1.0f, label = "SendPressAnimation")
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(sendScale)
                    .neumorphicButton(theme, isPressed = sendPressed)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            if (textInput.isNotBlank() && !isGenerating) {
                                sendPressed = true
                                delay(80)
                                sendPressed = false
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Transmit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
