package com.mr3y.poodle.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mr3y.poodle.ui.preview_utils.MultiThemePreview
import com.mr3y.poodle.ui.theme.PoodleTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun TextChip(text: String, modifier: Modifier = Modifier) {
    Chip(
        onClick = { },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colors.surface),
            contentColor = MaterialTheme.colors.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@MultiThemePreview
@Composable
fun TextChipPreview() {
    PoodleTheme {
        TextChip(text = "Example", Modifier.size(width = 88.dp, height = 48.dp))
    }
}
