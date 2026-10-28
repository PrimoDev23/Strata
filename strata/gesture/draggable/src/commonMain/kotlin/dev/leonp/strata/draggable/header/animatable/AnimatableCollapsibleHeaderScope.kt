package dev.leonp.strata.draggable.header.animatable

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope

/**
 * The scope of the header slots of [AnimatableCollapsibleHeaderContainer].
 *
 * Combines the [SharedTransitionScope] and the [AnimatedVisibilityScope] of the header, so the
 * header content can use `Modifier.sharedElement`, `Modifier.sharedBounds` and
 * `Modifier.animateEnterExit` directly. Pass this scope as the `animatedVisibilityScope` of shared
 * elements.
 *
 * The [AnimatedVisibilityScope] follows the transition between the collapsed and the expanded
 * header, which is driven by the drag position of the container.
 *
 * Header slots are also composed as hidden copies that are only used to measure the header
 * heights. In those copies, the scopes belong to a separate, always visible transition, so shared
 * elements do not interact with the visible headers.
 */
public class AnimatableCollapsibleHeaderScope internal constructor(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) : SharedTransitionScope by sharedTransitionScope,
    AnimatedVisibilityScope by animatedVisibilityScope