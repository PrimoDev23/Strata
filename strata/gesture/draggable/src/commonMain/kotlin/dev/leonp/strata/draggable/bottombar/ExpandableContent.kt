package dev.leonp.strata.draggable.bottombar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.clearAndSetSemantics
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.common.DraggableLayoutTransitionEffect

// The content slot is intentionally composed multiple times at once: a hidden copy is used to
// measure the collapsed height while another copy is the visible content.
/**
 * A layout that can be dragged between a collapsed and an expanded state, like a player bar that
 * expands into a full screen player.
 *
 * The height of the layout follows the drag position continuously:
 * - [DraggableLayoutState.Collapsed]: the natural height of the collapsed content.
 * - [DraggableLayoutState.Expanded]: the full available height.
 *
 * Only the height changes, so place the layout at the bottom of a full size parent, for example
 * with `Modifier.align(Alignment.BottomCenter)` in a `Box`, to let it grow upwards. Dragging the
 * content collapses or expands the layout, and when a drag ends between both states, the layout
 * settles at one of them, based on the position and the velocity of the drag.
 *
 * ## Content
 *
 * [content] is called with the state it should show. Each state is laid out at its natural size
 * within the full available height, aligned to the top, and clipped to the current height. Content
 * that fills the available height, such as `Modifier.fillMaxSize`, is therefore laid out with the
 * full expanded height even while the layout is still growing.
 *
 * The layout does not draw a background. Apply the background or surface of the layout with
 * [modifier], so it grows with the drag right away, and only draw the actual content in [content].
 *
 * ## Animations
 *
 * While a drag is in progress, both states are composed and a transition between them is seeked by
 * the drag position. The states themselves are not animated by the layout. Use the
 * [ExpandableContentScope] to animate the content, for example:
 * - `Modifier.animateEnterExit` to fade content in or out at a certain point of the drag,
 * - `Modifier.sharedElement` or `Modifier.sharedBounds` for elements that move between both states.
 *
 * While both states are composed, the state that is being transitioned to is drawn on top.
 *
 * Seeking maps the drag progress linearly onto the total duration of the transition, which is the
 * end time of its longest animation. Durations and delays therefore define at which point of the
 * drag an animation runs, not how long it takes. For example, with a total duration of `1000` ms,
 * `fadeIn(tween(durationMillis = 200, delayMillis = 800))` fades the content in during the last
 * 20 % of the drag. Use `tween` specs with explicit durations on one shared timeline, because the
 * duration of springs is only estimated and silently changes the total duration.
 *
 * ## Collapsed height measurement
 *
 * The collapsed height is measured from [content] itself. To know it at any time, **[content] is
 * composed more than once at the same time**: a hidden copy of the collapsed state is always
 * composed and measured, but never placed, and further copies are the visible states. This has a
 * few consequences for [content]:
 * - Side effects run once per copy. Do not use composition or effects such as `LaunchedEffect`
 *   for impression tracking or analytics. Use visibility-based APIs or the settled state instead.
 * - Each copy has its own `remember` and `rememberSaveable` state. The size of the collapsed
 *   content must only depend on its parameters, otherwise the measured height does not match the
 *   visible content. Hoist any state that changes its size.
 * - Expensive work such as loading images is done once per copy.
 * - The hidden copy uses its own [SharedTransitionScope], so shared element keys do not clash with
 *   the visible states.
 *
 * @receiver the [SharedTransitionScope] used for shared elements between both states, for example
 *   from a `SharedTransitionLayout` around the layout.
 * @param modifier the [Modifier] applied to the layout. The layout clips its content to its bounds.
 * @param state the [ExpandableContentState] that holds the collapsed or expanded state.
 * @param content the content for the given state, called in an [ExpandableContentScope].
 */
@Suppress("ContentSlotReused")
@Composable
public fun SharedTransitionScope.ExpandableContent(
    modifier: Modifier = Modifier,
    state: ExpandableContentState = rememberExpandableContentState(),
    content: @Composable ExpandableContentScope.(DraggableLayoutState) -> Unit,
) {
    DraggableLayoutTransitionEffect(
        draggableState = state.draggableState,
        transitionState = state.transitionState,
    )

    val transition = rememberTransition(state.transitionState)

    Layout(
        modifier = modifier.clipToBounds(),
        measurePolicy = rememberExpandableContentMeasurePolicy(state = state),
        content = {
            Box(
                modifier = Modifier
                    .layoutId(ExpandableContentLayoutId.Collapsed)
                    .clearAndSetSemantics {},
            ) {
                DummyExpandableContentScope {
                    content(DraggableLayoutState.Collapsed)
                }
            }

            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Vertical,
                    )
                    .layoutId(ExpandableContentLayoutId.Current),
            ) {
                transition.AnimatedContent(
                    transitionSpec = {
                        // The caller animates its content with animateEnterExit on the
                        // ExpandableContentScope, so the states themselves are not animated.
                        (EnterTransition.None togetherWith ExitTransition.None) using null
                    },
                ) { layoutState ->
                    val scope = remember(this@ExpandableContent, this) {
                        ExpandableContentScope(
                            sharedTransitionScope = this@ExpandableContent,
                            animatedVisibilityScope = this,
                        )
                    }

                    scope.content(layoutState)
                }
            }
        },
    )
}

/**
 * Provides an [ExpandableContentScope] for the hidden measurement copy, backed by its own
 * [SharedTransitionScope] and an always visible [AnimatedVisibility].
 */
@Composable
private fun DummyExpandableContentScope(content: @Composable ExpandableContentScope.() -> Unit) {
    SharedTransitionScope { modifier ->
        AnimatedVisibility(
            modifier = modifier,
            visible = true,
        ) {
            val scope = remember(this@SharedTransitionScope, this) {
                ExpandableContentScope(
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this,
                )
            }

            scope.content()
        }
    }
}
