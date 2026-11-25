package com.example.practicaapis.data.remote.jokes

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints de chistes de JokeAPI. Documentación: https://v2.jokeapi.dev/ */
interface JokesApiService {

    /** Obtiene un chiste filtrado por categoría y banderas de lista negra opcionales. */
    @GET("joke/{categoria}")
    suspend fun obtenerChiste(
        @Path("categoria") categoria: String = "Any",
        @Query("blacklistFlags") blacklistFlags: String? = null
    ): JokeResponse
}

