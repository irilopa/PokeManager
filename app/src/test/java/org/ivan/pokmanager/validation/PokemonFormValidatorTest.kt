package org.ivan.pokmanager.validation

import org.ivan.pokmanager.domain.validation.PokemonField
import org.ivan.pokmanager.domain.validation.PokemonForm
import org.ivan.pokmanager.domain.validation.PokemonFormValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonFormValidatorTest {

    private val valid = PokemonForm(name = "Pikachu", primaryType = "Eléctrico", level = "25")

    private fun errorsFor(form: PokemonForm) = PokemonFormValidator.validate(form)

    @Test
    fun `formulario correcto no tiene errores`() {
        assertTrue(errorsFor(valid).isEmpty())
    }

    @Test
    fun `numero de pokedex es opcional`() {
        assertTrue(errorsFor(valid.copy(pokedexNumber = "")).isEmpty())
    }

    @Test
    fun `nombre vacio o con espacios es error`() {
        assertEquals(setOf(PokemonField.NAME), errorsFor(valid.copy(name = "   ")).keys)
    }

    @Test
    fun `nivel fuera de 1-100 es error`() {
        listOf("", "0", "101", "999").forEach { level ->
            assertEquals("nivel=$level", setOf(PokemonField.LEVEL), errorsFor(valid.copy(level = level)).keys)
        }
        listOf("1", "100").forEach { level ->
            assertTrue("nivel=$level", errorsFor(valid.copy(level = level)).isEmpty())
        }
    }

    @Test
    fun `tipo secundario igual al principal es error`() {
        val errors = errorsFor(valid.copy(secondaryType = "Eléctrico"))
        assertEquals(setOf(PokemonField.SECONDARY_TYPE), errors.keys)
    }

    @Test
    fun `tipo principal es obligatorio`() {
        assertEquals(setOf(PokemonField.PRIMARY_TYPE), errorsFor(valid.copy(primaryType = "")).keys)
    }

    @Test
    fun `notas demasiado largas es error`() {
        val max = PokemonFormValidator.MAX_NOTES_LENGTH
        assertTrue(errorsFor(valid.copy(notes = "a".repeat(max))).isEmpty())
        assertEquals(setOf(PokemonField.NOTES), errorsFor(valid.copy(notes = "a".repeat(max + 1))).keys)
    }

    @Test
    fun `numero de pokedex cero es error`() {
        assertEquals(setOf(PokemonField.POKEDEX_NUMBER), errorsFor(valid.copy(pokedexNumber = "0")).keys)
    }
}
