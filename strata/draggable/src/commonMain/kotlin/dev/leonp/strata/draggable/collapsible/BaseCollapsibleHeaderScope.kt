package dev.leonp.strata.draggable.collapsible

internal sealed interface BaseCollapsibleHeaderScope {
    val isMeasuring: Boolean

    data object MeasurementScope : BaseCollapsibleHeaderScope {
        override val isMeasuring: Boolean = true
    }

    data object PlacementScope : BaseCollapsibleHeaderScope {
        override val isMeasuring: Boolean = false
    }
}