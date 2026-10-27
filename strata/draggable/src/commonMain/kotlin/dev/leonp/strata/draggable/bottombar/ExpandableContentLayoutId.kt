package dev.leonp.strata.draggable.bottombar

/** The layout ids of the children of [ExpandableContent]. */
internal enum class ExpandableContentLayoutId {
    /** The hidden copy of the collapsed content, only measured to determine the anchors. */
    Collapsed,

    /** The visible content for the current state, including both states during a transition. */
    Current,
}
