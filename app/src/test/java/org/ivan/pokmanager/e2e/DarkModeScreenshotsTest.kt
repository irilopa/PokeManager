package org.ivan.pokmanager.e2e

import org.robolectric.annotation.Config

/**
 * Las mismas capturas que el README, con el sistema en modo oscuro.
 * Quedan en app/build/readme-screenshots-dark/.
 */
@Config(sdk = [35], qualifiers = "w411dp-h891dp-night-xxhdpi")
class DarkModeScreenshotsTest : ReadmeScreenshotsTest() {
    override val shotsFolder = "readme-screenshots-dark"
    override val demoEmail = "ash.dark@pokemanager.dev"
}
