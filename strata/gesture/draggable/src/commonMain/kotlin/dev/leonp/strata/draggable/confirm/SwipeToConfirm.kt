package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
public fun SwipeToConfirm(
    thumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: SwipeToConfirmState = rememberSwipeToConfirmState(),
    shape: Shape = CircleShape,
    trackColor: Color? = null,
    onConfirm: () -> Unit = {},
    enabled: Boolean = true,
    positionalThreshold: (
        totalDistance: Float,
    ) -> Float = AnchoredDraggableDefaults.PositionalThreshold,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val updatedOnConfirm by rememberUpdatedState(onConfirm)
    LaunchedEffect(state.draggableState.settledValue) {
        if (state.draggableState.settledValue == SwipeState.Confirmed) {
            updatedOnConfirm()
        }
    }

    val flingBehavior = rememberSwipeToConfirmFlingBehavior(
        state = state,
        positionalThreshold = positionalThreshold,
    )

    Layout(
        modifier = modifier.clip(shape = shape),
        measurePolicy = rememberSwipeToConfirmMeasurePolicy(state = state),
        content = {
            Box(
                modifier = Modifier
                    .then(
                        other = trackColor?.let { color ->
                            Modifier.drawWithContent {
                                drawContent()

                                val trackSize = size.copy(width = state.thumbEndOffset)
                                val outline = shape.createOutline(
                                    size = trackSize,
                                    layoutDirection = layoutDirection,
                                    density = this,
                                )

                                drawOutline(
                                    outline = outline,
                                    color = color,
                                )
                            }
                        } ?: Modifier,
                    )
                    .padding(contentPadding)
                    .layoutId(SwipeToConfirmLayoutId.Content),
            ) {
                content()
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Horizontal,
                        enabled = enabled &&
                            state.draggableState.settledValue != SwipeState.Confirmed &&
                            !state.draggableState.isAnimationRunning,
                        reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                        flingBehavior = flingBehavior,
                    )
                    .padding(contentPadding)
                    .layoutId(SwipeToConfirmLayoutId.Thumb),
            ) {
                thumb()
            }
        },
    )
}