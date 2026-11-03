package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.common.takeOrElse

public object SwipeToConfirmDefaults {
    public val shape: Shape = CircleShape
    public const val CONFIRMATION_THRESHOLD: Float = 0.5f
    public val contentPadding: PaddingValues = PaddingValues(8.dp)

    private val defaultColors = SwipeToConfirmColors()

    public fun colors(): SwipeToConfirmColors = defaultColors

    public fun colors(
        backgroundColor: Color = Color.Unspecified,
        progressColor: Color = Color.Unspecified,
    ): SwipeToConfirmColors = defaultColors.copy(
        backgroundColor = backgroundColor.takeOrElse(defaultColors.backgroundColor),
        progressColor = progressColor.takeOrElse(defaultColors.progressColor),
    )
}

@Immutable
@ConsistentCopyVisibility
public data class SwipeToConfirmColors internal constructor(
    val backgroundColor: Color = Color.Unspecified,
    val progressColor: Color = Color.Unspecified,
)