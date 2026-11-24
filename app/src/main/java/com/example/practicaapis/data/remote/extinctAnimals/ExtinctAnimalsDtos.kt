package com.example.practicaapis.data.remote.extinctAnimals

data class AnimalResponse(
    val `data`: List<Data>,
    val status: String
)

data class Data(
    val binomialName: String,
    val commonName: String,
    val imageSrc: String,
    val lastRecord: String,
    val location: String,
    val shortDesc: String,
    val wikiLink: String
)
