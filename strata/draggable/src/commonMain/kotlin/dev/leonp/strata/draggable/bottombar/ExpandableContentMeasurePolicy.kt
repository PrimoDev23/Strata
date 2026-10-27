package dev.leonp.strata.draggable.bottombar

import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.common.getPlaceableById

/**
 * Measures [ExpandableContent].
 *
 * Measures the hidden collapsed copy at its natural height to update the anchors of
 * [ExpandableContentState.draggableState]: [DraggableLayoutState.Expanded] at `0` and
 * [DraggableLayoutState.Collapsed] at the full height minus the collapsed height. The visible
 * content is measured at its natural size within the full height, and the layout takes the height
 * given by the current drag offset. The offset is read during measurement, so dragging remeasures
 * the layout without recomposing it.
 */
private class ExpandableContentMeasurePolicy(private val state: ExpandableContentState) :
    MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        require(constraints.hasBoundedHeight && constraints.hasBoundedWidth) {
            "ExpandableContent cannot be measured with Infinite constraints"
        }

        val collapsedContentConstraints = constraints.copyMaxDimensions()
        val collapsedContentPlaceable = measurables.getPlaceableById(
            layoutId = ExpandableContentLayoutId.Collapsed,
            constraints = collapsedContentConstraints,
        ) ?: error("There was no measurable with the LayoutId Collapsed")

        val collapsedAnchor = constraints.maxHeight - collapsedContentPlaceable.height
        state.draggableState.updateAnchors(
            newAnchors = DraggableAnchors {
                DraggableLayoutState.Expanded at 0f
                DraggableLayoutState.Collapsed at collapsedAnchor.toFloat()
            },
        )

        val currentContentHeight = (constraints.maxHeight - state.draggableState.offset).toInt()
        // Measure the current content at its natural size within the full height instead of the
        // current dragged height, so every state is laid out like its final layout. The layout
        // itself only takes the current height and clips the rest.
        val currentContentConstraints = constraints.copy(
            minWidth = 0,
            maxWidth = constraints.maxWidth,
            minHeight = 0,
            maxHeight = constraints.maxHeight,
        )
        val currentContentPlaceable = measurables.getPlaceableById(
            layoutId = ExpandableContentLayoutId.Current,
            constraints = currentContentConstraints,
        ) ?: error("There was no measurable with the LayoutId Current")

        return layout(
            width = constraints.maxWidth,
            height = currentContentHeight,
        ) {
            currentContentPlaceable.place(
                x = 0,
                y = 0,
            )
        }
    }
}

/** Creates and remembers the [MeasurePolicy] of [ExpandableContent] for [state]. */
@Composable
internal fun rememberExpandableContentMeasurePolicy(state: ExpandableContentState): MeasurePolicy =
    remember(
        state,
    ) {
        ExpandableContentMeasurePolicy(state = state)
    }