package org.ivan.pokmanager.type

import org.ivan.pokmanager.domain.type.PokemonType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokemonTypeTest {

    @Test
    fun `parsea el texto guardado con uno o dos tipos`() {
        assertEquals(listOf(PokemonType.GRASS, PokemonType.POISON), PokemonType.parseList("Planta / Veneno"))
        assertEquals(listOf(PokemonType.ELECTRIC), PokemonType.parseList("Eléctrico"))
    }

    @Test
    fun `ignora mayusculas y tildes y acepta nombres en ingles`() {
        assertEquals(PokemonType.PSYCHIC, PokemonType.fromName("psiquico"))
        assertEquals(PokemonType.DRAGON, PokemonType.fromName("  DRAGÓN "))
        assertEquals(PokemonType.FIRE, PokemonType.fromName("fire"))
    }

    @Test
    fun `texto vacio o desconocido no produce tipos`() {
        assertEquals(emptyList<PokemonType>(), PokemonType.parseList(""))
        assertNull(PokemonType.fromName("Sonido"))
    }
}
