package dev.leonp.strata.draggable.header.base

/**
 * The scope of the header slot of [BaseCollapsibleHeaderContainer], telling the header whether it
 * is composed for measurement or as the visible header.
 */
internal sealed interface BaseCollapsibleHeaderScope {
    /**
     * `true` if this composition is a hidden copy that is only used to measure the header and is
     * never placed or drawn, `false` if it is the visible header.
     */
    val isMeasuring: Boolean

    /** The scope of the hidden copies that are only measured to determine the anchors. */
    data object MeasurementScope : BaseCollapsibleHeaderScope {
        override val isMeasuring: Boolean = true
    }

    /** The scope of the visible header. */
    data object PlacementScope : BaseCollapsibleHeaderScope {
        override val isMeasuring: Boolean = false
    }
}