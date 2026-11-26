package com.example.practicaapis.model

data class Pony(
    val id: Int,
    val name: String,
    val alias: String?,
    val images: List<String>,
    val species: List<String>,
    val occupation: String?,
    val residence: String?,
    val sex: String?,
    val url: String?
)
