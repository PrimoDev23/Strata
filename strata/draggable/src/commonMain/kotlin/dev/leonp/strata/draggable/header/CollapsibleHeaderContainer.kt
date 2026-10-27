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
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.header.base.BaseCollapsibleHeaderContainer
import dev.leonp.strata.theme.StrataTheme

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
/**
 * A container with a header above [content] that collapses and expands with the user's scroll
 * and drag gestures.
 *
 * The header height follows the drag position continuously between the collapsed and the expanded
 * height. [expandedHeader] is shown while the header is closer to the expanded height and
 * [collapsedHeader] while it is closer to the collapsed height. The visible header is laid out with
 * the current height, so it is stretched or squeezed between the two heights. For an animated
 * transition between both headers, use [AnimatableCollapsibleHeaderContainer].
 *
 * Gestures:
 * - Scrolling a scrollable [content] towards its end collapses the header before the content
 *   scrolls. Scrolling back towards the start expands it once the content reached its start.
 * - Non-scrollable [content] can be dragged directly to collapse or expand the header.
 * - When a gesture ends between both heights, the header settles at one of them, based on the
 *   position and the velocity of the gesture.
 *
 * ## Header measurement
 *
 * The collapsed and expanded heights are measured from the header slots themselves. To know both
 * heights at any time, **each header slot is composed more than once at the same time**: one hidden
 * copy per state is composed and measured but never placed, and another copy is the visible
 * header. This has a few consequences for the header slots:
 * - Side effects run once per copy. Do not use composition or effects such as `LaunchedEffect`
 *   for impression tracking or analytics. Use visibility-based APIs or the settled state instead.
 * - Each copy has its own `remember` and `rememberSaveable` state. The size of a header must only
 *   depend on its parameters, otherwise the measured heights do not match the visible header.
 *   Hoist any state that changes the header size.
 * - Expensive work such as loading images is done once per copy.
 * - The hidden copies are excluded from the semantics tree, so tests and accessibility services
 *   only see the visible header.
 *
 * ## Insets
 *
 * The container consumes [insets] and hands them to its slots as [PaddingValues]:
 * - the header slots receive the top and horizontal insets,
 * - [content] receives the bottom and horizontal insets.
 *
 * Apply the given [PaddingValues] in the slots, for example as padding inside the header
 * background or as `contentPadding` of a lazy list. Because the insets are consumed, inset
 * modifiers such as `Modifier.windowInsetsPadding` inside the slots do not apply them again.
 *
 * @param expandedHeader the header shown while the container is expanded. Receives the top and
 *   horizontal [insets] as [PaddingValues].
 * @param modifier the [Modifier] applied to the container.
 * @param state the [CollapsibleHeaderContainerState] that holds the collapsed or expanded state.
 * @param collapsedHeader the header shown while the container is collapsed. Receives the top and
 *   horizontal [insets] as [PaddingValues]. If `null`, the header collapses down to the top
 *   [insets] and [expandedHeader] stays visible while collapsing.
 * @param insets the window insets handled by the container. They are consumed for all descendants.
 * @param content the content below the header. It is measured with the remaining height and
 *   receives the bottom and horizontal [insets] as [PaddingValues].
 */
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
                    DraggableLayoutState.Collapsed -> collapsedHeader?.invoke(headerPadding)
                        ?: InsetSpacer(insets = headerInsets)

                    DraggableLayoutState.Expanded -> expandedHeader(headerPadding)
                }
            } else {
                when (headerState) {
                    DraggableLayoutState.Collapsed -> collapsedHeader?.invoke(headerPadding)
                        ?: expandedHeader(headerPadding)

                    DraggableLayoutState.Expanded -> expandedHeader(headerPadding)
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