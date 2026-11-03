package dev.leonp.strata.draggable.confirm

/** The two states of a [SwipeToConfirmState]. */
public enum class SwipeState {
    /** The thumb rests at the start of the track. */
    Start,

    /** The thumb reached the end of the track and the action was confirmed. */
    Confirmed,
}
