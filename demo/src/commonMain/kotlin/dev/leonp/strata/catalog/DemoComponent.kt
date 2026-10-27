package dev.leonp.strata.catalog

/** The library components that have demos. The home screen is grouped by them. */
enum class DemoComponent(val title: String, val description: String) {
    CollapsibleHeader(
        title = "Collapsible header",
        description = "A header above the content that collapses and expands with scroll and drag gestures.",
    ),
    ExpandableContent(
        title = "Expandable content",
        description = "A bottom layout that is dragged from a compact bar into a full screen, like a mini player.",
    ),
}
