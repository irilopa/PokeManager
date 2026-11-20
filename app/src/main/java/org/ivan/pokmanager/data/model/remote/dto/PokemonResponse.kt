package org.ivan.pokmanager.data.model.remote.dto

import org.ivan.pokmanager.domain.model.PokemonStats
import org.ivan.pokmanager.domain.type.PokemonType

/** Nombre del tipo devuelto por la PokeAPI. */
data class TypeNameDTO(val name: String)
/** Posicion del tipo en la lista de tipos. */
data class TypeSlotDTO(val slot: Int = 1, val type: TypeNameDTO)

/** Respuesta basica de Pokemon usada para autocompletar datos. */
data class PokemonResponse(
    val id: Int,
    val name: String,
    val stats: List<StatResponseDTO>,
    val types: List<TypeSlotDTO> = emptyList()
)
/** Convierte la lista de stats en el modelo de dominio. */
fun PokemonResponse.toPokemonStats(): PokemonStats {

    fun getStat(statName: String): Int =
        stats.firstOrNull { it.stat.name == statName }?.base_stat ?: 0

    return PokemonStats(
        hp             = getStat("hp"),
        attack         = getStat("attack"),
        defense        = getStat("defense"),
        specialAttack  = getStat("special-attack"),
        specialDefense = getStat("special-defense"),
        speed          = getStat("speed")
    )
}

/** Traduce el nombre del tipo de la PokeAPI al español. */
fun TypeNameDTO.toSpanish(): String =
    PokemonType.fromName(name)?.spanishName ?: name.replaceFirstChar { it.uppercase() }
