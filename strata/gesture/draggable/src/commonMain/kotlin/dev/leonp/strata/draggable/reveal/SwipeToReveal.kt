package dev.leonp.strata.draggable.reveal

import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
public fun SwipeToReveal(
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    state: SwipeToRevealState = rememberSwipeToRevealState(),
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .onSizeChanged { size ->
                    state.draggableState.updateAnchors(
                        newAnchors = DraggableAnchors {
                            RevealState.Hidden at 0f
                            RevealState.Revealed at -size.width.toFloat()
                        },
                        newTarget = state.draggableState.targetValue,
                    )
                }
                .align(Alignment.TopEnd),
        ) {
            actions()
        }

        Box(
            modifier = Modifier
                .anchoredDraggable(
                    state = state.draggableState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled,
                )
                .offset {
                    IntOffset(
                        x = state.draggableState.offset.roundToInt(),
                        y = 0,
                    )
                },
        ) {
            content()
        }
    }
}