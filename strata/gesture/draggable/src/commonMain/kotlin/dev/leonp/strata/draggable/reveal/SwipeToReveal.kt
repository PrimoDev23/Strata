package dev.leonp.strata.draggable.reveal

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.filter

/**
 * A layout whose [content] can be swiped horizontally to reveal actions that sit behind it.
 *
 * The actions stay in place and are uncovered as the content moves aside. Releasing the content
 * between two positions settles it at the closer one, taking the fling velocity into account.
 *
 * The layout takes the size of the incoming constraints and measures [content] to exactly that
 * size. The actions are measured with the same height and as wide as they need to be. The content
 * is drawn on top of the actions, so it must be opaque for them to be hidden. The actions are
 * always composed, even while they are covered. In a right-to-left layout the swipe direction is
 * mirrored.
 *
 * ## Accessibility
 *
 * A swipe is hard or impossible to perform with a screen reader or a switch, so apply
 * [SwipeToRevealContentScope.revealSemantics] to the root of the [content] to offer custom actions
 * that reveal and hide the actions instead. Without it the [content] has no such actions. The
 * actions that are covered by the content are hidden from accessibility services, so they only
 * become reachable once they are fully revealed. Describe everything with [labels], as the library
 * does not translate any texts.
 *
 * @param modifier The modifier applied to the layout.
 * @param startActions The actions revealed by swiping the content towards the end, or `null` if
 * the content cannot be swiped to that side. The slot is measured with a fixed height, so it
 * should arrange multiple actions itself, for example in a `Row`.
 * @param endActions The actions revealed by swiping the content towards the start, or `null` if
 * the content cannot be swiped to that side. The slot is measured with a fixed height, so it
 * should arrange multiple actions itself, for example in a `Row`.
 * @param state The state to observe and control the revealed actions.
 * @param group Coordinates this layout with others, or `null` to let it act on its own. While this
 * layout is revealed, the actions of all other layouts in the same [SwipeToRevealGroup] are hidden.
 * @param enabled Whether the content can be swiped by the user. The state can still be changed from
 * code while this is `false`. The accessibility actions that reveal and hide are not offered then.
 * @param labels The texts accessibility services use to describe the layout.
 * @param content The content that covers the actions. Apply
 * [SwipeToRevealContentScope.revealSemantics] to its root to make the swipe accessible.
 */
@Composable
public fun SwipeToReveal(
    modifier: Modifier = Modifier,
    startActions: (@Composable () -> Unit)? = null,
    endActions: (@Composable () -> Unit)? = null,
    state: SwipeToRevealState = rememberSwipeToRevealState(),
    group: SwipeToRevealGroup? = null,
    enabled: Boolean = true,
    labels: SwipeToRevealLabels = SwipeToRevealLabels(),
    content: @Composable SwipeToRevealContentScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val contentScope = remember(state, labels, startActions != null, endActions != null, enabled) {
        SwipeToRevealContentScope(
            state = state,
            labels = labels,
            hasStartActions = startActions != null,
            hasEndActions = endActions != null,
            enabled = enabled,
            coroutineScope = coroutineScope,
        )
    }

    group?.let { group ->
        val key = remember {
            Uuid.random()
        }

        DisposableEffect(group, state, key) {
            group.register(key, state)

            onDispose {
                group.unregister(key)
            }
        }

        LaunchedEffect(state, group, key) {
            snapshotFlow { state.isRevealed }
                .filter { it }
                .collect {
                    group.hideOther(key)
                }
        }
    }

    Layout(
        modifier = modifier,
        measurePolicy = rememberSwipeToRevealMeasurePolicy(state = state),
        content = {
            if (startActions != null) {
                Box(
                    modifier = Modifier
                        .semantics {
                            if (state.draggableState.settledValue !=
                                RevealState.StartActionsVisible
                            ) {
                                hideFromAccessibility()
                            }
                        }
                        .layoutId(SwipeToRevealLayoutId.StartActions),
                ) {
                    startActions()
                }
            }

            if (endActions != null) {
                Box(
                    modifier = Modifier
                        .semantics {
                            if (state.draggableState.settledValue !=
                                RevealState.EndActionsVisible
                            ) {
                                hideFromAccessibility()
                            }
                        }
                        .layoutId(SwipeToRevealLayoutId.EndActions),
                ) {
                    endActions()
                }
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Horizontal,
                        enabled = enabled,
                        reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                    )
                    .layoutId(SwipeToRevealLayoutId.MainContent),
            ) {
                contentScope.content()
            }
        },
    )
}
