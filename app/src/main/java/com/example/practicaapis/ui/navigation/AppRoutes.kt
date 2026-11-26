package com.example.practicaapis.ui.navigation

import androidx.annotation.StringRes
import com.example.practicaapis.R
import com.example.practicaapis.ui.components.CurioSymbol

enum class AppRoute(val route: String, @StringRes val label: Int, val icon: CurioSymbol) {
    Home("home", R.string.nav_home, CurioSymbol.Explore),
    Cats("cats", R.string.nav_cats, CurioSymbol.Pets),
    Extinct("extinct", R.string.nav_extinct, CurioSymbol.Eco),
    Jokes("jokes", R.string.nav_jokes, CurioSymbol.Smile),
    Ponies("ponies", R.string.nav_ponies, CurioSymbol.Sparkles)
}
