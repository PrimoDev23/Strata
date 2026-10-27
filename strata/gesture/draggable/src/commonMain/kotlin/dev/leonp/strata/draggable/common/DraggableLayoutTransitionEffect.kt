package dev.leonp.strata.draggable.common

import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

/**
 * Drives [transitionState] with the drag progress of [draggableState].
 *
 * While the layout is between both states, the transition is seeked towards the other state by the
 * drag progress, so every animation of the transition follows the drag. Once the layout reaches a
 * state exactly, the transition snaps to it.
 *
 * Seeking maps the progress linearly onto the total duration of the transition, which is the end
 * time of its longest animation, including child transitions.
 *
 * @param draggableState the state whose offset drives the transition.
 * @param transitionState the transition state that follows the drag progress.
 */
@Composable
internal fun DraggableLayoutTransitionEffect(
    draggableState: AnchoredDraggableState<DraggableLayoutState>,
    transitionState: SeekableTransitionState<DraggableLayoutState>,
) {
    LaunchedEffect(draggableState, transitionState) {
        snapshotFlow {
            draggableState.progress(
                from = DraggableLayoutState.Collapsed,
                to = DraggableLayoutState.Expanded,
            )
        }.collect { progress ->
            when {
                progress == 0f -> transitionState.snapTo(DraggableLayoutState.Collapsed)

                progress == 1f -> transitionState.snapTo(DraggableLayoutState.Expanded)

                transitionState.currentState == DraggableLayoutState.Collapsed -> transitionState.seekTo(
                    fraction = progress,
                    targetState = DraggableLayoutState.Expanded,
                )

                else -> transitionState.seekTo(
                    fraction = 1f - progress,
                    targetState = DraggableLayoutState.Collapsed,
                )
            }
        }
    }
}