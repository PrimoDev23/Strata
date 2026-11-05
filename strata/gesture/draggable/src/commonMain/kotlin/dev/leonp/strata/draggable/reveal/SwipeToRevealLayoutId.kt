package dev.leonp.strata.draggable.reveal

/** Identifies the slots of [SwipeToReveal] for its measure policy. */
internal enum class SwipeToRevealLayoutId {
    /** The actions behind the left edge of the content, revealed by swiping towards the end. */
    StartActions,

    /** The actions behind the right edge of the content, revealed by swiping towards the start. */
    EndActions,

    /** The draggable content that covers the actions. */
    MainContent,
}
