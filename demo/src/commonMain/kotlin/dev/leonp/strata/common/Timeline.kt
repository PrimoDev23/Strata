package dev.leonp.strata.common

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

/**
 * The total duration of every drag driven transition in the samples. The transitions are seeked by
 * the drag progress, so durations and delays only define at which point of the drag an animation
 * runs. Springs are avoided, because their estimated duration would change this timeline.
 */
internal const val SEEK_DURATION_MILLIS = 1000

/** Fades in during the last [fraction] of the drag. */
internal fun fadeInAtEnd(fraction: Float = 1f): EnterTransition {
    val durationMillis = (SEEK_DURATION_MILLIS * fraction).toInt()

    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = SEEK_DURATION_MILLIS - durationMillis,
            easing = LinearEasing,
        ),
    )
}

/** Fades out during the first [fraction] of the drag. */
internal fun fadeOutAtStart(fraction: Float = 1f): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = (SEEK_DURATION_MILLIS * fraction).toInt(),
        easing = LinearEasing,
    ),
)

/** Moves shared elements linearly along the whole drag. */
internal val SeekBoundsTransform = BoundsTransform { _, _ ->
    tween(
        durationMillis = SEEK_DURATION_MILLIS,
        easing = LinearEasing,
    )
}
