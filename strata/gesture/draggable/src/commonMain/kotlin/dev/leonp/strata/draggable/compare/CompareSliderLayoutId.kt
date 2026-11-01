package dev.leonp.strata.draggable.compare

/** Identifies the slots of the [CompareSlider] layout, so its measure policy can find them. */
internal enum class CompareSliderLayoutId {
    /** The content left of the slider. */
    Before,

    /** The content right of the slider. */
    After,

    /** The line that separates [Before] and [After]. */
    SliderLine,

    /** The draggable handle on top of the [SliderLine]. */
    SliderThumb,
}