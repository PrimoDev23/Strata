package dev.leonp.strata.catalog

import dev.leonp.strata.catalog.DemoCategory.Animated
import dev.leonp.strata.catalog.DemoCategory.Configuration
import dev.leonp.strata.catalog.DemoCategory.NonAnimated
import dev.leonp.strata.catalog.DemoComponent.AnimatedColumn
import dev.leonp.strata.catalog.DemoComponent.CollapsibleHeader
import dev.leonp.strata.catalog.DemoComponent.ExpandableContent

/** Every demo of the app, sorted into a [DemoComponent] and one of its [DemoCategory] sub menus. */
enum class DemoSample(
    val component: DemoComponent,
    val category: DemoCategory,
    val title: String,
    val description: String,
) {
    HeaderSharedTitle(
        component = CollapsibleHeader,
        category = Animated,
        title = "Shared title",
        description = "The title moves between both headers as a shared element while the subtitle fades out.",
    ),
    HeaderCrossfade(
        component = CollapsibleHeader,
        category = Animated,
        title = "Crossfade",
        description = "Two completely different headers crossfade along the drag.",
    ),
    HeaderAnimatedWithoutCollapsed(
        component = CollapsibleHeader,
        category = Animated,
        title = "Without collapsed header",
        description = "Collapses down to the status bar. The header keeps its natural height and is clipped.",
    ),
    HeaderSwitching(
        component = CollapsibleHeader,
        category = NonAnimated,
        title = "Switching headers",
        description = "Switches to the collapsed header halfway through the drag. The header is " +
            "squeezed to the current height.",
    ),
    HeaderWithoutCollapsed(
        component = CollapsibleHeader,
        category = NonAnimated,
        title = "Without collapsed header",
        description = "Collapses down to the status bar. The header is squeezed, so the title stays at its bottom.",
    ),
    HeaderNonScrollable(
        component = CollapsibleHeader,
        category = Configuration,
        title = "Non-scrollable content",
        description = "Drag the content itself to collapse or expand the header.",
    ),
    HeaderShortList(
        component = CollapsibleHeader,
        category = Configuration,
        title = "Short list",
        description = "A list that fits on the screen still collapses the header when dragged.",
    ),
    HeaderBelowTopAppBar(
        component = CollapsibleHeader,
        category = Configuration,
        title = "Below a top app bar",
        description = "Inside a Scaffold, only the navigation bar insets are left, so the header " +
            "collapses to zero height.",
    ),
    ExpandablePlayer(
        component = ExpandableContent,
        category = Animated,
        title = "Music player",
        description = "Artwork and titles move between the mini and the full player while the controls fade.",
    ),
    ExpandableFadeThrough(
        component = ExpandableContent,
        category = Animated,
        title = "Fade through",
        description = "The collapsed content fades out at the start of the drag, the expanded " +
            "content fades in at its end.",
    ),
    ExpandableInstantSwitch(
        component = ExpandableContent,
        category = NonAnimated,
        title = "Instant switch",
        description = "Without animations, the target state is drawn on top as soon as the drag starts.",
    ),
    ExpandableAboveNavigationBar(
        component = ExpandableContent,
        category = Configuration,
        title = "Above a navigation bar",
        description = "Expands into the area above a bottom navigation bar.",
    ),
    ExpandableWithCollapsibleHeader(
        component = ExpandableContent,
        category = Configuration,
        title = "With a collapsible header",
        description = "Both components on one screen, each with its own shared elements.",
    ),
    ExpandableScrollableContent(
        component = ExpandableContent,
        category = Configuration,
        title = "Scrollable expanded content",
        description = "The expanded state contains a list that scrolls on its own. Drag the top part to collapse.",
    ),
    AnimatedColumnReorder(
        component = AnimatedColumn,
        category = Animated,
        title = "Reordering",
        description = "Shuffle, reverse or move items. The children animate to their new place.",
    ),
    ;

    companion object {
        /** All samples of [component] in the sub menu [category]. */
        fun of(component: DemoComponent, category: DemoCategory): List<DemoSample> =
            entries.filter { it.component == component && it.category == category }
    }
}
