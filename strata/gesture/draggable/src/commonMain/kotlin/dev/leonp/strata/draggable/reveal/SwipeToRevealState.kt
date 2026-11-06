package dev.leonp.strata.draggable.reveal

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable

private const val DEFAULT_ANIMATION_DURATION_MILLIS = 300

/**
 * Observes and controls a [SwipeToReveal].
 *
 * Create it with [rememberSwipeToRevealState]. Its [settledValue] survives configuration changes
 * and process death.
 */
@Stable
public class SwipeToRevealState internal constructor(initialRevealState: RevealState) {
    internal val draggableState: AnchoredDraggableState<RevealState> = AnchoredDraggableState(
        initialValue = initialRevealState,
    )

    /**
     * The position the content has settled at.
     *
     * It only changes once the content has arrived at a new position, so it keeps its previous
     * value while the content is dragged or animated. It is backed by snapshot state, so reading it
     * in composition recomposes when it changes.
     */
    public val settledValue: RevealState
        get() = draggableState.settledValue

    /**
     * Whether the content has settled at a position that shows either the start or the end actions.
     *
     * Like [settledValue] it stays unchanged while the content is dragged or animated, and reading
     * it in composition recomposes when it changes.
     */
    public val isRevealed: Boolean
        get() = draggableState.settledValue != RevealState.NoActionsVisible

    /**
     * Animates the content to the position of [targetValue].
     *
     * Suspends until the animation finishes. If it is interrupted, for example by a drag, [snapTo]
     * or another call to [animateTo], it is cancelled. Only pass positions whose actions are
     * provided to [SwipeToReveal], as the content has no position to move to otherwise.
     *
     * @param targetValue The position to animate to.
     * @param animationSpec The animation that moves the content.
     */
    public suspend fun animateTo(
        targetValue: RevealState,
        animationSpec: FiniteAnimationSpec<Float>,
    ) {
        draggableState.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec,
        )
    }

    /**
     * Animates the content to the position of [targetValue] with the animation the library uses for
     * transitions it starts itself, for example from accessibility actions or a
     * [SwipeToRevealGroup].
     */
    internal suspend fun animateToDefault(targetValue: RevealState) {
        animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS),
        )
    }

    /**
     * Moves the content to the position of [targetValue] without an animation.
     *
     * Use it to reset the state, for example when the item it belongs to is replaced. It cancels a
     * running drag or animation. Only pass positions whose actions are provided to
     * [SwipeToReveal], as the content has no position to move to otherwise.
     *
     * @param targetValue The position to jump to.
     */
    public suspend fun snapTo(targetValue: RevealState) {
        draggableState.snapTo(targetValue = targetValue)
    }

    internal companion object {
        private const val SETTLED_VALUE_KEY = "SettledValue"

        internal val Saver = mapSaver(
            save = {
                mapOf(SETTLED_VALUE_KEY to it.draggableState.settledValue)
            },
            restore = {
                SwipeToRevealState(initialRevealState = it[SETTLED_VALUE_KEY] as RevealState)
            },
        )
    }
}

/**
 * Creates a [SwipeToRevealState] that starts with the actions hidden.
 *
 * The settled position is saved and restored across configuration changes and process death.
 */
@Composable
public fun rememberSwipeToRevealState(): SwipeToRevealState = rememberSaveable(
    saver = SwipeToRevealState.Saver,
) {
    SwipeToRevealState(initialRevealState = RevealState.NoActionsVisible)
}
