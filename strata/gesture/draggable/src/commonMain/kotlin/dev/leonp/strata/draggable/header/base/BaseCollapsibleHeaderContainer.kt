package dev.leonp.strata.draggable.header.base

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.clearAndSetSemantics
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.header.common.CollapsibleHeaderContainerState
import dev.leonp.strata.draggable.header.common.rememberCollapsibleHeaderContainerState

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
/**
 * The shared layout of the collapsible header containers.
 *
 * Composes [header] three times at once:
 * - Two hidden copies in [BaseCollapsibleHeaderScope.MeasurementScope], one for
 *   [DraggableLayoutState.Collapsed] and one for [DraggableLayoutState.Expanded]. They are measured
 *   to determine the anchors, but never placed. Their semantics are cleared, so they do not appear
 *   in the semantics tree.
 * - The visible header in [BaseCollapsibleHeaderScope.PlacementScope] for the current state. It is
 *   measured with the current header height and clipped to it.
 *
 * [content] is placed below the header with the remaining height. The container consumes [insets]
 * and passes the bottom and horizontal insets to [content]. Passing the top and horizontal insets
 * to the header slots is up to the calling container.
 *
 * @param header the header for the given state, composed three times as described above.
 * @param modifier the [Modifier] applied to the container.
 * @param state the state holding the header height and the settled state.
 * @param insets the window insets consumed by the container.
 * @param content the content below the header.
 */
@Suppress("ContentSlotReused")
@Composable
internal fun BaseCollapsibleHeaderContainer(
    header: @Composable BaseCollapsibleHeaderScope.(DraggableLayoutState) -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderContainerState = rememberCollapsibleHeaderContainerState(),
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit,
) {
    Layout(
        modifier = modifier
            .consumeWindowInsets(insets)
            .nestedScroll(rememberCollapsibleHeaderContainerScrollConnection(state = state)),
        measurePolicy = rememberCollapsibleHeaderContainerMeasurePolicy(state = state),
        content = {
            Box(
                modifier = Modifier
                    .layoutId(CollapsibleHeaderLayoutId.CollapsedHeader)
                    .clearAndSetSemantics {},
            ) {
                BaseCollapsibleHeaderScope.MeasurementScope.header(DraggableLayoutState.Collapsed)
            }

            Box(
                modifier = Modifier
                    .layoutId(CollapsibleHeaderLayoutId.ExpandedHeader)
                    .clearAndSetSemantics {},
            ) {
                BaseCollapsibleHeaderScope.MeasurementScope.header(DraggableLayoutState.Expanded)
            }

            Box(
                modifier = Modifier
                    .layoutId(CollapsibleHeaderLayoutId.Header)
                    .clipToBounds(),
                propagateMinConstraints = true,
            ) {
                BaseCollapsibleHeaderScope.PlacementScope.header(state.draggableState.currentValue)
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Vertical,
                    )
                    .layoutId(CollapsibleHeaderLayoutId.Content),
            ) {
                val contentInsets = insets.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                )

                content(contentInsets.asPaddingValues())
            }
        },
    )
}