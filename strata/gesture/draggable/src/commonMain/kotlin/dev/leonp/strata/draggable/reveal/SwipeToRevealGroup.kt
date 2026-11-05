package dev.leonp.strata.draggable.reveal

import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.uuid.Uuid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val HIDE_ANIMATION_DURATION_MILLIS = 300

/**
 * Coordinates multiple [SwipeToReveal]s so that at most one of them has its actions revealed.
 *
 * Pass the same group to every [SwipeToReveal] that should take part, for example all items of a
 * list. As soon as one of them settles revealed, the actions of all the others are hidden again.
 * A [SwipeToReveal] registers itself while it is in the composition, so items that scroll out of a
 * lazy list no longer take part until they are composed again.
 *
 * Create it with [rememberSwipeToRevealGroup].
 */
@Stable
public class SwipeToRevealGroup internal constructor() {
    /** The states of all participating [SwipeToReveal]s, identified by a key that is unique to each. */
    internal var items by mutableStateOf(emptyMap<Uuid, SwipeToRevealState>())
        private set

    /** Adds [state] under [key], replacing a state that was registered with the same key. */
    internal fun register(key: Uuid, state: SwipeToRevealState) {
        items += (key to state)
    }

    /** Removes the state that was registered under [key], so it is no longer hidden by others. */
    internal fun unregister(key: Uuid) {
        items = items.filterNot { it.key == key }
    }

    /**
     * Hides every registered state except the one registered under [key].
     *
     * The states are hidden one after another and the call suspends until the last animation
     * finished.
     */
    internal suspend fun hideOther(key: Uuid) {
        coroutineScope {
            items.forEach { (itemKey, state) ->
                if (key != itemKey) {
                    launch {
                        state.animateTo(
                            targetValue = RevealState.NoActionsVisible,
                            animationSpec = tween(durationMillis = HIDE_ANIMATION_DURATION_MILLIS),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates and remembers a [SwipeToRevealGroup].
 *
 * The group is not saved across configuration changes and process death. The settled positions of
 * the individual [SwipeToRevealState]s are, so a restored item stays revealed until another item
 * is revealed.
 */
@Composable
public fun rememberSwipeToRevealGroup(): SwipeToRevealGroup = remember {
    SwipeToRevealGroup()
}
