package com.example.practicaapis.data.remote.mlp

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints de personajes de Pony API. Documentación: https://ponyapi.net/ */
interface MlpService {

    /** Lista paginada de personajes. */
    @GET("character/all")
    suspend fun obtenerPersonajes(
        @Query("limit") limite: Int,
        @Query("offset") desplazamiento: Int
    ): PonyResponse

    /** Busca personajes por nombre o ID. Para un ID, pasa su valor como texto. */
    @GET("character/{personaje}")
    suspend fun obtenerPersonaje(
        @Path("personaje") personaje: String
    ): PonyResponse
}
