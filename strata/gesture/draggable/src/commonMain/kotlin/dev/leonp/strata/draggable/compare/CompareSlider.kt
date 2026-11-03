package dev.leonp.strata.draggable.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Compares two layouts, such as the same image before and after an edit, by dragging a slider
 * between them. [before] is visible left of the slider and [after] right of it.
 *
 * Both slots are measured at the full size of the layout and are only clipped at the slider, so
 * dragging reveals more or less of them without scaling or relaying them out. Elements in the
 * slots, such as labels in a corner, therefore keep their position while the slider moves.
 *
 * The slider is dragged horizontally by its [thumb]. Its position lives in the [state], which
 * starts the slider in the middle by default, lets you move it from code and, when created with
 * [rememberCompareSliderState], survives configuration changes and process death.
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
 * @param state The [CompareSliderState] holding the position of the slider. Hoist it to read or
 * change the position, for example with [CompareSliderState.animateTo].
 * @param sliderIndicatorStroke The [SliderIndicatorStroke] describing the thickness and color of
 * the line between [before] and [after].
 * @param interactionSource The [MutableInteractionSource] that emits the drag interactions of the
 * [thumb], for example to show a pressed or dragged appearance. Pass `null` to ignore them.
 * @param enabled Whether the [thumb] can be dragged. The slider can still be moved through the
 * [state] while it is disabled.
 */
@Composable
public fun CompareSlider(
    before: @Composable () -> Unit,
    after: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: CompareSliderState = rememberCompareSliderState(),
    sliderIndicatorStroke: SliderIndicatorStroke = SliderIndicatorStroke(),
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
) {
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
                        enabled = enabled,
                        reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                        interactionSource = interactionSource,
                    )
                    .layoutId(CompareSliderLayoutId.SliderThumb),
            ) {
                thumb()
            }

            Spacer(
                modifier = Modifier
                    .width(sliderIndicatorStroke.thickness)
                    .fillMaxHeight()
                    .background(color = sliderIndicatorStroke.color)
                    .layoutId(CompareSliderLayoutId.SliderLine),
            )
        },
    )
}