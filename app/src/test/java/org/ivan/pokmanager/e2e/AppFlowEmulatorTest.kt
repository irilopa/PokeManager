package org.ivan.pokmanager.e2e

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.firebase.auth.FirebaseAuth
import org.ivan.pokmanager.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.json.JSONObject
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL

/**
 * Recorre la app completa (Koin + SDK real de Firebase) contra Firebase Emulator Suite.
 * Se omite si los emuladores no estan arrancados. Ejecutar con:
 *
 *   firebase emulators:exec --only auth,firestore --project pokemanager-6113d \
 *     "./gradlew testDebugUnitTest --tests '*AppFlowEmulatorTest*'"
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppFlowEmulatorTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun requireEmulators() {
            assumeTrue("Firebase Emulator Suite no está arrancado", FirebaseEmulators.areRunning())
        }
    }

    @get:Rule
    val compose: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>().also { FirebaseEmulators.configure() }

    @After
    fun tearDown() = FirebaseEmulators.reset()

    private val shotsDir = File(System.getProperty("user.dir"), "build/e2e-screenshots").apply { mkdirs() }

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { activity ->
            val view = activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(shotsDir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    private fun SemanticsNodeInteractionsProvider.waitForText(text: String, timeoutMs: Long = 15_000) {
        compose.waitUntil(timeoutMs) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun type(label: String, value: String) {
        compose.onNodeWithText(label).performScrollTo().performTextInput(value)
    }

    private fun addPokemon(name: String, level: String, notes: String = "") {
        type("Nombre del Pokémon", name)
        compose.onNodeWithText("Tipo Principal").performScrollTo().performClick()
        compose.onNodeWithText("Eléctrico").performClick()
        type("Nivel", level)
        if (notes.isNotEmpty()) type("Notas adicionales", notes)
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
    }

    private fun hasNode(text: String, substring: Boolean = false) =
        compose.onAllNodes(hasText(text, substring = substring)).fetchSemanticsNodes().isNotEmpty()

    private fun logout() {
        compose.onNodeWithContentDescription("Más opciones").performClick()
        compose.onNodeWithText("Cerrar sesión").performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForText("¡Bienvenido de nuevo!")
    }

    @Test
    fun registro_login_equipo_detalle_busqueda_y_logout() {
        val email = "ash${System.currentTimeMillis()}@test.com"
        val credential = FirebaseEmulators.randomCredential()
        val wrongCredential = FirebaseEmulators.randomCredential()

        // Registro: la casilla de privacidad es obligatoria
        compose.waitForText("Iniciar Sesión")
        compose.onNodeWithText("Regístrate").performClick()
        compose.waitForText("Registrarse")
        type("Nombre", "Ash")
        type("Email", email)
        type("Contraseña", credential)
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()
        compose.waitForText("Debes aceptar la Política de Privacidad")
        compose.onNodeWithContentDescription("Acepto la Política de Privacidad").performScrollTo().performClick()
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()

        // Tras registrarse se entra directamente al equipo vacio (HU-04)
        compose.waitForText("Añadir mi primer Pokémon")
        shot("01_lista_vacia")

        // Login con contraseña incorrecta: mensaje en español
        logout()
        type("Email", email)
        type("Contraseña", wrongCredential)
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.waitForText("Email o contraseña incorrectos")
        shot("02_login_error")

        // Login correcto
        compose.onNodeWithText("Contraseña").performTextClearance()
        type("Contraseña", credential)
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.waitForText("Añadir mi primer Pokémon")

        // HU-05: errores de validacion
        compose.onNodeWithText("Añadir mi primer Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        compose.onNodeWithText("Guardar Pokémon").performScrollTo().performClick()
        compose.waitForText("El nombre es obligatorio")
        compose.onNodeWithText("Nombre del Pokémon").performScrollTo()
        shot("03_formulario_errores")

        // HU-06: guardado confirmado -> vuelve a la lista
        addPokemon("Pikachu", "5", notes = "Mi primer Pokémon")
        compose.waitForText("Nv. 5 · Eléctrico")

        compose.onNodeWithContentDescription("Añadir Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        addPokemon("Pikachu", "50")
        compose.waitForText("Nv. 50 · Eléctrico")
        shot("04_lista_con_buscador")

        // IRI-19: busqueda sin resultados y filtro por tipo
        compose.onNodeWithText("Buscar en tu equipo").performTextInput("zzz")
        compose.waitForText("No se encontraron Pokémon con esos filtros.")
        shot("05_busqueda_sin_resultados")
        compose.onNodeWithText("Limpiar filtros").performClick()
        compose.waitForText("Nv. 50 · Eléctrico")
        compose.onNodeWithText("Agua").performClick()
        compose.waitForText("No se encontraron Pokémon con esos filtros.")
        compose.onNodeWithContentDescription("Limpiar filtros").performClick()
        compose.waitForText("Nv. 5 · Eléctrico")

        // HU-07 + IRI-18 + IRI-22: cada Pikachu abre su propio detalle con tipos, stats y debilidades
        compose.onNodeWithText("Nv. 50 · Eléctrico").performClick()
        compose.waitForText("Nivel 50")
        assertTrue(hasNode("Tierra ×2"))
        assertTrue(hasNode("Volador ×½"))
        compose.waitForIdle()
        shot("06_detalle_pikachu_nv50")
        assertTrue(!hasNode("Nivel 5"))
        compose.onNodeWithContentDescription("Volver").performClick()

        compose.waitForText("Nv. 5 · Eléctrico")
        compose.onNodeWithText("Nv. 5 · Eléctrico").performClick()
        compose.waitForText("Nivel 5")
        assertTrue(hasNode("Mi primer Pokémon"))
        assertTrue(!hasNode("Nivel 50"))
        compose.onNodeWithText("Debilidades y resistencias").performScrollTo()
        shot("07_detalle_pikachu_nv5_debilidades")
        compose.onNodeWithContentDescription("Volver").performClick()

        // HU-02: logout real y la pila queda vacia
        compose.waitForText("Mi Equipo Pokémon")
        logout()
        shot("08_login_tras_logout")
        assertNull(FirebaseAuth.getInstance().currentUser)

        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.activityRule.scenario.onActivity { assertTrue("Atrás debe cerrar la app", it.isFinishing) }
    }

    private fun register(email: String, credential: String) {
        compose.waitForText("Iniciar Sesión")
        compose.onNodeWithText("Regístrate").performClick()
        compose.waitForText("Registrarse")
        type("Nombre", "Misty")
        type("Email", email)
        type("Contraseña", credential)
        compose.onNodeWithContentDescription("Acepto la Política de Privacidad").performScrollTo().performClick()
        compose.onNodeWithText("Registrarse").performScrollTo().performClick()
        compose.waitForText("Añadir mi primer Pokémon")
    }

    /** Numero de documentos en una coleccion del emulador de Firestore (con acceso de administrador). */
    private fun countDocuments(path: String): Int {
        val url = URL("http://127.0.0.1:${FirebaseEmulators.FIRESTORE_PORT}/v1/projects/" +
            "${FirebaseEmulators.PROJECT}/databases/(default)/documents/$path")
        val conn = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer owner")
        if (conn.responseCode == 404) return 0
        val body = JSONObject(conn.inputStream.bufferedReader().readText())
        return body.optJSONArray("documents")?.length() ?: 0
    }

    private fun documentExists(path: String): Boolean {
        val url = URL("http://127.0.0.1:${FirebaseEmulators.FIRESTORE_PORT}/v1/projects/" +
            "${FirebaseEmulators.PROJECT}/databases/(default)/documents/$path")
        val conn = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer owner")
        return conn.responseCode == 200
    }

    @Test
    fun eliminar_cuenta_borra_la_coleccion_el_perfil_y_la_cuenta() {
        val email = "misty${System.currentTimeMillis()}@test.com"
        val credential = FirebaseEmulators.randomCredential()
        register(email, credential)

        compose.onNodeWithText("Añadir mi primer Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        addPokemon("Staryu", "20")
        compose.waitForText("Nv. 20 · Eléctrico")

        val uid = requireNotNull(FirebaseAuth.getInstance().currentUser).uid
        assertEquals(1, countDocuments("users/$uid/pokemon"))
        assertTrue(documentExists("users/$uid"))

        // Contrasena incorrecta: no se borra nada
        compose.onNodeWithContentDescription("Más opciones").performClick()
        // En Robolectric el menu desplegable puede colocarse fuera de la pantalla: se pulsa
        // la opcion con su accion de accesibilidad en lugar de por coordenadas.
        compose.onNodeWithText("Eliminar cuenta").performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForText("Esta acción no se puede deshacer")
        shot("09_eliminar_cuenta")
        compose.onNodeWithText("Contraseña").performTextInput(FirebaseEmulators.randomCredential())
        compose.onNodeWithText("Eliminar definitivamente").performClick()
        compose.waitForText("La contraseña no es correcta.")
        assertEquals(1, countDocuments("users/$uid/pokemon"))
        shot("10_eliminar_cuenta_error")

        // Contrasena correcta: se borra todo y vuelve al login
        compose.onNodeWithText("Contraseña").performTextClearance()
        compose.onNodeWithText("Contraseña").performTextInput(credential)
        compose.onNodeWithText("Eliminar definitivamente").performClick()
        compose.waitForText("¡Bienvenido de nuevo!")

        assertNull(FirebaseAuth.getInstance().currentUser)
        assertEquals(0, countDocuments("users/$uid/pokemon"))
        assertFalse(documentExists("users/$uid"))

        // La cuenta ya no existe
        type("Email", email)
        type("Contraseña", credential)
        compose.onNodeWithText("Iniciar Sesión").performClick()
        compose.waitForText("Email o contraseña incorrectos")
    }

    /** Correos de restablecimiento que ha generado el emulador de Auth para un email. */
    private fun passwordResetCodes(email: String): Int {
        val url = URL("http://127.0.0.1:${FirebaseEmulators.AUTH_PORT}/emulator/v1/projects/" +
            "${FirebaseEmulators.PROJECT}/oobCodes")
        val conn = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
        val codes = JSONObject(conn.inputStream.bufferedReader().readText()).optJSONArray("oobCodes") ?: return 0
        return (0 until codes.length()).map { codes.getJSONObject(it) }
            .count { it.optString("email") == email && it.optString("requestType") == "PASSWORD_RESET" }
    }

    @Test
    fun recuperar_contrasena_envia_el_correo_sin_revelar_si_la_cuenta_existe() {
        val email = "brock${System.currentTimeMillis()}@test.com"
        register(email, FirebaseEmulators.randomCredential())
        logout()

        compose.onNodeWithText("¿Has olvidado tu contraseña?").performClick()
        compose.waitForText("Enviar enlace")
        type("Email", email)
        shot("11_recuperar_contrasena")
        compose.onNodeWithText("Enviar enlace").performClick()
        compose.waitForText("Volver a iniciar sesión")
        shot("12_recuperar_contrasena_enviado")
        assertEquals(1, passwordResetCodes(email))

        // Con un email que no existe se muestra el mismo mensaje
        compose.onNodeWithText("Volver a iniciar sesión").performClick()
        compose.waitForText("¡Bienvenido de nuevo!")
        compose.onNodeWithText("¿Has olvidado tu contraseña?").performClick()
        compose.waitForText("Enviar enlace")
        type("Email", "nadie${System.currentTimeMillis()}@test.com")
        compose.onNodeWithText("Enviar enlace").performClick()
        compose.waitForText("Volver a iniciar sesión")
    }

    @Test
    fun editar_un_pokemon_actualiza_el_mismo_documento() {
        val email = "gary${System.currentTimeMillis()}@test.com"
        register(email, FirebaseEmulators.randomCredential())
        compose.onNodeWithText("Añadir mi primer Pokémon").performClick()
        compose.waitForText("Guardar Pokémon")
        addPokemon("Pikachu", "5", notes = "Recién capturado")
        compose.waitForText("Nv. 5 · Eléctrico")
        val uid = requireNotNull(FirebaseAuth.getInstance().currentUser).uid

        compose.onNodeWithText("Nv. 5 · Eléctrico").performClick()
        compose.waitForText("Nivel 5")
        compose.onNodeWithContentDescription("Editar").performClick()
        compose.waitForText("Guardar cambios")
        compose.onNodeWithText("Pikachu").assertExists()
        shot("13_editar_pokemon")

        compose.onNodeWithText("Nivel").performScrollTo().performTextClearance()
        type("Nivel", "12")
        compose.onNodeWithText("Notas adicionales").performScrollTo().performTextClearance()
        type("Notas adicionales", "Ya ha subido de nivel")
        compose.onNodeWithText("Guardar cambios").performScrollTo().performClick()

        // Vuelve al detalle, que muestra los datos nuevos
        compose.waitForText("Nivel 12")
        assertTrue(hasNode("Ya ha subido de nivel"))
        assertEquals(1, countDocuments("users/$uid/pokemon"))
        compose.onNodeWithContentDescription("Volver").performClick()
        compose.waitForText("Nv. 12 · Eléctrico")
    }
}
