package dev.leonp.strata.draggable.header.base

/** The layout ids of the children of [BaseCollapsibleHeaderContainer]. */
internal enum class CollapsibleHeaderLayoutId {
    /** The hidden copy of the collapsed header, only measured. */
    CollapsedHeader,

    /** The hidden copy of the expanded header, only measured. */
    ExpandedHeader,

    /** The visible header. */
    Header,

    /** The content below the header. */
    Content
}