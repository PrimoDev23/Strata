package dev.leonp.strata.draggable.header.base

import androidx.compose.foundation.gestures.animateToWithDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import dev.leonp.strata.draggable.header.CollapsibleHeaderContainerState

/**
 * Connects scrollable content to the header height.
 *
 * - Scrolling towards the end of the content collapses the header before the content scrolls.
 * - Scrolling towards the start expands the header with whatever the content did not consume,
 *   so the header only expands once the content reached its start.
 * - After a fling, the header settles at its closest state.
 */
internal class CollapsibleHeaderContainerScrollConnection(
    private val state: CollapsibleHeaderContainerState
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta >= 0) {
            return Offset.Zero
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
            return Offset.Zero
        }

        val dispatchedDelta = state.draggableState.dispatchRawDelta(delta)

        return Offset(
            x = 0f,
            y = dispatchedDelta
        )
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val used = state.draggableState.animateToWithDecay(
            targetValue = state.draggableState.targetValue,
            velocity = available.y
        )

        return Velocity(
            x = 0f,
            y = used
        )
    }
}

@Composable
internal fun rememberCollapsibleHeaderContainerScrollConnection(
    state: CollapsibleHeaderContainerState
) = remember(state) { CollapsibleHeaderContainerScrollConnection(state = state) }