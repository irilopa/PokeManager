package org.ivan.pokmanager.domain.type

import java.text.Normalizer

/**
 * Los 18 tipos Pokemon, con su nombre en ingles (PokeAPI) y en espanol (el que guarda la app).
 */
enum class PokemonType(val apiName: String, val spanishName: String) {
    NORMAL("normal", "Normal"),
    FIRE("fire", "Fuego"),
    WATER("water", "Agua"),
    ELECTRIC("electric", "Eléctrico"),
    GRASS("grass", "Planta"),
    ICE("ice", "Hielo"),
    FIGHTING("fighting", "Lucha"),
    POISON("poison", "Veneno"),
    GROUND("ground", "Tierra"),
    FLYING("flying", "Volador"),
    PSYCHIC("psychic", "Psíquico"),
    BUG("bug", "Bicho"),
    ROCK("rock", "Roca"),
    GHOST("ghost", "Fantasma"),
    DRAGON("dragon", "Dragón"),
    DARK("dark", "Siniestro"),
    STEEL("steel", "Acero"),
    FAIRY("fairy", "Hada");

    companion object {
        private val byKey: Map<String, PokemonType> = entries.flatMap {
            listOf(normalize(it.apiName) to it, normalize(it.spanishName) to it)
        }.toMap()

        /** Busca un tipo por su nombre en espanol o ingles, ignorando mayusculas y tildes. */
        fun fromName(name: String): PokemonType? = byKey[normalize(name)]

        /** Convierte el texto guardado en `Pokemon.types` («Planta / Veneno») en tipos. */
        fun parseList(types: String): List<PokemonType> =
            types.split("/").mapNotNull { fromName(it) }.distinct()
    }
}

/** Minusculas, sin tildes y sin espacios extremos, para comparar textos escritos por el usuario. */
fun normalize(text: String): String =
    Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
