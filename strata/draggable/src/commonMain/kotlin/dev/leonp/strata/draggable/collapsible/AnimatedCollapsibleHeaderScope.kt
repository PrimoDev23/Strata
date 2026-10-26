package dev.leonp.strata.draggable.collapsible

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope

data class AnimatedCollapsibleHeaderScope internal constructor(
    private val sharedTransitionScope: SharedTransitionScope,
    private val animatedVisibilityScope: AnimatedVisibilityScope
) : SharedTransitionScope by sharedTransitionScope,
    AnimatedVisibilityScope by animatedVisibilityScope