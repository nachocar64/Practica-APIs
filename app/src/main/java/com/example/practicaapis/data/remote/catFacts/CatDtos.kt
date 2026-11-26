package com.example.practicaapis.data.remote.catFacts

data class catFacts(
    val current_page: Int,
    val `data`: List<Data>,
    val first_page_url: String? = null,
    val from: Int? = null,
    val last_page: Int? = null,
    val last_page_url: String? = null,
    val links: List<Link>? = null,
    val next_page_url: String? = null,
    val path: String? = null,
    val per_page: Any? = null,
    val prev_page_url: Any? = null,
    val to: Int? = null,
    val total: Int? = null
)

data class Data(
    val breed: String,
    val coat: String,
    val country: String,
    val origin: String,
    val pattern: String
)

data class Link(
    val active: Boolean,
    val label: String,
    val page: Int? = null,
    val url: String? = null
)

/** Representa un dato curioso individual sobre gatos */
data class CatFactDto(
    val fact: String,
    val length: Int
)

/** Respuesta paginada de datos curiosos sobre gatos */
data class CatFactsResponse(
    val current_page: Int,
    val `data`: List<CatFactDto>,
    val first_page_url: String? = null,
    val from: Int? = null,
    val last_page: Int? = null,
    val last_page_url: String? = null,
    val links: List<Link>? = null,
    val next_page_url: String? = null,
    val path: String? = null,
    val per_page: Any? = null,
    val prev_page_url: Any? = null,
    val to: Int? = null,
    val total: Int? = null
)
