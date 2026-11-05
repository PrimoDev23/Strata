package dev.leonp.strata.draggable.reveal

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
import kotlin.math.roundToInt

/**
 * Measures and places the actions and the content of a [SwipeToReveal].
 *
 * The anchors of the [state] are derived from the measured width of the actions. The drag offset is
 * read in the placement block, so dragging replaces the content without recomposing or remeasuring.
 */
@Stable
private class SwipeToRevealMeasurePolicy(private val state: SwipeToRevealState) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val contentPlaceable = measurables.getPlaceableById(
            layoutId = SwipeToRevealLayoutId.MainContent,
            constraints = constraints.copyMaxDimensions(),
        ) ?: error("There was no LayoutId MainContent!")

        val height = contentPlaceable.height
        val width = contentPlaceable.width

        val startActionsConstraints = constraints.copy(
            minHeight = height,
            maxHeight = height,
            minWidth = 0,
            maxWidth = width,
        )
        val startActionsPlaceable = measurables.getPlaceableById(
            layoutId = SwipeToRevealLayoutId.StartActions,
            constraints = startActionsConstraints,
        )

        val startActionsWidth = startActionsPlaceable?.width ?: 0

        val remainingWidth = (width - startActionsWidth).coerceIn(0..width)
        val endActionsConstraints = constraints.copy(
            minHeight = height,
            maxHeight = height,
            minWidth = 0,
            maxWidth = remainingWidth,
        )
        val endActionsPlaceable = measurables.getPlaceableById(
            layoutId = SwipeToRevealLayoutId.EndActions,
            constraints = endActionsConstraints,
        )

        val contentConstraints = Constraints.fixed(
            width = width,
            height = height,
        )

        state.draggableState.updateAnchors(
            newAnchors = DraggableAnchors {
                if (startActionsPlaceable != null) {
                    RevealState.StartActionsVisible at startActionsWidth.toFloat()
                }

                if (endActionsPlaceable != null) {
                    RevealState.EndActionsVisible at -endActionsPlaceable.width.toFloat()
                }

                RevealState.NoActionsVisible at 0f
            },
            newTarget = state.draggableState.targetValue,
        )

        return layout(
            width = width,
            height = height,
        ) {
            startActionsPlaceable?.place(
                x = 0,
                y = 0,
            )

            if (endActionsPlaceable != null) {
                val endActionsX = (width - endActionsPlaceable.width).coerceIn(0..width)
                endActionsPlaceable.place(
                    x = endActionsX,
                    y = 0,
                )
            }

            contentPlaceable.place(
                x = state.draggableState.offset.roundToInt(),
                y = 0,
            )
        }
    }
}

/** Remembers the [MeasurePolicy] of a [SwipeToReveal] that is driven by [state]. */
@Composable
internal fun rememberSwipeToRevealMeasurePolicy(state: SwipeToRevealState): MeasurePolicy =
    remember(state) {
        SwipeToRevealMeasurePolicy(state = state)
    }