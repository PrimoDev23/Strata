package dev.leonp.strata.draggable.header.animatable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.common.DraggableLayoutTransitionEffect
import dev.leonp.strata.draggable.header.CollapsibleHeaderContainer
import dev.leonp.strata.draggable.header.base.BaseCollapsibleHeaderContainer
import dev.leonp.strata.draggable.header.common.CollapsibleHeaderContainerState
import dev.leonp.strata.draggable.header.common.InsetSpacer
import dev.leonp.strata.draggable.header.common.rememberCollapsibleHeaderContainerState

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
/**
 * A container with a header above [content] that collapses and expands with the user's scroll
 * and drag gestures, animating between [expandedHeader] and [collapsedHeader].
 *
 * Behaves like [CollapsibleHeaderContainer], but instead of switching between both headers, it
 * runs a transition between them that is driven by the drag position: dragging halfway seeks the
 * transition to its middle, and releasing settles both the header height and the transition.
 * Both headers are composed while a gesture is in progress. Use the [AnimatableCollapsibleHeaderScope]
 * of the header slots to animate between them, for example:
 * - `Modifier.sharedElement` or `Modifier.sharedBounds` for elements that move between both
 *   headers, such as a title,
 * - `Modifier.animateEnterExit` for elements that only exist in one header, such as a subtitle.
 *
 * The transition itself does not fade or scale the headers. Both headers are laid out at their
 * natural height, aligned to the top and clipped to the current header height. While both are
 * composed, the header that is being transitioned to is drawn on top.
 *
 * Animations are mapped linearly onto the drag position based on the total duration of the
 * transition, which is the duration of its longest animation. Prefer `tween` specs with explicit
 * durations over springs, whose duration is only estimated.
 *
 * ## Header measurement
 *
 * The collapsed and expanded heights are measured from the header slots themselves. To know both
 * heights at any time, **each header slot is composed more than once at the same time**: one hidden
 * copy per state is composed and measured but never placed, and further copies are the visible,
 * animated headers. This has a few consequences for the header slots:
 * - Side effects run once per copy. Do not use composition or effects such as `LaunchedEffect`
 *   for impression tracking or analytics. Use visibility-based APIs or the settled state instead.
 * - Each copy has its own `remember` and `rememberSaveable` state. The size of a header must only
 *   depend on its parameters, otherwise the measured heights do not match the visible header.
 *   Hoist any state that changes the header size.
 * - Expensive work such as loading images is done once per copy.
 * - The hidden copies are excluded from the semantics tree, so tests and accessibility services
 *   only see the visible header.
 * - The hidden copies use their own [SharedTransitionScope], so shared element keys do not clash
 *   with the visible headers.
 *
 * ## Insets
 *
 * The container consumes [insets] and hands them to its slots as [PaddingValues]:
 * - the header slots receive the top and horizontal insets,
 * - [content] receives the bottom and horizontal insets.
 *
 * Apply the given [PaddingValues] in the slots, for example as padding inside the header
 * background or as `contentPadding` of a lazy list. Because the insets are consumed, inset
 * modifiers such as `Modifier.windowInsetsPadding` inside the slots do not apply them again.
 *
 * @receiver the [SharedTransitionScope] used for shared elements between both headers, for example
 *   from a `SharedTransitionLayout` around the container.
 * @param expandedHeader the header shown while the container is expanded. Receives the top and
 *   horizontal [insets] as [PaddingValues].
 * @param modifier the [Modifier] applied to the container.
 * @param state the [CollapsibleHeaderContainerState] that holds the collapsed or expanded state.
 * @param collapsedHeader the header shown while the container is collapsed. Receives the top and
 *   horizontal [insets] as [PaddingValues]. If `null`, the header collapses down to the top
 *   [insets] and [expandedHeader] stays visible while collapsing.
 * @param insets the window insets handled by the container. They are consumed for all descendants.
 * @param content the content below the header. It is measured with the remaining height and
 *   receives the bottom and horizontal [insets] as [PaddingValues].
 */
@Suppress("ContentSlotReused")
@Composable
public fun SharedTransitionScope.AnimatableCollapsibleHeaderContainer(
    expandedHeader: @Composable AnimatableCollapsibleHeaderScope.(PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderContainerState = rememberCollapsibleHeaderContainerState(),
    collapsedHeader: (@Composable AnimatableCollapsibleHeaderScope.(PaddingValues) -> Unit)? = null,
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit,
) {
    DraggableLayoutTransitionEffect(
        draggableState = state.draggableState,
        transitionState = state.transitionState,
    )

    val transition = rememberTransition(state.transitionState)

    BaseCollapsibleHeaderContainer(
        modifier = modifier,
        header = { headerState ->
            val headerInsets = insets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            val headerPadding = headerInsets.asPaddingValues()

            if (isMeasuring) {
                DummyAnimatedCollapsibleHeaderScope {
                    when (headerState) {
                        DraggableLayoutState.Collapsed -> collapsedHeader?.invoke(
                            this,
                            headerPadding,
                        ) ?: InsetSpacer(insets = headerInsets)

                        DraggableLayoutState.Expanded -> expandedHeader(headerPadding)
                    }
                }
            } else {
                transition.AnimatedContent(
                    transitionSpec = {
                        (EnterTransition.None togetherWith ExitTransition.None) using null
                    },
                ) { animatedHeaderState ->
                    val scope = remember {
                        AnimatableCollapsibleHeaderScope(
                            sharedTransitionScope = this@AnimatableCollapsibleHeaderContainer,
                            animatedVisibilityScope = this,
                        )
                    }

                    // Lay out each header at its natural height instead of the current dragged height,
                    // so the lookahead pass sees the real target layout for shared elements.
                    Box(
                        modifier = Modifier.wrapContentHeight(
                            align = Alignment.Top,
                            unbounded = true,
                        ),
                    ) {
                        with(scope) {
                            when (animatedHeaderState) {
                                DraggableLayoutState.Collapsed -> collapsedHeader?.invoke(
                                    this,
                                    headerPadding,
                                ) ?: expandedHeader(headerPadding)

                                DraggableLayoutState.Expanded -> expandedHeader(headerPadding)
                            }
                        }
                    }
                }
            }
        },
        state = state,
        insets = insets,
        content = content,
    )
}

// TODO: This is duplicated for now
@Composable
private fun DummyAnimatedCollapsibleHeaderScope(
    content: @Composable AnimatableCollapsibleHeaderScope.() -> Unit,
) {
    SharedTransitionScope { modifier ->
        AnimatedVisibility(
            modifier = modifier,
            visible = true,
        ) {
            val scope = remember(this@SharedTransitionScope, this) {
                AnimatableCollapsibleHeaderScope(
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this,
                )
            }

            scope.content()
        }
    }
}
