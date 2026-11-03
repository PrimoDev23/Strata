package dev.leonp.strata.draggable.compare

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Holds the position of the slider of a [CompareSlider].
 *
 * The position is a fraction of the width of the layout, so it stays at the same relative place
 * when the layout is resized or rotated. Create the state with [rememberCompareSliderState] to keep
 * it across configuration changes and process death.
 *
 * @param initialFraction The position the slider starts at, where `0f` is the left edge and `1f`
 * the right edge.
 */
@Stable
public class CompareSliderState(private val initialFraction: Float) {
    /**
     * The current position of the slider, where `0f` is the left edge and `1f` the right edge.
     * It is read while measuring, so dragging relays out the slider without recomposing. The value
     * is always kept within that range, so dragging past an edge does not build up an overshoot
     * that has to be dragged back first.
     */
    public var horizontalFraction: Float by mutableFloatStateOf(initialFraction.coerceIn(0f..1f))
        private set

    internal var width by mutableFloatStateOf(Float.NaN)

    internal val horizontalOffset by derivedStateOf {
        calculateCenterBasedOffsetFromFraction(
            fraction = horizontalFraction,
            width = width,
        )
    }

    /** Moves the slider by the dragged distance. */
    internal val draggableState = DraggableState {
        val newOffset = horizontalOffset + it
        val fraction = (newOffset + (width / 2)) / width

        horizontalFraction = fraction.coerceIn(0f..1f)
    }

    /**
     * Moves the slider to [fraction] immediately.
     *
     * This does not cancel a running [animateTo], which continues from the new position on its
     * next frame.
     *
     * @param fraction The new position, where `0f` is the left edge and `1f` the right edge. Values
     * outside of that range are moved to the closest edge.
     */
    public fun snapTo(fraction: Float) {
        horizontalFraction = fraction.coerceIn(0f..1f)
    }

    /**
     * Animates the slider to [fraction] and suspends until it arrives.
     *
     * The animation is cancelled when the user starts dragging the thumb, when [animateTo] is called
     * again, and when the calling coroutine is cancelled. In each case the slider stays where it
     * currently is.
     *
     * @param fraction The target position, where `0f` is the left edge and `1f` the right edge.
     * Values outside of that range are moved to the closest edge.
     * @param animationSpec The [FiniteAnimationSpec] used to move the slider.
     */
    public suspend fun animateTo(
        fraction: Float,
        animationSpec: FiniteAnimationSpec<Float> = spring(),
    ) {
        draggableState.drag(dragPriority = MutatePriority.Default) {
            animate(
                initialValue = horizontalFraction,
                targetValue = fraction.coerceIn(0f..1f),
                animationSpec = animationSpec,
            ) { fraction, _ ->
                horizontalFraction = fraction
            }
        }
    }

    private fun calculateCenterBasedOffsetFromFraction(fraction: Float, width: Float): Float {
        val centerX = width / 2f

        return if (width.isNaN()) {
            0f
        } else {
            fraction * width - centerX
        }.coerceIn(-centerX..centerX)
    }

    internal companion object {
        private const val FRACTION_KEY = "Fraction"

        /** Saves the [horizontalFraction], so the slider keeps its position after process death. */
        internal val Saver = mapSaver(
            save = {
                mapOf(FRACTION_KEY to it.horizontalFraction)
            },
            restore = {
                CompareSliderState(it[FRACTION_KEY] as Float)
            },
        )
    }
}

/**
 * Creates a [CompareSliderState] that survives configuration changes and process death.
 *
 * @param initialFraction The position of the slider as a fraction of the width, where `0f` is the
 * left edge and `1f` the right edge. The slider starts in the middle by default.
 */
@Composable
public fun rememberCompareSliderState(initialFraction: Float = 0.5f): CompareSliderState =
    rememberSaveable(saver = CompareSliderState.Saver) {
        CompareSliderState(initialFraction = initialFraction)
    }
