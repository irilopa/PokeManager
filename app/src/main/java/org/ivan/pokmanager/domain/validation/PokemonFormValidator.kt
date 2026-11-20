package org.ivan.pokmanager.domain.validation

/**
 * Campos del formulario de alta que pueden tener error.
 */
enum class PokemonField { NAME, POKEDEX_NUMBER, PRIMARY_TYPE, SECONDARY_TYPE, LEVEL, NOTES }

/**
 * Datos crudos del formulario, tal y como los escribe el usuario.
 */
data class PokemonForm(
    val name: String = "",
    val pokedexNumber: String = "",
    val primaryType: String = "",
    val secondaryType: String = "",
    val level: String = "",
    val notes: String = ""
)

/**
 * Reglas del formulario de alta de Pokemon.
 */
object PokemonFormValidator {
    const val MIN_LEVEL = 1
    const val MAX_LEVEL = 100
    const val MAX_NAME_LENGTH = 30
    const val MAX_NOTES_LENGTH = 200

    /** Devuelve los errores por campo. Un mapa vacio significa que el formulario es valido. */
    fun validate(form: PokemonForm): Map<PokemonField, String> {
        val errors = mutableMapOf<PokemonField, String>()

        val name = form.name.trim()
        when {
            name.isEmpty() -> errors[PokemonField.NAME] = "El nombre es obligatorio"
            name.length > MAX_NAME_LENGTH ->
                errors[PokemonField.NAME] = "Máximo $MAX_NAME_LENGTH caracteres"
        }

        if (form.pokedexNumber.isNotBlank()) {
            val number = form.pokedexNumber.trim().toIntOrNull()
            if (number == null || number <= 0) {
                errors[PokemonField.POKEDEX_NUMBER] = "Número de Pokédex no válido"
            }
        }

        if (form.primaryType.isBlank()) {
            errors[PokemonField.PRIMARY_TYPE] = "Elige el tipo principal"
        }

        if (form.secondaryType.isNotBlank() && form.secondaryType == form.primaryType) {
            errors[PokemonField.SECONDARY_TYPE] = "Debe ser distinto del tipo principal"
        }

        val level = form.level.trim().toIntOrNull()
        if (level == null || level !in MIN_LEVEL..MAX_LEVEL) {
            errors[PokemonField.LEVEL] = "El nivel debe estar entre $MIN_LEVEL y $MAX_LEVEL"
        }

        if (form.notes.length > MAX_NOTES_LENGTH) {
            errors[PokemonField.NOTES] = "Máximo $MAX_NOTES_LENGTH caracteres"
        }

        return errors
    }
}
