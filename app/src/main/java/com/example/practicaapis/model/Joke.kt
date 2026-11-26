package com.example.practicaapis.model

enum class JokeCategory(val apiName: String) {
    ALL("Any"),
    GENERAL("Miscellaneous"),
    PROGRAMMING("Programming")
}

sealed interface Joke {
    val id: Int
    val category: String

    data class Single(
        override val id: Int,
        override val category: String,
        val text: String,
    ) : Joke

    data class TwoPart(
        override val id: Int,
        override val category: String,
        val setup: String,
        val delivery: String,
    ) : Joke
}
