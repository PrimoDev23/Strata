package dev.leonp.strata.draggable.reveal

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
public class SwipeToRevealState internal constructor(initialRevealState: RevealState) {
    internal val draggableState: AnchoredDraggableState<RevealState> = AnchoredDraggableState(
        initialValue = initialRevealState,
    )

    /**
     * Whether the actions have settled revealed.
     *
     * It stays `false` while the content is dragged or animated towards the revealed position and
     * only changes once it has arrived. It is backed by snapshot state, so reading it in composition
     * recomposes when it changes.
     */
    public val isRevealed: Boolean
        get() = draggableState.settledValue == RevealState.Revealed

    /**
     * Animates the content aside to reveal the actions.
     *
     * Suspends until the animation finishes. If it is interrupted, for example by a drag or another
     * call to [reveal] or [hide], it is cancelled.
     */
    public suspend fun reveal() {
        draggableState.animateTo(RevealState.Revealed)
    }

    /**
     * Animates the content back over the actions.
     *
     * Suspends until the animation finishes. If it is interrupted, for example by a drag or another
     * call to [reveal] or [hide], it is cancelled.
     */
    public suspend fun hide() {
        draggableState.animateTo(RevealState.Hidden)
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

@Composable
public fun rememberSwipeToRevealState(): SwipeToRevealState = rememberSaveable(
    saver = SwipeToRevealState.Saver,
) {
    SwipeToRevealState(initialRevealState = RevealState.Hidden)
}