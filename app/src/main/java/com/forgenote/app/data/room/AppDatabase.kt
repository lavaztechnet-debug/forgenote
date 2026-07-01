package com.forgenote.app.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEntity::class, PromptEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun promptDao(): PromptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forge_note_database"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).promptDao()
                val now = System.currentTimeMillis()
                val starters = listOf(
                    PromptEntity(
                        title = "Code Optimizer",
                        body = "Analyze the following code block for performance bottlenecks, memory leaks, and architectural optimization improvements. Provide a clean, fully commented refactored version:\n\n[PASTE_CODE]",
                        tag = "Code",
                        createdAt = now
                    ),
                    PromptEntity(
                        title = "Creative Worldbuilder",
                        body = "Generate a rich, deeply atmospheric description of a physical fantasy or sci-fi environment using sensory details (sight, sound, smell, texture). Establish an underlying sense of mystery and tension:\n\n[PASTE_TOPIC]",
                        tag = "Creative",
                        createdAt = now - 1000
                    ),
                    PromptEntity(
                        title = "Structural Analysis",
                        body = "Break down the core arguments, logical fallacies, and underlying assumptions present in the text provided below. Separate your evaluation into structural strengths, fatal flaws, and alternative syntheses:\n\n[PASTE_TEXT]",
                        tag = "Analysis",
                        createdAt = now - 2000
                    ),
                    PromptEntity(
                        title = "Kotlin Jetpack Compose Expert",
                        body = "Act as an elite Android Engineer. Write a production-grade custom layout or modifier in Jetpack Compose to solve the following problem, utilizing drawBehind or graphicsLayer for ultimate performance:\n\n[PASTE_PROBLEM]",
                        tag = "Code",
                        createdAt = now - 3000
                    ),
                    PromptEntity(
                        title = "Narrative Copy Deck",
                        body = "Refine the following rough business concept into a compelling, emotional narrative deck hook targeted at early-stage investors. Maximize clarity, brevity, and emotional resonance:\n\n[PASTE_IDEAS]",
                        tag = "Creative",
                        createdAt = now - 4000
                    )
                )
                dao.insertAll(starters)
            }
        }
    }
}
