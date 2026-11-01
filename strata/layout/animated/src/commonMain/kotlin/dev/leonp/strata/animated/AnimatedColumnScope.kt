package dev.leonp.strata.animated

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.IntOffset
import dev.leonp.strata.animated.AnimatedColumnScope.FadeSpecs

/** The scope of the item content of [AnimatedColumn]. */
public interface AnimatedColumnScope {
    /**
     * Animates this item when [AnimatedColumn] changes.
     *
     * Apply it to the outermost modifier of the item. An item that is not animated moves and
     * disappears without a transition.
     *
     * @param placementSpec The animation of the item to its new position. `null` moves the item
     * without an animation.
     * @param fadeSpecs The animations of the item fading in when it is added and fading out when it
     * is removed. `null` shows and removes the item without a fade.
     */
    public fun Modifier.animateItem(
        placementSpec: FiniteAnimationSpec<IntOffset>? = spring(),
        fadeSpecs: FadeSpecs? = FadeSpecs(),
    ): Modifier

    /**
     * The animations of an item fading in and out.
     *
     * @property enterSpec The animation of an added item fading in.
     * @property exitSpec The animation of a removed item fading out. The item keeps its place in the
     * column until it is gone.
     */
    public data class FadeSpecs(
        val enterSpec: FiniteAnimationSpec<Float> = spring(),
        val exitSpec: FiniteAnimationSpec<Float> = spring(),
    )
}

/** Applies the placement and fade animations of an item of [AnimatedColumn]. */
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
        // Also applied without fadeSpecs, because the item is dropped from the column in here
        .then(
            other = FadeElement(
                removed = removed,
                onRemovalFinish = onRemovalFinish,
                fadeSpecs = fadeSpecs,
            ),
        )
}
