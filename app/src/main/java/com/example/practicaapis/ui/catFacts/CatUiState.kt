package com.example.practicaapis.ui.catFacts

import androidx.annotation.StringRes
import com.example.practicaapis.R
import com.example.practicaapis.model.CatBreed

internal enum class CoatFilter(@get:StringRes val label: Int) {
    All(R.string.cats_filter_all),
    Short(R.string.cats_filter_short),
    SemiLong(R.string.cats_filter_semi_long),
    Curly(R.string.cats_filter_curly);

    fun matches(coat: String?): Boolean = when (this) {
        All -> true
        Short -> shortCoat.containsMatchIn(coat.orEmpty())
        SemiLong -> semiLongCoat.containsMatchIn(coat.orEmpty())
        Curly -> curlyCoat.containsMatchIn(coat.orEmpty())
    }

    private companion object {
        val shortCoat = Regex("\\bshort\\b", RegexOption.IGNORE_CASE)
        val semiLongCoat = Regex("\\bsemi[ -]+long\\b", RegexOption.IGNORE_CASE)
        val curlyCoat = Regex("\\b(rex|curly)\\b", RegexOption.IGNORE_CASE)
    }
}

internal data class CatUiState(
    val catalog: List<CatBreed> = emptyList(),
    val query: String = "",
    val filter: CoatFilter = CoatFilter.All,
    val loading: Boolean = true,
    val error: Boolean = false
) {
    val breeds: List<CatBreed> get() = catalog.filter {
        it.name.contains(query.trim(), ignoreCase = true) && filter.matches(it.coat)
    }
}
