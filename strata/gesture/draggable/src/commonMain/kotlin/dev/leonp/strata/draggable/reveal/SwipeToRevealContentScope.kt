package dev.leonp.strata.draggable.reveal

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The scope of the content of a [SwipeToReveal].
 *
 * It offers [revealSemantics] to make the swipe available to screen readers and switch access.
 */
@Immutable
public class SwipeToRevealContentScope internal constructor(
    private val state: SwipeToRevealState,
    private val labels: SwipeToRevealLabels,
    private val hasStartActions: Boolean,
    private val hasEndActions: Boolean,
    private val enabled: Boolean,
    private val coroutineScope: CoroutineScope,
) {
    /**
     * Offers custom accessibility actions that reveal and hide the actions of the [SwipeToReveal],
     * as a swipe is hard or impossible to perform with a screen reader or a switch. They are
     * described by the [SwipeToRevealLabels] of the layout, and the state description is set while
     * actions are revealed.
     *
     * Apply it to the root of the content, so the actions end up on the node that screen readers
     * focus. The library cannot do that for you: content that merges its own semantics, like a
     * `ListItem`, a `Button` or a clickable `Card`, is focused on its own, so actions attached to
     * a wrapper around it would never be offered. Without this modifier the content has no
     * accessibility actions to reveal the actions.
     *
     * The actions that reveal or hide are left out while the layout is disabled, as they stand in
     * for the swipe.
     */
    public fun Modifier.revealSemantics(): Modifier = semantics {
        applyRevealSemantics(
            state = state,
            labels = labels,
            hasStartActions = hasStartActions,
            hasEndActions = hasEndActions,
            enabled = enabled,
            coroutineScope = coroutineScope,
        )
    }
}

/**
 * Describes the content of a [SwipeToReveal] to accessibility services.
 *
 * The actions are launched in [coroutineScope], as the semantics callbacks are not suspending.
 */
private fun SemanticsPropertyReceiver.applyRevealSemantics(
    state: SwipeToRevealState,
    labels: SwipeToRevealLabels,
    hasStartActions: Boolean,
    hasEndActions: Boolean,
    enabled: Boolean,
    coroutineScope: CoroutineScope,
) {
    if (state.isRevealed && labels.revealedStateDescription != null) {
        stateDescription = labels.revealedStateDescription
    }

    if (!enabled) {
        return
    }

    customActions = buildList {
        addRevealAction(
            label = labels.revealStartActions.takeIf { hasStartActions },
            target = RevealState.StartActionsVisible,
            state = state,
            coroutineScope = coroutineScope,
        )
        addRevealAction(
            label = labels.revealEndActions.takeIf { hasEndActions },
            target = RevealState.EndActionsVisible,
            state = state,
            coroutineScope = coroutineScope,
        )
        addRevealAction(
            label = labels.hideActions.takeIf { state.isRevealed },
            target = RevealState.NoActionsVisible,
            state = state,
            coroutineScope = coroutineScope,
        )
    }
}

/** Adds an action that moves [state] to [target] unless [label] is `null` or it is settled there. */
private fun MutableList<CustomAccessibilityAction>.addRevealAction(
    label: String?,
    target: RevealState,
    state: SwipeToRevealState,
    coroutineScope: CoroutineScope,
) {
    if (label == null || state.settledValue == target) {
        return
    }

    add(
        CustomAccessibilityAction(
            label = label,
            action = {
                coroutineScope.launch {
                    state.animateToDefault(targetValue = target)
                }
                true
            },
        ),
    )
}
