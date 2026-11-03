package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

@Composable
public fun SwipeToConfirm(
    thumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: SwipeToConfirmState = rememberSwipeToConfirmState(),
    shape: Shape = SwipeToConfirmDefaults.shape,
    onConfirm: () -> Unit = {},
    enabled: Boolean = true,
    confirmationThreshold: Float = SwipeToConfirmDefaults.CONFIRMATION_THRESHOLD,
    contentPadding: PaddingValues = SwipeToConfirmDefaults.contentPadding,
    colors: SwipeToConfirmColors = SwipeToConfirmDefaults.colors(),
    content: @Composable BoxScope.() -> Unit,
) {
    val updatedOnConfirm by rememberUpdatedState(onConfirm)
    LaunchedEffect(state.draggableState) {
        snapshotFlow { state.draggableState.settledValue }
            .drop(1)
            .filter { it == SwipeState.Confirmed }
            .collect {
                updatedOnConfirm()
            }
    }

    val flingBehavior = rememberSwipeToConfirmFlingBehavior(
        state = state,
        positionalThreshold = {
            it * confirmationThreshold
        },
    )

    Layout(
        modifier = modifier
            .background(
                color = colors.backgroundColor,
                shape = shape,
            )
            .clip(shape = shape),
        measurePolicy = rememberSwipeToConfirmMeasurePolicy(state = state),
        content = {
            Box(
                modifier = Modifier
                    .then(
                        other = colors.progressColor.takeIf { it.isSpecified }?.let { color ->
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