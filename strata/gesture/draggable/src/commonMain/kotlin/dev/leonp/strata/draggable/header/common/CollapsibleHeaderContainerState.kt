package dev.leonp.strata.draggable.header.common

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.draggable.header.CollapsibleHeaderContainer
import dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer

/**
 * The state of [CollapsibleHeaderContainer] and [AnimatableCollapsibleHeaderContainer].
 *
 * Holds the current header height, driven by scroll and drag gestures, and the transition between
 * the collapsed and the expanded header. Only the settled [DraggableLayoutState] is saved, so a
 * header that was restored while a gesture was in progress starts at its last settled state.
 *
 * Create it with [rememberCollapsibleHeaderContainerState].
 */
@Stable
public class CollapsibleHeaderContainerState internal constructor(
    initialValue: DraggableLayoutState,
) {
    internal val draggableState = AnchoredDraggableState(initialValue = initialValue)
    internal val transitionState = SeekableTransitionState(initialState = initialValue)

    public val isCollapsed: Boolean
        get() = draggableState.settledValue == DraggableLayoutState.Collapsed

    public suspend fun collapse(animationSpec: FiniteAnimationSpec<Float> = spring()) {
        draggableState.animateTo(
            targetValue = DraggableLayoutState.Collapsed,
            animationSpec = animationSpec,
        )
    }

    public suspend fun expand(animationSpec: FiniteAnimationSpec<Float> = spring()) {
        draggableState.animateTo(
            targetValue = DraggableLayoutState.Expanded,
            animationSpec = animationSpec,
        )
    }

    internal companion object {
        private const val DRAGGABLE_STATE_SETTLED_VALUE_KEY = "DraggableStateSettledValueKey"

        val Saver = mapSaver(
            save = {
                mapOf(DRAGGABLE_STATE_SETTLED_VALUE_KEY to it.draggableState.settledValue)
            },
            restore = {
                CollapsibleHeaderContainerState(
                    initialValue = it[DRAGGABLE_STATE_SETTLED_VALUE_KEY] as DraggableLayoutState,
                )
            },
        )
    }
}

/**
 * Creates and remembers a [CollapsibleHeaderContainerState] that starts expanded.
 *
 * The settled state survives configuration changes and process death.
 */
@Composable
public fun rememberCollapsibleHeaderContainerState(
    initialValue: DraggableLayoutState = DraggableLayoutState.Expanded,
): CollapsibleHeaderContainerState =
    rememberSaveable(saver = CollapsibleHeaderContainerState.Saver) {
        CollapsibleHeaderContainerState(initialValue = initialValue)
    }