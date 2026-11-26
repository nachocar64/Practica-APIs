package com.example.practicaapis.data.remote.jokes

data class JokeResponse(
    val category: String? = null,
    val type: String? = null,
    val joke: String? = null,
    val setup: String? = null,
    val delivery: String? = null,
    val flags: Flags? = null,
    val id: Int? = null,
    val safe: Boolean? = null,
    val lang: String? = null,
    val error: Boolean = false,
    val message: String? = null,
    val additionalInfo: String? = null
)

data class Flags(
    val explicit: Boolean = false,
    val nsfw: Boolean = false,
    val political: Boolean = false,
    val racist: Boolean = false,
    val religious: Boolean = false,
    val sexist: Boolean = false
)
