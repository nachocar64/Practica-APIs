package com.example.practicaapis.data

import com.example.practicaapis.data.mappers.toPony
import com.example.practicaapis.data.remote.RetrofitClient
import com.example.practicaapis.data.remote.mlp.Data
import com.example.practicaapis.data.remote.mlp.MlpApiService
import com.example.practicaapis.data.remote.mlp.PonyResponse
import com.example.practicaapis.model.Pony
import com.example.practicaapis.util.Resultado
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

internal data class PonyPage(val ponies: List<Pony>, val receivedCount: Int)

internal class MlpRepository(private val api: MlpApiService = RetrofitClient.mlpApiService) {
    suspend fun catalog(limit: Int, offset: Int): Resultado<PonyPage> =
        request { api.obtenerPersonajes(limit, offset).toPage() }

    suspend fun search(query: String): Resultado<PonyPage> =
        request(onNotFound = { PonyPage(emptyList(), 0) }) {
            // Pony API accepts "+" in character paths but rejects encoded spaces.
            api.obtenerPersonaje(query.replace(Regex("\\s+"), "+")).toPage()
        }

    suspend fun detail(id: Int): Resultado<Pony?> =
        request(onNotFound = { null }) {
            api.obtenerPersonaje(id.toString()).toPage().ponies.firstOrNull { it.id == id }
        }

    private suspend fun <T> request(
        onNotFound: (() -> T)? = null,
        load: suspend () -> T
    ): Resultado<T> = try {
        Resultado.Exito(load())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: HttpException) {
        if (error.code() == 404 && onNotFound != null) Resultado.Exito(onNotFound())
        else Resultado.Error("Pony API returned HTTP ${error.code()}.")
    } catch (_: Exception) {
        Resultado.Error("Could not read the Pony API response.")
    }
}

private fun PonyResponse.toPage(): PonyPage {
    check(status in 200..299)
    val rows: List<Data?>? = data
    val entries = checkNotNull(rows)
    val ponies = entries.mapNotNull { it?.toPony() }.distinctBy(Pony::id)
    check(entries.isEmpty() || ponies.isNotEmpty())
    return PonyPage(ponies, entries.size)
}
