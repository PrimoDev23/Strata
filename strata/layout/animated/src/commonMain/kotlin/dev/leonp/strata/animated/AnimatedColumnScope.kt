package dev.leonp.strata.animated

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.IntOffset
import dev.leonp.strata.animated.AnimatedColumnScope.FadeSpecs

public interface AnimatedColumnScope {
    public fun Modifier.animateItem(
        placementSpec: FiniteAnimationSpec<IntOffset>? = spring(),
        fadeSpecs: FadeSpecs? = FadeSpecs(),
    ): Modifier

    public data class FadeSpecs(
        val enterSpec: FiniteAnimationSpec<Float> = spring(),
        val exitSpec: FiniteAnimationSpec<Float> = spring(),
    )
}

internal class AnimatedColumnScopeImpl(
    private val parent: ColumnScope,
    private val lookaheadScope: LookaheadScope,
    private val handle: AttachmentHandle,
    private val removed: Boolean,
    private val onRemovalFinish: () -> Unit,
) : AnimatedColumnScope,
    ColumnScope by parent {
    override fun Modifier.animateItem(
        placementSpec: FiniteAnimationSpec<IntOffset>?,
        fadeSpecs: FadeSpecs?,
    ) = this
        .then(AttachmentElement(handle = handle))
        .then(
            other = placementSpec?.let {
                Modifier.animatePlacement(
                    scope = lookaheadScope,
                    animationSpec = it,
                )
            } ?: Modifier,
        )
        .then(
            other = fadeSpecs?.let {
                FadeElement(
                    removed = removed,
                    onRemovalFinish = onRemovalFinish,
                    fadeSpecs = it,
                )
            } ?: Modifier,
        )
}