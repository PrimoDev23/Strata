package dev.leonp.strata.draggable.confirm

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
import androidx.compose.ui.Modifier
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
    onConfirm: () -> Unit = {},
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val updatedOnConfirm by rememberUpdatedState(onConfirm)
    LaunchedEffect(state.draggableState.settledValue) {
        if (state.draggableState.settledValue == SwipeState.Confirmed) {
            updatedOnConfirm()
        }
    }

    Layout(
        modifier = modifier.padding(contentPadding),
        measurePolicy = rememberSwipeToConfirmMeasurePolicy(state = state),
        content = {
            Box(modifier = Modifier.layoutId(SwipeToConfirmLayoutId.Content)) {
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
                    )
                    .layoutId(SwipeToConfirmLayoutId.Thumb),
            ) {
                thumb()
            }
        },
    )
}