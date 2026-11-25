package com.example.practicaapis.data.remote.jokes

data class JokeResponse(
    val category: String,
    val type: String,
    val joke: String? = null,
    val setup: String? = null,
    val delivery: String? = null,
    val flags: Flags,
    val id: Int,
    val safe: Boolean,
    val lang: String,
    val error: Boolean
)

data class Flags(
    val explicit: Boolean,
    val nsfw: Boolean,
    val political: Boolean,
    val racist: Boolean,
    val religious: Boolean,
    val sexist: Boolean
)

