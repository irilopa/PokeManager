package org.ivan.pokmanager.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Colores de la marca. En modo oscuro el rojo se aclara y lleva texto oscuro encima,
 * como recomienda Material 3, para mantener el contraste (WCAG AA).
 */
private val LightColorScheme = lightColorScheme(
    primary = PokeColors.PokeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = PokeColors.PokeBlue,
    tertiary = PokeColors.PokeYellow,
    error = Color(0xFFBA1A1A)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFBAC3FF),
    tertiary = PokeColors.PokeYellow,
    error = Color(0xFFFFB4AB)
)

@Composable
fun PokéManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
