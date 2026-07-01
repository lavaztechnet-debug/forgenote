package com.forgenote.app.ui.gemini

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgenote.app.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long = System.currentTimeMillis() + (0..1000).random(),
    val text: String,
    val isUser: Boolean
)

class GeminiViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    fun sendMessage(promptText: String) {
        if (promptText.isBlank() || _isGenerating.value) return

        val userMessage = ChatMessage(text = promptText, isUser = true)
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        val aiMessageId = System.currentTimeMillis() + 2000
        val initialAiMessage = ChatMessage(id = aiMessageId, text = "", isUser = false)
        _messages.value = _messages.value + initialAiMessage

        viewModelScope.launch {
            try {
                var dynamicAccumulator = ""
                generativeModel.generateContentStream(promptText).collect { chunk ->
                    dynamicAccumulator += chunk.text ?: ""
                    _messages.value = _messages.value.map { msg ->
                        if (msg.id == aiMessageId) {
                            msg.copy(text = dynamicAccumulator)
                        } else {
                            msg
                        }
                    }
                }
            } catch (e: Exception) {
                _messages.value = _messages.value.map { msg ->
                    if (msg.id == aiMessageId) {
                        msg.copy(text = "Error communicating with Gemini engine: ${e.localizedMessage}")
                    } else {
                        msg
                    }
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
