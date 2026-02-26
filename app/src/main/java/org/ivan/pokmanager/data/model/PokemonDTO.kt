package org.ivan.pokmanager.data.model

data class PokemonDTO(
    val pokedexNumber: Int,
    val name: String,
    val types: String,
    val description: String,
    val heightM: Double,
    val weightKg: Double,
    val stats: PokemonStatsDTO,
    val imageUrl: String,
    val moves: List<String>
)
