package dev.leonp.strata.draggable.bottombar

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope

/**
 * The scope of the content of [ExpandableContent].
 *
 * Combines the [SharedTransitionScope] and the [AnimatedVisibilityScope] of the content, so it can
 * use `Modifier.animateEnterExit`, `Modifier.sharedElement` and `Modifier.sharedBounds` directly.
 * Pass this scope as the `animatedVisibilityScope` of shared elements.
 *
 * The [AnimatedVisibilityScope] follows the transition between the collapsed and the expanded
 * state, which is driven by the drag position of [ExpandableContent].
 *
 * The content is also composed as a hidden copy that is only used to measure the collapsed
 * height. In that copy, the scopes belong to a separate, always visible transition, so shared
 * elements do not interact with the visible content.
 */
class ExpandableContentScope internal constructor(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) : SharedTransitionScope by sharedTransitionScope,
    AnimatedVisibilityScope by animatedVisibilityScope
