package com.forgenote.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.forgenote.app.ui.theme.LocalNeumorphicTheme
import com.forgenote.app.ui.theme.neumorphicSurface
import com.forgenote.app.ui.notes.NotesScreen
import com.forgenote.app.ui.notes.NoteEditorScreen
import com.forgenote.app.ui.prompts.PromptsScreen
import com.forgenote.app.ui.gemini.GeminiScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Notes : Screen("notes", "Notes", Icons.Default.Description)
    object Prompts : Screen("prompts", "Prompts", Icons.Default.Terminal)
    object Gemini : Screen("gemini", "Gemini", Icons.Default.Chat)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(Screen.Notes, Screen.Prompts, Screen.Gemini)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val neumorphicTheme = LocalNeumorphicTheme.current
            // Raised Neumorphic Tray Layout Base
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(76.dp)
                    .neumorphicSurface(theme = neumorphicTheme, isPressed = false)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute?.startsWith(screen.route) == true
                    
                    val scaleByState by animateFloatAsState(
                        targetValue = if (isSelected) 0.95f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                        label = "TabScaleAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                            .scale(scaleByState)
                            .neumorphicSurface(theme = neumorphicTheme, isPressed = isSelected)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Notes.route) {
                NotesScreen(
                    onNavigateToEditor = { noteId ->
                        if (noteId != null) {
                            navController.navigate("notes_editor?noteId=$noteId")
                        } else {
                            navController.navigate("notes_editor")
                        }
                    }
                )
            }
            composable(
                route = "notes_editor?noteId={noteId}",
                arguments = listOf(navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull()
                NoteEditorScreen(
                    noteId = noteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Prompts.route) {
                PromptsScreen(
                    onSendToChat = { promptBody ->
                        val encodedPrompt = android.net.Uri.encode(promptBody)
                        navController.navigate("gemini?initialPrompt=$encodedPrompt")
                    }
                )
            }
            composable(
                route = "gemini?initialPrompt={initialPrompt}",
                arguments = listOf(navArgument("initialPrompt") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val initialPrompt = backStackEntry.arguments?.getString("initialPrompt")?.let { android.net.Uri.decode(it) }
                GeminiScreen(initialPrompt = initialPrompt)
            }
        }
    }
}
