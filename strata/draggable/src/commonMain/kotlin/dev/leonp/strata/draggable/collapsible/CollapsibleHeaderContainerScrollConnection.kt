package dev.leonp.strata.draggable.collapsible

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

internal class CollapsibleHeaderContainerScrollConnection(
    private val state: CollapsibleHeaderContainerState
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta >= 0) {
            return available
        }

        val dispatchedDelta = state.draggableState.dispatchRawDelta(delta)

        return Offset(
            x = 0f,
            y = dispatchedDelta
        )
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y

        if (delta <= 0) {
            return available
        }

        val dispatchedDelta = state.draggableState.dispatchRawDelta(delta)

        return Offset(
            x = 0f,
            y = dispatchedDelta
        )
    }
}

@Composable
internal fun rememberCollapsibleHeaderContainerScrollConnection(
    state: CollapsibleHeaderContainerState
) = remember { CollapsibleHeaderContainerScrollConnection(state = state) }