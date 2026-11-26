package com.example.practicaapis.model

data class ExtinctAnimal(
    val binomialName: String,
    val commonName: String?,
    val imageUrl: String?,
    val location: String?,
    val lastRecord: String?,
    val description: String?,
    val wikipediaUrl: String?
) {
    val id: String get() = binomialName.lowercase()
    val displayName: String get() = commonName ?: binomialName
}
