package com.example.practicaapis.data.mappers

import com.example.practicaapis.data.remote.jokes.JokeResponse
import com.example.practicaapis.model.Joke

fun JokeResponse.toDomain(): Joke {
    if (error) {
        throw IllegalArgumentException(message ?: "JokeAPI returned an error")
    }
    if (safe != true) {
        throw IllegalArgumentException("Joke fails safe-mode validation")
    }
    require(lang == "en") { "Joke is not in English" }
    val f = requireNotNull(flags) { "Joke safety flags are missing" }
    if (f.explicit || f.nsfw || f.political || f.racist || f.religious || f.sexist) {
        throw IllegalArgumentException("Joke fails blacklist flags validation")
    }
    val cat = category.orEmpty()
    val jokeId = id ?: 0
    return when (type?.lowercase()) {
        "single" -> {
            val t = joke
            require(!t.isNullOrBlank()) { "Single joke text is missing or blank" }
            Joke.Single(id = jokeId, category = cat, text = t)
        }
        "twopart" -> {
            val s = setup
            val d = delivery
            require(!s.isNullOrBlank() && !d.isNullOrBlank()) { "Two-part joke setup or delivery is missing or blank" }
            Joke.TwoPart(id = jokeId, category = cat, setup = s, delivery = d)
        }
        else -> throw IllegalArgumentException("Unsupported joke type: $type")
    }
}
