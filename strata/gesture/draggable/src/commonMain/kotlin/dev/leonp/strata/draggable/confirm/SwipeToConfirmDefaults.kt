package dev.leonp.strata.draggable.confirm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.common.takeOrElse

/** The default values of [SwipeToConfirm]. */
public object SwipeToConfirmDefaults {
    /** The default shape of the track, which is fully rounded. */
    public val shape: Shape = CircleShape

    /** The default fraction of the track the thumb has to be dragged to confirm. */
    public const val CONFIRMATION_THRESHOLD: Float = 0.5f

    /** The default space between the track and both the thumb and the content. */
    public val contentPadding: PaddingValues = PaddingValues(8.dp)

    private val defaultColors = SwipeToConfirmColors()

    /** The default colors, which draw neither a background nor a progress fill. */
    public fun colors(): SwipeToConfirmColors = defaultColors

    /**
     * Creates the colors of a [SwipeToConfirm]. A color that is [Color.Unspecified] is not drawn.
     *
     * @param backgroundColor The color of the whole track.
     * @param progressColor The color of the fill that grows behind the thumb. Use a translucent color
     *   to keep the content readable while the fill passes behind it.
     */
    public fun colors(
        backgroundColor: Color = Color.Unspecified,
        progressColor: Color = Color.Unspecified,
    ): SwipeToConfirmColors = defaultColors.copy(
        backgroundColor = backgroundColor.takeOrElse(defaultColors.backgroundColor),
        progressColor = progressColor.takeOrElse(defaultColors.progressColor),
    )
}

/**
 * The colors of a [SwipeToConfirm]. Create them with [SwipeToConfirmDefaults.colors].
 *
 * @property backgroundColor The color of the whole track.
 * @property progressColor The color of the fill that grows behind the thumb.
 */
@Immutable
@ConsistentCopyVisibility
public data class SwipeToConfirmColors internal constructor(
    val backgroundColor: Color = Color.Unspecified,
    val progressColor: Color = Color.Unspecified,
)
