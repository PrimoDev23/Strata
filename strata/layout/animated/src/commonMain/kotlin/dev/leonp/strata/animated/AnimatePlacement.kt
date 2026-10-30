package dev.leonp.strata.animated

import androidx.compose.animation.core.DeferredTargetAnimation
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ApproachLayoutModifierNode
import androidx.compose.ui.layout.ApproachMeasureScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

/**
 * Animates the placement of this layout from where it currently is to the position the lookahead
 * pass of [scope] measured for it.
 *
 * Shared by the animated layouts, so they only differ in how they place their children.
 */
internal fun Modifier.animatePlacement(
    scope: LookaheadScope,
    animationSpec: FiniteAnimationSpec<IntOffset>,
) = this then AnimatePlacementElement(
    scope = scope,
    animationSpec = animationSpec,
)

private class AnimatePlacementElement(
    private val scope: LookaheadScope,
    private val animationSpec: FiniteAnimationSpec<IntOffset>,
) : ModifierNodeElement<AnimatePlacementNode>() {
    override fun create(): AnimatePlacementNode = AnimatePlacementNode(
        scope = scope,
        animationSpec = animationSpec,
    )

    override fun update(node: AnimatePlacementNode) {
        node.scope = scope
        node.animationSpec = animationSpec
    }

    override fun InspectorInfo.inspectableProperties() = Unit

    // The element holds no properties, so all instances are interchangeable.
    override fun hashCode(): Int = this::class.hashCode()

    override fun equals(other: Any?): Boolean = other is AnimatePlacementElement
}

private class AnimatePlacementNode(
    var scope: LookaheadScope,
    var animationSpec: FiniteAnimationSpec<IntOffset>,
) : Modifier.Node(),
    ApproachLayoutModifierNode {
    private val animation = DeferredTargetAnimation(IntOffset.VectorConverter)

    override fun isMeasurementApproachInProgress(lookaheadSize: IntSize): Boolean = false
    override fun Placeable.PlacementScope.isPlacementApproachInProgress(
        lookaheadCoordinates: LayoutCoordinates,
    ): Boolean = !animation.isIdle

    override fun ApproachMeasureScope.approachMeasure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)

        return layout(
            width = placeable.width,
            height = placeable.height,
        ) {
            val coords = coordinates ?: return@layout placeable.place(x = 0, y = 0)

            with(scope) {
                val target = lookaheadScopeCoordinates.localLookaheadPositionOf(coords).round()
                val animated = animation.updateTarget(
                    target = target,
                    coroutineScope = coroutineScope,
                    animationSpec = animationSpec,
                )

                val current = lookaheadScopeCoordinates.localPositionOf(coords).round()

                placeable.place(animated - current)
            }
        }
    }
}
