package com.forgenote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.forgenote.app.navigation.AppNavigation
import com.forgenote.app.ui.theme.ForgeNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgeNoteTheme {
                AppNavigation()
            }
        }
    }
}
