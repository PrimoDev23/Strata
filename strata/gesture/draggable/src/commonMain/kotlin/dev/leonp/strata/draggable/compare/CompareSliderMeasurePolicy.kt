package dev.leonp.strata.draggable.compare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import dev.leonp.strata.draggable.common.getPlaceableById

@Stable
private class CompareSliderMeasurePolicy(private val state: CompareSliderState) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val width = constraints.maxWidth

        val sliderConstraints = constraints.copyMaxDimensions()
        val sliderPlaceable = measurables.getPlaceableById(
            layoutId = CompareSliderLayoutId.SliderLine,
            constraints = sliderConstraints,
        ) ?: error("Could not find LayoutId SliderLine")

        val imageConstraints = Constraints.fixed(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        )

        val beforePlaceable = measurables.getPlaceableById(
            layoutId = CompareSliderLayoutId.Before,
            constraints = imageConstraints,
        ) ?: error("Could not find LayoutId Before")
        val afterPlaceable = measurables.getPlaceableById(
            layoutId = CompareSliderLayoutId.After,
            constraints = imageConstraints,
        ) ?: error("Could not find LayoutId After")

        val thumbPlaceable = measurables.getPlaceableById(
            layoutId = CompareSliderLayoutId.SliderThumb,
            constraints = constraints.copyMaxDimensions(),
        ) ?: error("Could not find LayoutId SliderThumb")

        return layout(constraints.maxWidth, constraints.maxHeight) {
            val sliderCenter = (constraints.maxWidth / 2f) + state.horizontalOffset

            beforePlaceable.placeWithLayer(
                x = 0,
                y = 0,
                layerBlock = {
                    clip = true
                    shape = RectClip(
                        left = 0f,
                        right = sliderCenter,
                    )
                },
            )

            afterPlaceable.placeWithLayer(
                x = 0,
                y = 0,
                layerBlock = {
                    clip = true
                    shape = RectClip(
                        left = sliderCenter,
                        right = constraints.maxWidth.toFloat(),
                    )
                },
            )

            val sliderX = sliderCenter - (sliderPlaceable.width / 2f)

            sliderPlaceable.place(
                x = sliderX.toInt(),
                y = 0,
            )

            val thumbX = sliderCenter - (thumbPlaceable.width / 2f)
            val thumbY = (constraints.maxHeight - thumbPlaceable.height) / 2f
            thumbPlaceable.place(
                x = thumbX.toInt(),
                y = thumbY.toInt(),
            )
        }
    }

    private class RectClip(val left: Float, val right: Float) : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline = Outline.Rectangle(
            rect = Rect(
                topLeft = Offset(
                    x = left,
                    y = 0f,
                ),
                bottomRight = Offset(
                    x = right,
                    y = size.height,
                ),
            ),
        )
    }
}

@Composable
internal fun rememberCompareSliderMeasurePolicy(state: CompareSliderState): MeasurePolicy =
    remember(state) {
        CompareSliderMeasurePolicy(state = state)
    }