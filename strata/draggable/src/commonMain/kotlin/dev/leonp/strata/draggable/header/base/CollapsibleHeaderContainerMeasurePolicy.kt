package dev.leonp.strata.draggable.header.base

import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import dev.leonp.strata.draggable.header.CollapsibleHeaderContainerState
import dev.leonp.strata.draggable.header.CollapsibleHeaderState

internal class CollapsibleHeaderContainerMeasurePolicy(
    private val state: CollapsibleHeaderContainerState
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        require(constraints.hasBoundedHeight && constraints.hasBoundedWidth) {
            "CollapsibleHeaderContainer cannot be measured with Infinite constraints"
        }

        val headerPlaceable = measureHeader(
            measurables = measurables,
            constraints = constraints
        )

        val contentHeight = constraints.maxHeight - headerPlaceable.height
        val contentConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = contentHeight,
            maxHeight = contentHeight
        )

        val contentPlaceable = measurables.getPlaceableById(
            layoutId = CollapsibleHeaderLayoutId.Content,
            constraints = contentConstraints
        ) ?: error("Content was not defined in Layout")

        return layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            headerPlaceable.place(
                x = 0,
                y = 0
            )

            contentPlaceable.place(
                x = 0,
                y = headerPlaceable.height
            )
        }
    }

    private fun measureHeader(measurables: List<Measurable>, constraints: Constraints): Placeable {
        val headerConstraints = constraints.copyMaxDimensions()
        val collapsedHeaderHeight = measurables.getPlaceableById(
            layoutId = CollapsibleHeaderLayoutId.CollapsedHeader,
            constraints = headerConstraints
        )?.height ?: error("Collapsed header was not defined in the Layout")

        val expandedHeaderHeight = measurables.getPlaceableById(
            layoutId = CollapsibleHeaderLayoutId.ExpandedHeader,
            constraints = headerConstraints
        )?.height ?: error("Expanded header was not defined in Layout")

        state.draggableState.updateAnchors(
            newAnchors = DraggableAnchors {
                CollapsibleHeaderState.Collapsed at collapsedHeaderHeight.toFloat()
                CollapsibleHeaderState.Expanded at expandedHeaderHeight.toFloat()
            }
        )

        val currentHeaderHeight = if (state.draggableState.offset.isNaN()) {
            expandedHeaderHeight
        } else {
            state.draggableState.offset.toInt()
        }
        val realHeaderConstraints = constraints.copy(
            minHeight = currentHeaderHeight,
            maxHeight = currentHeaderHeight
        )
        val headerPlaceable = measurables.getPlaceableById(
            layoutId = CollapsibleHeaderLayoutId.Header,
            constraints = realHeaderConstraints
        ) ?: error("Header was not defined in the Layout")

        return headerPlaceable
    }

    private fun List<Measurable>.getPlaceableById(
        layoutId: CollapsibleHeaderLayoutId,
        constraints: Constraints
    ): Placeable? = this.find { it.layoutId == layoutId }?.measure(constraints)
}

@Composable
internal fun rememberCollapsibleHeaderContainerMeasurePolicy(
    state: CollapsibleHeaderContainerState
) = remember(state) {
    CollapsibleHeaderContainerMeasurePolicy(state = state)
}