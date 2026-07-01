package com.forgenote.app.ui.prompts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.forgenote.app.data.room.AppDatabase
import com.forgenote.app.data.room.PromptEntity
import com.forgenote.app.data.repository.PromptsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PromptsRepository
    val allPrompts: StateFlow<List<PromptEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PromptsRepository(database.promptDao())
        allPrompts = repository.allPrompts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addPrompt(title: String, body: String, tag: String) {
        if (title.isBlank() || body.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repository.insertPrompt(
                PromptEntity(
                    title = title,
                    body = body,
                    tag = tag,
                    createdAt = now
                )
            )
        }
    }

    fun deletePrompt(prompt: PromptEntity) {
        viewModelScope.launch {
            repository.deletePrompt(prompt)
        }
    }
}
