package dev.leonp.strata.draggable.collapsible

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

@Composable
internal fun CollapsibleHeaderStateTransitionEffect(state: CollapsibleHeaderContainerState) {
    LaunchedEffect(state) {
        snapshotFlow {
            state.draggableState.progress(
                from = CollapsibleHeaderState.Collapsed,
                to = CollapsibleHeaderState.Expanded
            )
        }.collect { progress ->
            when {
                progress == 0f -> state.transitionState.snapTo(CollapsibleHeaderState.Collapsed)

                progress == 1f -> state.transitionState.snapTo(CollapsibleHeaderState.Expanded)

                state.transitionState.currentState == CollapsibleHeaderState.Collapsed -> state.transitionState.seekTo(
                    fraction = progress,
                    targetState = CollapsibleHeaderState.Expanded
                )

                else -> state.transitionState.seekTo(
                    fraction = 1f - progress,
                    targetState = CollapsibleHeaderState.Collapsed
                )
            }
        }
    }
}