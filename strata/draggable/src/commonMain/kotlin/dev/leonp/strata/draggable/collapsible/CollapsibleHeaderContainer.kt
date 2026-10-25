package dev.leonp.strata.draggable.collapsible

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val horizontalInsets = insets.only(WindowInsetsSides.Horizontal)
    val verticalInsets = insets.only(WindowInsetsSides.Vertical)

    Layout(
        modifier = modifier
            .windowInsetsPadding(horizontalInsets)
            .consumeWindowInsets(verticalInsets)
            .nestedScroll(rememberCollapsibleHeaderContainerScrollConnection(state = state)),
        measurePolicy = rememberCollapsibleHeaderContainerMeasurePolicy(state = state),
        content = {
            val topInsets = insets.only(WindowInsetsSides.Top)
            val topPaddingValues = topInsets.asPaddingValues()

            if (collapsedHeader != null) {
                Box(modifier = Modifier.layoutId(CollapsibleHeaderLayoutId.CollapsedHeader)) {
                    collapsedHeader(topPaddingValues)
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .height(topPaddingValues.calculateTopPadding())
                        .layoutId(CollapsibleHeaderLayoutId.CollapsedHeader)
                )
            }

            Box(modifier = Modifier.layoutId(CollapsibleHeaderLayoutId.ExpandedHeader)) {
                expandedHeader(topPaddingValues)
            }

            Box(modifier = Modifier.layoutId(CollapsibleHeaderLayoutId.Header)) {
                if (collapsedHeader == null) {
                    expandedHeader(topPaddingValues)
                } else {
                    when (state.draggableState.targetValue) {
                        CollapsibleHeaderState.Collapsed -> collapsedHeader(topPaddingValues)
                        CollapsibleHeaderState.Expanded -> expandedHeader(topPaddingValues)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Vertical
                    )
                    .layoutId(CollapsibleHeaderLayoutId.Content)
            ) {
                val bottomInsets = insets.only(WindowInsetsSides.Bottom)

                content(bottomInsets.asPaddingValues())
            }
        }
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