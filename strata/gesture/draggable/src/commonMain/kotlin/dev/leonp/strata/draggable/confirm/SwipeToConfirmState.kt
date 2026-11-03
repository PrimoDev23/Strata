package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * The state of a [SwipeToConfirm]. Create it with [rememberSwipeToConfirmState].
 *
 * A confirmed state does not accept drags anymore, call [reset] to move the thumb back to the
 * start. The settled state survives configuration changes and process death.
 */
@Stable
public class SwipeToConfirmState internal constructor(
    private val initialValue: SwipeState = SwipeState.Start,
) {
    internal val draggableState: AnchoredDraggableState<SwipeState> =
        AnchoredDraggableState(initialValue = initialValue)

    internal var thumbEndOffset by mutableFloatStateOf(0f)

    /** Whether the thumb settled at the end of the track. It stays `false` while the thumb is dragged. */
    public val isConfirmed: Boolean
        get() = draggableState.settledValue == SwipeState.Confirmed

    /** The offset of the thumb in pixels, or `NaN` until the track was measured. */
    public val offset: Float
        get() = draggableState.offset

    /**
     * How far the thumb moved from the start to the end of the track, from `0f` to `1f`. It is `0f`
     * until the track was measured, so it is safe to read during the first composition.
     */
    public val progress: Float
        get() = if (draggableState.offset.isNaN()) {
            0f
        } else {
            draggableState.progress(
                from = SwipeState.Start,
                to = SwipeState.Confirmed,
            )
        }

    /**
     * Moves the thumb to the end of the track without a drag, which confirms it. It is used by
     * accessibility services, which cannot swipe.
     */
    internal suspend fun confirm() {
        draggableState.snapTo(targetValue = SwipeState.Confirmed)
    }

    /** Animates the thumb back to the start of the track. */
    public suspend fun reset() {
        draggableState.animateTo(targetValue = SwipeState.Start)
    }

    internal companion object {
        private const val SETTLED_VALUE_KEY = "SettledValue"

        internal val Saver = mapSaver(
            save = {
                mapOf(SETTLED_VALUE_KEY to it.draggableState.settledValue)
            },
            restore = {
                SwipeToConfirmState(initialValue = it[SETTLED_VALUE_KEY] as SwipeState)
            },
        )
    }
}

/** Creates and remembers a [SwipeToConfirmState] that survives configuration changes. */
@Composable
public fun rememberSwipeToConfirmState(): SwipeToConfirmState =
    rememberSaveable(saver = SwipeToConfirmState.Saver) {
        SwipeToConfirmState()
    }
