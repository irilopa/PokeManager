package org.ivan.pokmanager.filter

import org.ivan.pokmanager.domain.filter.PokemonFilter
import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.type.PokemonType
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonFilterTest {

    private val pikachu = Pokemon(id = "1", name = "Pikachu", types = "Eléctrico")
    private val bulbasaur = Pokemon(id = "2", name = "Bulbasaur", types = "Planta / Veneno")
    private val flabebe = Pokemon(id = "3", name = "Flabébé", types = "Hada")
    private val team = listOf(pikachu, bulbasaur, flabebe)

    @Test
    fun `sin filtros devuelve todo`() {
        assertEquals(team, PokemonFilter.apply(team, "", null))
    }

    @Test
    fun `busca por parte del nombre sin mayusculas ni tildes`() {
        assertEquals(listOf(pikachu), PokemonFilter.apply(team, "PIKA", null))
        assertEquals(listOf(flabebe), PokemonFilter.apply(team, "flabebe", null))
    }

    @Test
    fun `filtra por tipo, tambien el secundario`() {
        assertEquals(listOf(bulbasaur), PokemonFilter.apply(team, "", PokemonType.POISON))
    }

    @Test
    fun `combina nombre y tipo`() {
        assertEquals(emptyList<Pokemon>(), PokemonFilter.apply(team, "pika", PokemonType.GRASS))
        assertEquals(listOf(bulbasaur), PokemonFilter.apply(team, "bulb", PokemonType.GRASS))
    }
}
