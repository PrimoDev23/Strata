package dev.leonp.strata.draggable.collapsible

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class CollapsibleHeaderContainerState internal constructor(
    initialValue: CollapsibleHeaderState = CollapsibleHeaderState.Expanded
) {
    internal val draggableState = AnchoredDraggableState(initialValue = initialValue)

    internal companion object {
        val Saver = mapSaver(
            save = {
                mapOf(DRAGGABLE_STATE_SETTLED_VALUE_KEY to it.draggableState.settledValue)
            },
            restore = {
                CollapsibleHeaderContainerState(
                    initialValue = it[DRAGGABLE_STATE_SETTLED_VALUE_KEY] as CollapsibleHeaderState
                )
            }
        )

        private const val DRAGGABLE_STATE_SETTLED_VALUE_KEY = "DraggableStateSettledValueKey"
    }
}

@Composable
fun rememberCollapsibleHeaderContainerState() =
    rememberSaveable(saver = CollapsibleHeaderContainerState.Saver) {
        CollapsibleHeaderContainerState()
    }