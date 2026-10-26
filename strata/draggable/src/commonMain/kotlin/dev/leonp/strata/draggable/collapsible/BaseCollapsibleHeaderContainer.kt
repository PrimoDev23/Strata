package dev.leonp.strata.draggable.collapsible

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

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
@Suppress("ContentSlotReused")
@Composable
internal fun BaseCollapsibleHeaderContainer(
    header: @Composable BaseCollapsibleHeaderScope.(CollapsibleHeaderState) -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderContainerState = rememberCollapsibleHeaderContainerState(),
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    Layout(
        modifier = modifier
            .consumeWindowInsets(insets)
            .nestedScroll(rememberCollapsibleHeaderContainerScrollConnection(state = state)),
        measurePolicy = rememberCollapsibleHeaderContainerMeasurePolicy(state = state),
        content = {
            Box(modifier = Modifier.layoutId(CollapsibleHeaderLayoutId.CollapsedHeader)) {
                BaseCollapsibleHeaderScope.MeasurementScope.header(CollapsibleHeaderState.Collapsed)
            }

            Box(modifier = Modifier.layoutId(CollapsibleHeaderLayoutId.ExpandedHeader)) {
                BaseCollapsibleHeaderScope.MeasurementScope.header(CollapsibleHeaderState.Expanded)
            }

            Box(
                modifier = Modifier
                    .layoutId(CollapsibleHeaderLayoutId.Header)
                    .clipToBounds(),
                propagateMinConstraints = true
            ) {
                BaseCollapsibleHeaderScope.PlacementScope.header(state.draggableState.currentValue)
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Vertical
                    )
                    .layoutId(CollapsibleHeaderLayoutId.Content)
            ) {
                val contentInsets = insets.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                )

                content(contentInsets.asPaddingValues())
            }
        }
    )
}