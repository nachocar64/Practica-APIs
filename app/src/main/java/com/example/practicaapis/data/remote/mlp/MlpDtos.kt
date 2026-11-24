package com.example.practicaapis.data.remote.mlp

data class PonyResponse(
    val `data`: List<Data>,
    val status: Int
)

data class Data(
    val alias: String,
    val id: Int,
    val image: List<String>,
    val kind: List<String>,
    val name: String,
    val occupation: String,
    val residence: String,
    val sex: String,
    val url: String
)
