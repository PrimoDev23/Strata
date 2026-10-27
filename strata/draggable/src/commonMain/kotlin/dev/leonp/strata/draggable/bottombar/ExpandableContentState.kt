package dev.leonp.strata.draggable.bottombar

import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import dev.leonp.strata.draggable.common.DraggableLayoutState

/**
 * The state of [ExpandableContent].
 *
 * Holds the current height of the layout, driven by drag gestures, and the transition between the
 * collapsed and the expanded content. Only the settled [DraggableLayoutState] is saved, so a layout
 * that was restored while a drag was in progress starts at its last settled state.
 *
 * Create it with [rememberExpandableContentState].
 */
@Stable
class ExpandableContentState internal constructor(initialValue: DraggableLayoutState) {
    internal val draggableState = AnchoredDraggableState(initialValue)
    internal val transitionState = SeekableTransitionState(initialValue)

    internal companion object {
        private const val DRAGGABLE_STATE_SETTLED_VALUE = "SettledValue"

        val Saver = mapSaver(
            save = {
                mapOf(
                    DRAGGABLE_STATE_SETTLED_VALUE to it.draggableState.settledValue,
                )
            },
            restore = {
                ExpandableContentState(
                    initialValue = it[DRAGGABLE_STATE_SETTLED_VALUE] as DraggableLayoutState,
                )
            },
        )
    }
}

/**
 * Creates and remembers an [ExpandableContentState] that starts collapsed.
 *
 * The settled state survives configuration changes and process death.
 */
@Composable
fun rememberExpandableContentState() = rememberSaveable(saver = ExpandableContentState.Saver) {
    ExpandableContentState(initialValue = DraggableLayoutState.Collapsed)
}