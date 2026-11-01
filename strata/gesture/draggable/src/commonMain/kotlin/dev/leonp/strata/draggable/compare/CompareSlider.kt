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

/**
 * Compares two layouts, such as the same image before and after an edit, by dragging a slider
 * between them. [before] is visible left of the slider and [after] right of it.
 *
 * Both slots are measured at the full size of the layout and are only clipped at the slider, so
 * dragging reveals more or less of them without scaling or relaying them out. Elements in the
 * slots, such as labels in a corner, therefore keep their position while the slider moves.
 *
 * The slider starts in the middle and is dragged horizontally by its [thumb]. The position
 * survives configuration changes and process death.
 *
 * ## Layout
 *
 * The layout fills the incoming constraints, so give it a size, for example with
 * `Modifier.fillMaxWidth().aspectRatio(3f / 4f)`. Clip the modifier to a shape to round the corners
 * of both slots. The slider line spans the full height and the [thumb] is centered on it
 * vertically.
 *
 * @param before The content left of the slider.
 * @param after The content right of the slider.
 * @param thumb The handle in the middle of the slider line. Only the thumb reacts to drags, so
 * give it a size that is easy to hit.
 * @param modifier The [Modifier] applied to the layout.
 * @param sliderThickness The width of the line between [before] and [after].
 * @param sliderColor The color of the line between [before] and [after].
 */
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