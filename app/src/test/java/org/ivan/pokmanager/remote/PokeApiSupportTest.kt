package org.ivan.pokmanager.remote

import org.ivan.pokmanager.data.model.remote.PokeApiErrors
import org.ivan.pokmanager.data.model.remote.toPokeApiName
import org.ivan.pokmanager.fakes.FakePokemonApiService
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PokeApiSupportTest {

    @Test
    fun `normaliza los nombres al formato de la PokeAPI`() {
        assertEquals("pikachu", toPokeApiName("  Pikachu "))
        assertEquals("mr-mime", toPokeApiName("Mr. Mime"))
        assertEquals("farfetchd", toPokeApiName("Farfetch'd"))
        assertEquals("nidoran-f", toPokeApiName("Nidoran♀"))
        assertEquals("nidoran-m", toPokeApiName("Nidoran ♂"))
        assertEquals("flabebe", toPokeApiName("Flabébé"))
        assertEquals("type-null", toPokeApiName("Type: Null"))
        assertEquals("tapu-koko", toPokeApiName("Tapu  Koko"))
    }

    @Test
    fun `distingue no encontrado, sin conexion y servidor caido`() {
        assertEquals(PokeApiErrors.NOT_FOUND, PokeApiErrors.messageFor(FakePokemonApiService.httpError(404)))
        assertEquals(PokeApiErrors.SERVER, PokeApiErrors.messageFor(FakePokemonApiService.httpError(503)))
        assertEquals(PokeApiErrors.NO_CONNECTION, PokeApiErrors.messageFor(UnknownHostException("pokeapi.co")))
        assertEquals(PokeApiErrors.NO_CONNECTION, PokeApiErrors.messageFor(SocketTimeoutException()))
        assertEquals(PokeApiErrors.GENERIC, PokeApiErrors.messageFor(FakePokemonApiService.httpError(429)))
    }
}
