package dev.leonp.strata.animated

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch

internal class FadeElement(
    private val removed: Boolean,
    private val onRemovalFinish: () -> Unit,
    private val fadeSpecs: AnimatedColumnScope.FadeSpecs?,
) : ModifierNodeElement<FadeNode>() {
    override fun create(): FadeNode = FadeNode(
        removed = removed,
        onRemovalFinish = onRemovalFinish,
        fadeSpecs = fadeSpecs,
    )

    override fun update(node: FadeNode) = node.update(
        removed = removed,
        onRemovalFinish = onRemovalFinish,
        fadeSpecs = fadeSpecs,
    )

    override fun InspectorInfo.inspectableProperties() = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FadeElement) return false

        return removed == other.removed &&
            onRemovalFinish == other.onRemovalFinish &&
            fadeSpecs == other.fadeSpecs
    }

    override fun hashCode(): Int {
        var result = removed.hashCode()
        result = 31 * result + onRemovalFinish.hashCode()
        result = 31 * result + fadeSpecs.hashCode()
        return result
    }
}

/**
 * Fades the item in and out and holds a removed item at zero height until it faded out.
 *
 * Without [fadeSpecs] the item is shown at once, and a removed item is dropped right away.
 */
internal class FadeNode(
    var removed: Boolean,
    var onRemovalFinish: () -> Unit,
    var fadeSpecs: AnimatedColumnScope.FadeSpecs?,
) : Modifier.Node(),
    LayoutModifierNode {
    private val animatable = Animatable(
        initialValue = if (removed || fadeSpecs == null) {
            1f
        } else {
            0f
        },
    )

    override fun onAttach() {
        animate()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val height = if (removed) {
            0
        } else {
            placeable.height
        }

        return layout(placeable.width, height) {
            placeable.placeWithLayer(
                x = 0,
                y = 0,
                layerBlock = {
                    alpha = animatable.value
                },
            )
        }
    }

    fun update(
        removed: Boolean,
        onRemovalFinish: () -> Unit,
        fadeSpecs: AnimatedColumnScope.FadeSpecs?,
    ) {
        this.onRemovalFinish = onRemovalFinish
        this.fadeSpecs = fadeSpecs

        if (this.removed == removed) {
            return
        }

        this.removed = removed
        invalidateMeasurement()
        animate()
    }

    private fun animate() {
        coroutineScope.launch {
            val specs = fadeSpecs
            if (specs == null) {
                animatable.snapTo(1f)
            } else {
                animatable.animateTo(
                    targetValue = removed.toTargetAlpha(),
                    animationSpec = if (removed) {
                        specs.exitSpec
                    } else {
                        specs.enterSpec
                    },
                )
            }

            if (removed) {
                onRemovalFinish()
            }
        }
    }

    private fun Boolean.toTargetAlpha() = if (this) {
        0f
    } else {
        1f
    }
}