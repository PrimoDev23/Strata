package dev.leonp.strata.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.leonp.strata.category.CategoryScreen
import dev.leonp.strata.home.HomeScreen
import dev.leonp.strata.sample.SampleScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/** Non-JVM targets cannot look up serializers by reflection, so every route is registered here. */
private val RouteSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Route.Home::class, Route.Home.serializer())
            subclass(Route.Category::class, Route.Category.serializer())
            subclass(Route.Sample::class, Route.Sample.serializer())
        }
    }
}

/** Hosts the back stack of the demo app, starting at [Route.Home]. */
@Composable
fun StrataNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(RouteSavedStateConfiguration, Route.Home)
    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeScreen(
                    onCategoryClick = { component, category ->
                        backStack.add(Route.Category(component = component, category = category))
                    },
                )
            }

            entry<Route.Category> { route ->
                CategoryScreen(
                    component = route.component,
                    category = route.category,
                    onSampleClick = { sample -> backStack.add(Route.Sample(sample = sample)) },
                    onBack = onBack,
                )
            }

            entry<Route.Sample> { route ->
                SampleScreen(
                    sample = route.sample,
                    onBack = onBack,
                )
            }
        },
    )
}
