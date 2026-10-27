package dev.leonp.strata.draggable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun InsetSpacer(insets: WindowInsets, modifier: Modifier = Modifier) {
    val insetsPadding = insets.asPaddingValues()

    Spacer(modifier = modifier.padding(insetsPadding))
}