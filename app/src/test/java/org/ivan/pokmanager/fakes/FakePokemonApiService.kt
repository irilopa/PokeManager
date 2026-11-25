package org.ivan.pokmanager.fakes

import okhttp3.ResponseBody.Companion.toResponseBody
import org.ivan.pokmanager.data.model.remote.dto.PokemonResponse
import org.ivan.pokmanager.data.model.repository.PokemonApiService
import retrofit2.HttpException
import retrofit2.Response

/**
 * PokeAPI falsa: devuelve las respuestas registradas, un 404 real para los nombres
 * desconocidos o el fallo configurado en [failure].
 */
class FakePokemonApiService(
    private val responses: Map<String, PokemonResponse> = emptyMap()
) : PokemonApiService {
    var failure: Throwable? = null
    val queries = mutableListOf<String>()

    override suspend fun getPokemonByName(name: String): PokemonResponse {
        queries += name
        failure?.let { throw it }
        return responses[name] ?: throw httpError(404)
    }

    companion object {
        fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))
    }
}
