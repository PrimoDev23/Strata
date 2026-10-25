package dev.leonp.strata

import androidx.compose.runtime.Composable
import dev.leonp.strata.collapsible.CollapsibleHeaderScreen
import dev.leonp.strata.theme.StrataTheme

@Composable
fun App() {
    StrataTheme {
        CollapsibleHeaderScreen()
    }
}
