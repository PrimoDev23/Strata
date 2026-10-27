package dev.leonp.strata.draggable.common

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints

/**
 * Measures the first [Measurable] with the given [layoutId] with [constraints].
 *
 * @return the resulting [Placeable], or `null` if no [Measurable] has the given [layoutId].
 */
internal fun List<Measurable>.getPlaceableById(
    layoutId: Any?,
    constraints: Constraints
): Placeable? = this.find { it.layoutId == layoutId }?.measure(constraints)
