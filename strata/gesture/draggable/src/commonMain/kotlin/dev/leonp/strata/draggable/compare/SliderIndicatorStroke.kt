package dev.leonp.strata.draggable.compare

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The line between the two slots of a [CompareSlider].
 *
 * @param thickness the width of the line.
 * @param color the color of the line.
 */
@Immutable
public data class SliderIndicatorStroke(val thickness: Dp = 2.dp, val color: Color = Color.White)
