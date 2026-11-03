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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * A track with a thumb that is slid to its end to confirm an action, so the action cannot be
 * triggered by an accidental tap.
 *
 * The thumb is as high as the track, so it only needs an aspect ratio to be round. The track needs
 * a bounded height for that, for example through [Modifier.height]. Once released, the thumb
 * confirms if it was dragged further than [confirmationThreshold], no matter how fast it was
 * released, and snaps back otherwise.
 *
 * @param thumb The draggable thumb.
 * @param modifier The modifier of the track. The track is clipped to [shape].
 * @param state The state that tells the progress of the thumb and resets it.
 * @param shape The shape of the track, which also clips the progress fill.
 * @param onConfirm Called once the thumb is confirmed, but not again after the state was restored.
 * @param enabled Whether the thumb can be dragged. A confirmed thumb cannot be dragged either.
 * @param confirmationThreshold The fraction of the track the thumb has to be dragged to confirm,
 *   between `0f` and `1f`.
 * @param contentPadding The space between the track and both the thumb and the [content].
 * @param colors The colors of the track, see [SwipeToConfirmDefaults.colors].
 * @param content The label of the track, drawn behind the thumb. It fills the whole track, so
 *   children can be centered with [BoxScope.align].
 */
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
    confirmedStateDescription: String? = null,
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

    val dragEnabled = enabled && !state.isConfirmed

    val coroutineScope = rememberCoroutineScope()
    Layout(
        modifier = modifier
            .background(
                color = colors.backgroundColor,
                shape = shape,
            )
            .clip(shape = shape)
            .semantics(mergeDescendants = true) {
                role = Role.Button

                if (!dragEnabled) {
                    disabled()
                } else {
                    onClick(
                        label = null,
                        action = {
                            coroutineScope.launch {
                                state.confirm()
                            }
                            true
                        },
                    )
                }

                if (state.isConfirmed && confirmedStateDescription != null) {
                    stateDescription = confirmedStateDescription
                }
            },
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
                        enabled = dragEnabled,
                        reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                        flingBehavior = flingBehavior,
                    )
                    .padding(contentPadding)
                    .layoutId(SwipeToConfirmLayoutId.Thumb)
                    .semantics(mergeDescendants = false) {
                        hideFromAccessibility()
                    },
            ) {
                thumb()
            }
        },
    )
}