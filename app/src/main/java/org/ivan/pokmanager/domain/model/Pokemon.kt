package org.ivan.pokmanager.domain.model

import com.google.firebase.firestore.DocumentId

data class Pokemon(
    @DocumentId val id: String = "",
    val pokedexNumber: Int,
    val name: String = "",
    val types: String = "",
    val description: String = "",
    val heightM: Double = 0.0,
    val weightKg: Double = 0.0,
    val stats: PokemonStats = PokemonStats(),
    val imageUrl: String = "",
    val moves: List<String> = emptyList()

)
