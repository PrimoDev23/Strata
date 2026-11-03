package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
public class SwipeToConfirmState internal constructor(
    private val initialValue: SwipeState = SwipeState.Start,
) {
    internal val draggableState: AnchoredDraggableState<SwipeState> =
        AnchoredDraggableState(initialValue = initialValue)

    internal var thumbEndOffset by mutableFloatStateOf(0f)

    public val isConfirmed: Boolean
        get() = draggableState.settledValue == SwipeState.Confirmed

    public val offset: Float
        get() = draggableState.offset

    public val progress: Float
        get() = if (draggableState.offset.isNaN()) {
            0f
        } else {
            draggableState.progress(
                from = SwipeState.Start,
                to = SwipeState.Confirmed,
            )
        }

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

@Composable
public fun rememberSwipeToConfirmState(): SwipeToConfirmState =
    rememberSaveable(saver = SwipeToConfirmState.Saver) {
        SwipeToConfirmState()
    }