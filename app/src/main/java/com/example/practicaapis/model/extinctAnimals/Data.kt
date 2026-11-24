package com.example.practicaapis.model.extinctAnimals

data class Data(
    val binomialName: String,
    val commonName: String,
    val imageSrc: String,
    val lastRecord: String,
    val location: String,
    val shortDesc: String,
    val wikiLink: String
)