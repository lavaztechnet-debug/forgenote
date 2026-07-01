package com.forgenote.app.ui.notes

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.forgenote.app.data.room.AppDatabase
import com.forgenote.app.data.room.NoteEntity
import com.forgenote.app.data.repository.NotesRepository
import com.forgenote.app.util.FileExporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository
    val allNotes: StateFlow<List<NoteEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NotesRepository(database.noteDao())
        allNotes = repository.allNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }

    fun saveNote(id: Long, title: String, body: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (id == 0L) {
                repository.insertNote(NoteEntity(title = title, body = body, createdAt = now, updatedAt = now))
            } else {
                val existing = repository.getNoteById(id)
                if (existing != null) {
                    repository.updateNote(existing.copy(title = title, body = body, updatedAt = now))
                }
            }
            onComplete()
        }
    }

    fun exportNoteAsHtml(context: Context, title: String, body: String) {
        FileExporter.exportToDownloads(context, title, body, isHtml = true)
    }

    fun exportNoteAsMarkdown(context: Context, title: String, body: String) {
        FileExporter.exportToDownloads(context, title, body, isHtml = false)
    }
}
