package com.example.practicaapis.data.remote.catFacts

import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {

    /** Obtiene razas de gatos con parámetro de límite opcional. */
    @GET("breeds")
    suspend fun getCatBreeds(
        @Query("limit") limit: Int? = null
    ): catFacts

    /** Obtiene un dato curioso aleatorio de gatos, opcionalmente limitando su longitud. */
    @GET("fact")
    suspend fun getRandomCatFact(
        @Query("max_length") maxLength: Int? = null
    ): CatFactDto

    /** Obtiene una lista de datos curiosos de gatos con límite y/or longitud máxima. */
    @GET("facts")
    suspend fun getCatFacts(
        @Query("limit") limit: Int? = null,
        @Query("max_length") maxLength: Int? = null
    ): CatFactsResponse
}
