package dev.leonp.strata.draggable.reveal

/**
 * The positions a [SwipeToReveal] can rest at.
 *
 * [StartActionsVisible] and [EndActionsVisible] only exist while the matching actions slot is
 * provided. Without it the content cannot be dragged to that side.
 */
public enum class RevealState {
    /** The content is moved aside so that the start actions are fully visible. */
    StartActionsVisible,

    /** The content is moved aside so that the end actions are fully visible. */
    EndActionsVisible,

    /** The content covers the actions on both sides. */
    NoActionsVisible,
}
