package dev.leonp.strata.draggable.confirm

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * Creates the fling behavior that settles the thumb after the drag ended.
 *
 * The thumb confirms if it was dragged further than [positionalThreshold] and snaps back otherwise,
 * no matter how fast it was released. The default fling behavior of `AnchoredDraggable` only
 * applies the threshold to slow releases, so a quick flick or a release without movement would
 * ignore it.
 */
@Composable
internal fun rememberSwipeToConfirmFlingBehavior(
    state: SwipeToConfirmState,
    positionalThreshold: (totalDistance: Float) -> Float,
    animationSpec: AnimationSpec<Float> = AnchoredDraggableDefaults.SnapAnimationSpec,
): TargetedFlingBehavior {
    // The thumb can be released while the caller recomposes with a new lambda
    val updatedPositionalThreshold by rememberUpdatedState(positionalThreshold)

    return remember(state, animationSpec) {
        snapFlingBehavior(
            snapLayoutInfoProvider = SwipeToConfirmSnapLayoutInfoProvider(
                state = state,
                positionalThreshold = { totalDistance ->
                    updatedPositionalThreshold(totalDistance)
                },
            ),
            // The approach offset is always zero, so the decay is never used
            decayAnimationSpec = exponentialDecay(),
            snapAnimationSpec = animationSpec,
        )
    }
}

private class SwipeToConfirmSnapLayoutInfoProvider(
    private val state: SwipeToConfirmState,
    private val positionalThreshold: (totalDistance: Float) -> Float,
) : SnapLayoutInfoProvider {
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float = 0f

    override fun calculateSnapOffset(velocity: Float): Float {
        val anchors = state.draggableState.anchors
        val currentOffset = state.draggableState.requireOffset()
        val startPosition = anchors.positionOf(SwipeState.Start)
        val confirmedPosition = anchors.positionOf(SwipeState.Confirmed)

        val totalDistance = confirmedPosition - startPosition
        val draggedDistance = currentOffset - startPosition
        val targetPosition = if (draggedDistance >= positionalThreshold(totalDistance)) {
            confirmedPosition
        } else {
            startPosition
        }

        return targetPosition - currentOffset
    }
}
