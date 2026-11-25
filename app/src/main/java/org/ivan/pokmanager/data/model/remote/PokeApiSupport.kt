package org.ivan.pokmanager.data.model.remote

import org.ivan.pokmanager.domain.type.normalize
import retrofit2.HttpException
import java.io.IOException

/**
 * Convierte lo que escribe el usuario al nombre que espera la PokeAPI:
 * minusculas, sin tildes ni puntos y con guiones («Mr. Mime» -> «mr-mime»,
 * «Nidoran♀» -> «nidoran-f», «Farfetch'd» -> «farfetchd»).
 */
fun toPokeApiName(name: String): String =
    normalize(name.replace("♀", "-f").replace("♂", "-m"))
        .replace(Regex("[.'’:]"), "")
        .replace(Regex("[\\s_]+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')

/** Mensajes en español para los fallos al consultar la PokeAPI. */
object PokeApiErrors {
    const val NOT_FOUND = "Pokémon no encontrado. Revisa el nombre."
    const val NO_CONNECTION = "Sin conexión. Comprueba tu red e inténtalo de nuevo."
    const val SERVER = "La Pokédex no responde ahora mismo. Inténtalo más tarde."
    const val GENERIC = "No se pudo consultar la Pokédex. Inténtalo de nuevo."

    fun messageFor(error: Throwable): String = when {
        error is HttpException && error.code() == 404 -> NOT_FOUND
        error is HttpException && error.code() >= 500 -> SERVER
        error is IOException -> NO_CONNECTION // sin red, DNS o tiempo de espera agotado
        else -> GENERIC
    }
}
