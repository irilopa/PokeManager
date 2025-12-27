package org.ivan.pokmanager.data.model.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ivan.pokmanager.data.model.Pokemon
import org.ivan.pokmanager.data.model.PokemonStats

object PokemonRepository {
    /**
         * 1. La "Tabla" de la base de datos en memoria.
         * Iniciamos con 5 Pokémon para que la lista no se vea vacía al abrir la app.
         */
    private val initialData = listOf(
        // 1. Squirtle (Agua)
        Pokemon(
            pokedexNumber = 7,
            name = "Squirtle",
            types = "Agua",
            description = "Tras nacer, su espalda se hincha y endurece como una concha. Echa espuma por la boca.",
            heightM = 0.5,
            weightKg = 9.0,
            stats = PokemonStats(44, 48, 65, 50, 64, 43),
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
            moves = listOf("Pistola Agua", "Cabezazo", "Refugio")
        ),
        // 2. Pikachu (Eléctrico)
        Pokemon(
            pokedexNumber = 25,
            name = "Pikachu",
            types = "Eléctrico",
            description = "Mantiene su cola en alto para vigilar. Si das un tirón a su cola, querrá morderte.",
            heightM = 0.4,
            weightKg = 6.0,
            stats = PokemonStats(35, 55, 40, 50, 50, 90),
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            moves = listOf("Impactrueno", "Onda Trueno", "Ataque Rápido")
        ),
        // 3. Jigglypuff (Normal / Hada)
        Pokemon(
            pokedexNumber = 39,
            name = "Jigglypuff",
            types = "Normal / Hada",
            description = "Si infla su cuerpo para cantar una nana, es capaz de cantar más tiempo sin detenerse a respirar.",
            heightM = 0.5,
            weightKg = 5.5,
            stats = PokemonStats(115, 45, 20, 45, 25, 20),
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/39.png",
            moves = listOf("Canto", "Destructor", "Desenrollar")
        ),
        // 4. Gengar (Fantasma / Veneno)
        Pokemon(
            pokedexNumber = 94,
            name = "Gengar",
            types = "Fantasma / Veneno",
            description = "Si sientes un frío repentino, es que hay un Gengar cerca. Tal vez intente echarte una maldición.",
            heightM = 1.5,
            weightKg = 40.5,
            stats = PokemonStats(60, 65, 60, 130, 75, 110),
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png",
            moves = listOf("Bola Sombra", "Hipnosis", "Come Sueños")
        ),
        // 5. Dragonite (Dragón / Volador)
        Pokemon(
            pokedexNumber = 149,
            name = "Dragonite",
            types = "Dragón / Volador",
            description = "Es un Pokémon de buen corazón que guía hasta tierra a los barcos que se encuentran perdidos en plena tormenta.",
            heightM = 2.2,
            weightKg = 210.0,
            stats = PokemonStats(91, 134, 95, 100, 100, 80),
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/149.png",
            moves = listOf("Enfado", "Huracán", "Puño Fuego", "Velocidad Extrema")
        )
    )


    private val _pokemons = MutableStateFlow<List<Pokemon>>(initialData)

    // Variable pública de solo lectura (para que la UI se suscriba)
    val pokemons = _pokemons.asStateFlow()

    /**
     * SIMULA UN 'INSERT INTO pokemons ...'
     */
    fun addPokemon(pokemon: Pokemon) {
        // En StateFlow, para actualizar la lista, necesitamos crear una NUEVA lista
        // (No vale con hacer .add sobre la vieja)
        val currentList = _pokemons.value.toMutableList()
        currentList.add(0, pokemon) // Añadimos al principio para verlo rápido
        _pokemons.value = currentList
    }

    /**
     * SIMULA UN 'DELETE FROM pokemons WHERE id = ...'
     */
    fun deletePokemon(pokedexNumber: Int) {
        val currentList = _pokemons.value.toMutableList()
        currentList.removeAll { it.pokedexNumber == pokedexNumber }
        _pokemons.value = currentList
    }
}