package com.example.practicaapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.practicaapis.ui.navigation.AppNavigation
import com.example.practicaapis.ui.theme.PracticaAPIsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("curio_settings", MODE_PRIVATE)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var themeOverride by rememberSaveable {
                mutableStateOf(if (preferences.contains("dark_theme")) preferences.getBoolean("dark_theme", false) else null)
            }
            val darkTheme = themeOverride ?: systemDark

            SideEffect {
                val transparent = android.graphics.Color.TRANSPARENT
                val barStyle = if (darkTheme) {
                    SystemBarStyle.dark(transparent)
                } else {
                    SystemBarStyle.light(transparent, android.graphics.Color.BLACK)
                }
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
            }

            PracticaAPIsTheme(darkTheme = darkTheme) {
                AppNavigation(darkTheme, onToggleTheme = {
                    val newTheme = !darkTheme
                    themeOverride = newTheme
                    preferences.edit().putBoolean("dark_theme", newTheme).apply()
                })
            }
        }
    }
}
