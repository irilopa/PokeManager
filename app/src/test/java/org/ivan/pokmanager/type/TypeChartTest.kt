package org.ivan.pokmanager.type

import org.ivan.pokmanager.domain.type.PokemonType.*
import org.ivan.pokmanager.domain.type.TypeChart
import org.ivan.pokmanager.domain.type.TypeMatchup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeChartTest {

    @Test
    fun `multiplicadores simples`() {
        assertEquals(2.0, TypeChart.multiplier(WATER, FIRE), 0.0)
        assertEquals(0.5, TypeChart.multiplier(FIRE, WATER), 0.0)
        assertEquals(1.0, TypeChart.multiplier(NORMAL, FIRE), 0.0)
        assertEquals(0.0, TypeChart.multiplier(NORMAL, GHOST), 0.0)
        assertEquals(0.0, TypeChart.multiplier(DRAGON, FAIRY), 0.0)
    }

    @Test
    fun `doble tipo multiplica ambos modificadores`() {
        // Hielo contra Dragon/Volador (Dragonite) es x4
        assertEquals(4.0, TypeChart.effectiveness(ICE, listOf(DRAGON, FLYING)), 0.0)
        // Planta contra Agua/Tierra (Swampert) es x4
        assertEquals(4.0, TypeChart.effectiveness(GRASS, listOf(WATER, GROUND)), 0.0)
        // Lucha contra Bicho/Volador es x1/4
        assertEquals(0.25, TypeChart.effectiveness(FIGHTING, listOf(BUG, FLYING)), 0.0)
        // Electrico contra Agua/Tierra es inmune
        assertEquals(0.0, TypeChart.effectiveness(ELECTRIC, listOf(WATER, GROUND)), 0.0)
    }

    @Test
    fun `perfil defensivo de Bulbasaur`() {
        val profile = TypeChart.defensiveProfile(listOf(GRASS, POISON))

        assertEquals(
            listOf(TypeMatchup(FIRE, 2.0), TypeMatchup(ICE, 2.0), TypeMatchup(FLYING, 2.0), TypeMatchup(PSYCHIC, 2.0)),
            profile.weaknesses
        )
        assertEquals(TypeMatchup(GRASS, 0.25), profile.resistances.first())
        assertTrue(profile.immunities.isEmpty())
    }

    @Test
    fun `perfil defensivo ordena x4 antes que x2 y detecta inmunidades`() {
        val profile = TypeChart.defensiveProfile(listOf(WATER, GROUND))

        assertEquals(TypeMatchup(GRASS, 4.0), profile.weaknesses.single())
        assertEquals(listOf(ELECTRIC), profile.immunities)
    }

    @Test
    fun `sin tipos el perfil esta vacio`() {
        val profile = TypeChart.defensiveProfile(emptyList())
        assertTrue(profile.weaknesses.isEmpty() && profile.resistances.isEmpty() && profile.immunities.isEmpty())
    }

    @Test
    fun `la tabla tiene 18 tipos atacantes con al menos una entrada`() {
        entries.forEach { attacker ->
            assertTrue(attacker.name, entries.any { TypeChart.multiplier(attacker, it) != 1.0 })
        }
    }
}
