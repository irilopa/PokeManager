package org.ivan.pokmanager.domain.model

/**
 * Estadisticas basicas de un Pokemon.
 */
data class PokemonStats(
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val specialAttack: Int = 0,
    val specialDefense: Int = 0,
    val speed: Int = 0
)
