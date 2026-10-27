package dev.leonp.strata.draggable.header.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * An empty spacer that is exactly as large as [insets]. Used as the collapsed header when no
 * collapsed header is given, so the header collapses down to the insets.
 */
@Composable
internal fun InsetSpacer(insets: WindowInsets, modifier: Modifier = Modifier) {
    val insetsPadding = insets.asPaddingValues()

    Spacer(modifier = modifier.padding(insetsPadding))
}