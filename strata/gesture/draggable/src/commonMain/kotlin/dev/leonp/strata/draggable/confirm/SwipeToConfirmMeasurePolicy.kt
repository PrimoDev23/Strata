package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import dev.leonp.strata.draggable.common.getPlaceableById
import kotlin.math.max
import kotlin.math.roundToInt

@Stable
internal class SwipeToConfirmMeasurePolicy(private val state: SwipeToConfirmState) :
    MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val maxHeight = if (constraints.hasBoundedHeight) {
            constraints.maxHeight
        } else {
            constraints.minHeight
        }
        val thumbConstraints = constraints.copy(
            minWidth = 0,
            minHeight = maxHeight,
            maxHeight = maxHeight,
        )
        val thumbPlaceable = measurables.getPlaceableById(
            layoutId = SwipeToConfirmLayoutId.Thumb,
            constraints = thumbConstraints,
        ) ?: error("There was no LayoutId Thumb")
        val thumbCenterX = thumbPlaceable.width / 2f

        val maxWidth = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            constraints.minWidth
        }
        val contentPlaceable = measurables.getPlaceableById(
            layoutId = SwipeToConfirmLayoutId.Content,
            constraints = constraints.copy(
                minWidth = thumbPlaceable.width,
                maxWidth = maxWidth,
                minHeight = thumbPlaceable.height,
                maxHeight = maxHeight,
            ),
        ) ?: error("There was no LayoutId Content")

        state.draggableState.updateAnchors(
            newAnchors = DraggableAnchors {
                SwipeState.Start at thumbCenterX
                SwipeState.Confirmed at constraints.maxWidth - thumbCenterX
            },
        )
        state.thumbEndOffset = state.draggableState.offset + thumbCenterX

        val width = constraints.maxWidth
        val height = max(
            thumbPlaceable.height,
            contentPlaceable.height,
        )

        return layout(
            width = width,
            height = height,
        ) {
            contentPlaceable.place(
                x = 0,
                y = 0,
            )

            thumbPlaceable.place(
                x = (state.draggableState.offset - thumbCenterX).roundToInt(),
                y = 0,
            )
        }
    }
}

@Composable
internal fun rememberSwipeToConfirmMeasurePolicy(state: SwipeToConfirmState): MeasurePolicy =
    remember(state) {
        SwipeToConfirmMeasurePolicy(state = state)
    }