package dev.leonp.strata.draggable.header.common

import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import dev.leonp.strata.draggable.common.DraggableLayoutState

/**
 * The state of [dev.leonp.strata.draggable.header.CollapsibleHeaderContainer] and [dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer].
 *
 * Holds the current header height, driven by scroll and drag gestures, and the transition between
 * the collapsed and the expanded header. Only the settled [DraggableLayoutState] is saved, so a
 * header that was restored while a gesture was in progress starts at its last settled state.
 *
 * Create it with [rememberCollapsibleHeaderContainerState].
 */
@Stable
class CollapsibleHeaderContainerState internal constructor(
    initialValue: DraggableLayoutState = DraggableLayoutState.Expanded,
) {
    internal val draggableState = AnchoredDraggableState(initialValue = initialValue)
    internal val transitionState = SeekableTransitionState(initialState = initialValue)

    internal companion object {
        val Saver = mapSaver(
            save = {
                mapOf(DRAGGABLE_STATE_SETTLED_VALUE_KEY to it.draggableState.settledValue)
            },
            restore = {
                CollapsibleHeaderContainerState(
                    initialValue = it[DRAGGABLE_STATE_SETTLED_VALUE_KEY] as DraggableLayoutState,
                )
            },
        )

        private const val DRAGGABLE_STATE_SETTLED_VALUE_KEY = "DraggableStateSettledValueKey"
    }
}

/**
 * Creates and remembers a [CollapsibleHeaderContainerState] that starts expanded.
 *
 * The settled state survives configuration changes and process death.
 */
@Composable
fun rememberCollapsibleHeaderContainerState() =
    rememberSaveable(saver = CollapsibleHeaderContainerState.Saver) {
        CollapsibleHeaderContainerState()
    }