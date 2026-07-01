package com.forgenote.app.data.repository

import com.forgenote.app.data.room.PromptDao
import com.forgenote.app.data.room.PromptEntity
import kotlinx.coroutines.flow.Flow

class PromptsRepository(private val promptDao: PromptDao) {
    val allPrompts: Flow<List<PromptEntity>> = promptDao.getAllPrompts()

    suspend fun insertPrompt(prompt: PromptEntity): Long {
        return promptDao.insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: PromptEntity) {
        promptDao.updatePrompt(prompt)
    }

    suspend fun deletePrompt(prompt: PromptEntity) {
        promptDao.deletePrompt(prompt)
    }
}
