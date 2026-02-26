package org.ivan.pokmanager.data.model.remote.dto

import org.ivan.pokmanager.domain.model.PokemonStats

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
@Suppress("unused")
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

/** Mapa de nombre de tipo en ingles a espanol. */
private val typeTranslations = mapOf(
    "normal" to "Normal", "fire" to "Fuego", "water" to "Agua",
    "grass" to "Planta", "electric" to "Eléctrico", "ice" to "Hielo",
    "fighting" to "Lucha", "poison" to "Veneno", "ground" to "Tierra",
    "flying" to "Volador", "psychic" to "Psíquico", "bug" to "Bicho",
    "rock" to "Roca", "ghost" to "Fantasma", "dragon" to "Dragón",
    "dark" to "Siniestro", "steel" to "Acero", "fairy" to "Hada"
)

/** Traduce el nombre del tipo al español cuando existe un mapeo. */
fun TypeNameDTO.toSpanish(): String = typeTranslations[name] ?: name.replaceFirstChar { it.uppercase() }
