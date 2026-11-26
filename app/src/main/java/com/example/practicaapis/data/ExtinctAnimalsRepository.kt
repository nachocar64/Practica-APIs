package com.example.practicaapis.data

import com.example.practicaapis.data.mappers.toExtinctAnimal
import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.extinctAnimals.ExtinctAnimalsApiService
import com.example.practicaapis.model.ExtinctAnimal
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

class ExtinctAnimalsRepository(
    private val apiService: ExtinctAnimalsApiService = RetrofitClient.extinctAnimalsApiService
) {
    suspend fun discover(): Resultado<ExtinctAnimal?> = try {
        val response = apiService.obtenerAnimalAleatorio(requiereImagen = false)
        val records = requireNotNull(response.data)

        when {
            response.status != "success" ->
                Resultado.Error("The Extinct Animals API returned an unsuccessful response.")
            records.isEmpty() -> Resultado.Exito(null)
            else -> records.firstOrNull()?.toExtinctAnimal()?.let { Resultado.Exito(it) }
                ?: Resultado.Error("The response is missing the animal's scientific name.")
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: HttpException) {
        Resultado.Error("The Extinct Animals API returned HTTP ${error.code()}.")
    } catch (_: IOException) {
        Resultado.Error("Could not reach the Extinct Animals API or read its response.")
    } catch (_: Exception) {
        Resultado.Error("The Extinct Animals API returned an invalid response.")
    }
}
