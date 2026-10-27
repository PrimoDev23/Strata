package dev.leonp.strata.navigation

import androidx.navigation3.runtime.NavKey
import dev.leonp.strata.catalog.DemoCategory
import dev.leonp.strata.catalog.DemoComponent
import dev.leonp.strata.catalog.DemoSample
import kotlinx.serialization.Serializable

/** The destinations of the demo app. They are serializable, so the back stack can be saved. */
@Serializable
sealed interface Route : NavKey {
    /** The list of all components, each with its sub menus. */
    @Serializable
    data object Home : Route

    /** The samples of [component] in the sub menu [category]. */
    @Serializable
    data class Category(val component: DemoComponent, val category: DemoCategory) : Route

    /** A single [sample]. */
    @Serializable
    data class Sample(val sample: DemoSample) : Route
}
