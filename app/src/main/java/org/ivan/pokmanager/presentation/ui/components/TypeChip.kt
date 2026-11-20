package org.ivan.pokmanager.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivan.pokmanager.domain.type.PokemonType
import org.ivan.pokmanager.presentation.ui.theme.TypeColors

/** Etiqueta con el color del tipo. [suffix] permite añadir p. ej. «×2». */
@Composable
fun TypeChip(type: PokemonType, modifier: Modifier = Modifier, suffix: String? = null) {
    Text(
        text = if (suffix != null) "${type.spanishName} $suffix" else type.spanishName,
        color = TypeColors.content(type),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(TypeColors.background(type))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
