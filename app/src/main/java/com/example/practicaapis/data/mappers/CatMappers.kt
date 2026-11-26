package com.example.practicaapis.data.mappers

import com.example.practicaapis.data.remote.catFacts.Data
import com.example.practicaapis.model.CatBreed

internal fun Data.toCatBreed(): CatBreed? {
    val name = breed.nonBlank() ?: return null
    return CatBreed(name, country.nonBlank(), origin.nonBlank(), coat.nonBlank(), pattern.nonBlank())
}

private fun String?.nonBlank(): String? = this?.trim()?.takeIf(String::isNotEmpty)
