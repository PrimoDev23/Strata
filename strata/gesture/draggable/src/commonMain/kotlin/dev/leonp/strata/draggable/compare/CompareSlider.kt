package dev.leonp.strata.draggable.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun CompareSlider(
    before: @Composable () -> Unit,
    after: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sliderThickness: Dp = 2.dp,
    sliderColor: Color = Color.White,
) {
    val state = rememberCompareSliderState()

    Layout(
        modifier = modifier,
        measurePolicy = rememberCompareSliderMeasurePolicy(state = state),
        content = {
            Box(modifier = Modifier.layoutId(CompareSliderLayoutId.Before)) {
                before()
            }

            Box(modifier = Modifier.layoutId(CompareSliderLayoutId.After)) {
                after()
            }

            Box(
                modifier = Modifier
                    .draggable(
                        state = state.draggableState,
                        orientation = Orientation.Horizontal,
                    )
                    .layoutId(CompareSliderLayoutId.SliderThumb),
            ) {
                thumb()
            }

            Spacer(
                modifier = Modifier
                    .width(sliderThickness)
                    .fillMaxHeight()
                    .background(color = sliderColor)
                    .layoutId(CompareSliderLayoutId.SliderLine),
            )
        },
    )
}