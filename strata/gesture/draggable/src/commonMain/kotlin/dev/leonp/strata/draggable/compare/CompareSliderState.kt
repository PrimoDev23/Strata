package dev.leonp.strata.draggable.compare

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Holds the position of the slider of a [CompareSlider].
 *
 * @param initialOffset The distance of the slider from the middle of the layout in pixels.
 */
@Stable
internal class CompareSliderState(initialOffset: Float) {
    /**
     * The distance of the slider from the middle of the layout in pixels. It is read while
     * measuring, so dragging relays out the slider without recomposing.
     */
    internal var horizontalOffset by mutableStateOf(initialOffset)

    /** Moves the slider by the dragged distance. */
    internal val draggableState = DraggableState {
        horizontalOffset += it
    }

    companion object {
        private const val OFFSET_KEY = "HorizontalOffset"

        /** Saves the [horizontalOffset], so the slider keeps its position after process death. */
        internal val Saver = mapSaver(
            save = {
                mapOf(OFFSET_KEY to it.horizontalOffset)
            },
            restore = {
                CompareSliderState(it[OFFSET_KEY] as Float)
            },
        )
    }
}

/** Creates a [CompareSliderState] with the slider in the middle that survives process death. */
@Composable
internal fun rememberCompareSliderState() = rememberSaveable(saver = CompareSliderState.Saver) {
    CompareSliderState(initialOffset = 0f)
}