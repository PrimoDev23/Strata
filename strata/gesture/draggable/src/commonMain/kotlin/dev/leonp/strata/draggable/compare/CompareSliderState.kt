package dev.leonp.strata.draggable.compare

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
internal class CompareSliderState(initialOffset: Float) {
    internal var horizontalOffset by mutableStateOf(initialOffset)
    internal val draggableState = DraggableState {
        horizontalOffset += it
    }

    companion object {
        private const val OFFSET_KEY = "HorizontalOffset"

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

@Composable
internal fun rememberCompareSliderState() = rememberSaveable(saver = CompareSliderState.Saver) {
    CompareSliderState(initialOffset = 0f)
}