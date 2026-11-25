package com.example.practicaapis.data.remote.extinctAnimals

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints de animales extintos. Documentación: https://cheba-apis.vercel.app/ */
interface ExtinctAnimalsApiService {

    /** Obtiene un animal al azar. Por defecto, la API devuelve uno con imagen. */
    @GET("animal/")
    suspend fun obtenerAnimalAleatorio(
        @Query("imageRequired") requiereImagen: Boolean? = null
    ): AnimalResponse

    /** Obtiene entre 1 y 804 animales al azar. */
    @GET("animal/{cantidad}")
    suspend fun obtenerAnimales(
        @Path("cantidad") cantidad: Int
    ): AnimalResponse
}
