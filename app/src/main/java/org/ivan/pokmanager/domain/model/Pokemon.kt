package org.ivan.pokmanager.domain.model

data class Pokemon(
    val pokedexNumber: Int,
    val name: String,
    val types: String,
    val description: String,
    val heightM: Double,
    val weightKg: Double,
    val stats: PokemonStats,
    val imageUrl: String,
    val moves: List<String>
)
