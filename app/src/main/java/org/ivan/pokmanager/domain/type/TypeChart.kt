package org.ivan.pokmanager.domain.type

import org.ivan.pokmanager.domain.type.PokemonType.*

/** Multiplicador de un tipo atacante contra un Pokemon defensor. */
data class TypeMatchup(val type: PokemonType, val multiplier: Double)

/** Debilidades, resistencias e inmunidades de un Pokemon. */
data class DefensiveProfile(
    val weaknesses: List<TypeMatchup>,
    val resistances: List<TypeMatchup>,
    val immunities: List<PokemonType>
)

/**
 * Tabla de efectividad de tipos (Gen 6 en adelante). Sin red ni estado.
 */
object TypeChart {

    /** Solo las combinaciones distintas de x1: atacante -> (defensor -> multiplicador). */
    private val chart: Map<PokemonType, Map<PokemonType, Double>> = mapOf(
        NORMAL to mapOf(ROCK to 0.5, GHOST to 0.0, STEEL to 0.5),
        FIRE to mapOf(FIRE to 0.5, WATER to 0.5, GRASS to 2.0, ICE to 2.0, BUG to 2.0, ROCK to 0.5, DRAGON to 0.5, STEEL to 2.0),
        WATER to mapOf(FIRE to 2.0, WATER to 0.5, GRASS to 0.5, GROUND to 2.0, ROCK to 2.0, DRAGON to 0.5),
        ELECTRIC to mapOf(WATER to 2.0, ELECTRIC to 0.5, GRASS to 0.5, GROUND to 0.0, FLYING to 2.0, DRAGON to 0.5),
        GRASS to mapOf(FIRE to 0.5, WATER to 2.0, GRASS to 0.5, POISON to 0.5, GROUND to 2.0, FLYING to 0.5, BUG to 0.5, ROCK to 2.0, DRAGON to 0.5, STEEL to 0.5),
        ICE to mapOf(FIRE to 0.5, WATER to 0.5, GRASS to 2.0, ICE to 0.5, GROUND to 2.0, FLYING to 2.0, DRAGON to 2.0, STEEL to 0.5),
        FIGHTING to mapOf(NORMAL to 2.0, ICE to 2.0, POISON to 0.5, FLYING to 0.5, PSYCHIC to 0.5, BUG to 0.5, ROCK to 2.0, GHOST to 0.0, DARK to 2.0, STEEL to 2.0, FAIRY to 0.5),
        POISON to mapOf(GRASS to 2.0, POISON to 0.5, GROUND to 0.5, ROCK to 0.5, GHOST to 0.5, STEEL to 0.0, FAIRY to 2.0),
        GROUND to mapOf(FIRE to 2.0, ELECTRIC to 2.0, GRASS to 0.5, POISON to 2.0, FLYING to 0.0, BUG to 0.5, ROCK to 2.0, STEEL to 2.0),
        FLYING to mapOf(ELECTRIC to 0.5, GRASS to 2.0, FIGHTING to 2.0, BUG to 2.0, ROCK to 0.5, STEEL to 0.5),
        PSYCHIC to mapOf(FIGHTING to 2.0, POISON to 2.0, PSYCHIC to 0.5, DARK to 0.0, STEEL to 0.5),
        BUG to mapOf(FIRE to 0.5, GRASS to 2.0, FIGHTING to 0.5, POISON to 0.5, FLYING to 0.5, PSYCHIC to 2.0, GHOST to 0.5, DARK to 2.0, STEEL to 0.5, FAIRY to 0.5),
        ROCK to mapOf(FIRE to 2.0, ICE to 2.0, FIGHTING to 0.5, GROUND to 0.5, FLYING to 2.0, BUG to 2.0, STEEL to 0.5),
        GHOST to mapOf(NORMAL to 0.0, PSYCHIC to 2.0, GHOST to 2.0, DARK to 0.5),
        DRAGON to mapOf(DRAGON to 2.0, STEEL to 0.5, FAIRY to 0.0),
        DARK to mapOf(FIGHTING to 0.5, PSYCHIC to 2.0, GHOST to 2.0, DARK to 0.5, FAIRY to 0.5),
        STEEL to mapOf(FIRE to 0.5, WATER to 0.5, ELECTRIC to 0.5, ICE to 2.0, ROCK to 2.0, STEEL to 0.5, FAIRY to 2.0),
        FAIRY to mapOf(FIRE to 0.5, FIGHTING to 2.0, POISON to 0.5, DRAGON to 2.0, DARK to 2.0, STEEL to 0.5)
    )

    /** Multiplicador de un ataque de tipo [attacker] contra un tipo defensor. */
    fun multiplier(attacker: PokemonType, defender: PokemonType): Double =
        chart[attacker]?.get(defender) ?: 1.0

    /** Multiplicador contra un Pokemon de uno o dos tipos (se multiplican). */
    fun effectiveness(attacker: PokemonType, defenders: List<PokemonType>): Double =
        defenders.fold(1.0) { acc, defender -> acc * multiplier(attacker, defender) }

    /** Perfil defensivo de un Pokemon, ordenado de mas a menos efectivo. */
    fun defensiveProfile(defenders: List<PokemonType>): DefensiveProfile {
        if (defenders.isEmpty()) return DefensiveProfile(emptyList(), emptyList(), emptyList())
        val matchups = PokemonType.entries.map { TypeMatchup(it, effectiveness(it, defenders)) }
        return DefensiveProfile(
            weaknesses = matchups.filter { it.multiplier > 1.0 }.sortedByDescending { it.multiplier },
            resistances = matchups.filter { it.multiplier > 0.0 && it.multiplier < 1.0 }.sortedBy { it.multiplier },
            immunities = matchups.filter { it.multiplier == 0.0 }.map { it.type }
        )
    }
}
