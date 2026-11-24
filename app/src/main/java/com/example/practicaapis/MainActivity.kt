package com.example.practicaapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.practicaapis.ui.CatScreen
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticaAPIsTheme {
                CatScreen()
            }
        }
    }
}
