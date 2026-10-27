package dev.leonp.strata.catalog

/** The sub menus of every [DemoComponent]. */
enum class DemoCategory(val title: String, val description: String) {
    Animated(
        title = "Animated",
        description = "Transitions that follow the drag position",
    ),
    NonAnimated(
        title = "Non-animated",
        description = "States switch without a transition",
    ),
    Configuration(
        title = "Configurations",
        description = "Insets, content types and combinations",
    ),
}
