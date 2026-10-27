package dev.leonp.strata.draggable.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import dev.leonp.strata.draggable.common.DraggableLayoutState

@Composable
internal fun CollapsibleHeaderStateTransitionEffect(state: CollapsibleHeaderContainerState) {
    LaunchedEffect(state) {
        snapshotFlow {
            state.draggableState.progress(
                from = DraggableLayoutState.Collapsed,
                to = DraggableLayoutState.Expanded
            )
        }.collect { progress ->
            when {
                progress == 0f -> state.transitionState.snapTo(DraggableLayoutState.Collapsed)

                progress == 1f -> state.transitionState.snapTo(DraggableLayoutState.Expanded)

                state.transitionState.currentState == DraggableLayoutState.Collapsed -> state.transitionState.seekTo(
                    fraction = progress,
                    targetState = DraggableLayoutState.Expanded
                )

                else -> state.transitionState.seekTo(
                    fraction = 1f - progress,
                    targetState = DraggableLayoutState.Collapsed
                )
            }
        }
    }
}