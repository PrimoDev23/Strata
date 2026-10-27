package dev.leonp.strata.draggable.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.InsetSpacer
import dev.leonp.strata.draggable.header.base.BaseCollapsibleHeaderContainer
import dev.leonp.strata.theme.StrataTheme

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
@Suppress("ContentSlotReused")
@Composable
fun CollapsibleHeaderContainer(
    expandedHeader: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderContainerState = rememberCollapsibleHeaderContainerState(),
    collapsedHeader: (@Composable (PaddingValues) -> Unit)? = null,
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    BaseCollapsibleHeaderContainer(
        modifier = modifier,
        header = { headerState ->
            val headerInsets = insets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            val headerPadding = headerInsets.asPaddingValues()

            if (isMeasuring) {
                when (headerState) {
                    CollapsibleHeaderState.Collapsed -> collapsedHeader?.invoke(headerPadding)
                        ?: InsetSpacer(insets = headerInsets)

                    CollapsibleHeaderState.Expanded -> expandedHeader(headerPadding)
                }
            } else {
                when (headerState) {
                    CollapsibleHeaderState.Collapsed -> collapsedHeader?.invoke(headerPadding)
                        ?: expandedHeader(headerPadding)

                    CollapsibleHeaderState.Expanded -> expandedHeader(headerPadding)
                }
            }
        },
        state = state,
        insets = insets,
        content = content
    )
}

@Preview
@Composable
private fun CollapsibleHeaderContainerPreview() {
    StrataTheme {
        CollapsibleHeaderContainer(
            expandedHeader = { padding ->
                Text(
                    text = "Expanded header",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding)
                        .padding(all = 24.dp)
                )
            },
            collapsedHeader = { padding ->
                Text(
                    text = "Collapsed header",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding)
                        .padding(all = 16.dp)
                )
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) { padding ->
            LazyColumn(contentPadding = padding) {
                items(count = 30) { index ->
                    Text(
                        text = "Item $index",
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
            }
        }
    }
}