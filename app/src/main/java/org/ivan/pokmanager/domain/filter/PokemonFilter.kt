package org.ivan.pokmanager.domain.filter

import org.ivan.pokmanager.domain.model.Pokemon
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.domain.type.normalize

/**
 * Filtra la coleccion del usuario por nombre (contiene, sin mayusculas ni tildes) y por tipo.
 */
object PokemonFilter {
    fun apply(pokemons: List<Pokemon>, query: String, type: PokemonType?): List<Pokemon> {
        val normalizedQuery = normalize(query)
        return pokemons.filter { pokemon ->
            (normalizedQuery.isEmpty() || normalize(pokemon.name).contains(normalizedQuery)) &&
                (type == null || type in PokemonType.parseList(pokemon.types))
        }
    }
}
