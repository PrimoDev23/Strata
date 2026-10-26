package dev.leonp.strata.draggable.collapsible

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.leonp.strata.theme.StrataTheme

// The header slots are intentionally composed multiple times at once: hidden copies are used to
// measure the collapsed and expanded heights while another copy is the visible header.
@Suppress("ContentSlotReused")
@Composable
fun SharedTransitionScope.AnimatableCollapsibleHeaderContainer(
    expandedHeader: @Composable AnimatedCollapsibleHeaderScope.(PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    state: CollapsibleHeaderContainerState = rememberCollapsibleHeaderContainerState(),
    collapsedHeader: (@Composable AnimatedCollapsibleHeaderScope.(PaddingValues) -> Unit)? = null,
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    CollapsibleHeaderStateTransitionEffect(state = state)

    val transition = rememberTransition(state.transitionState)

    BaseCollapsibleHeaderContainer(
        modifier = modifier,
        header = { headerState ->
            val topInsets = insets.only(WindowInsetsSides.Top)
            val topPadding = topInsets.asPaddingValues()

            if (isMeasuring) {
                DummyAnimatedCollapsibleHeaderScope {
                    when (headerState) {
                        CollapsibleHeaderState.Collapsed -> collapsedHeader?.invoke(
                            this,
                            topPadding
                        )
                            ?: InsetSpacer(insets = topInsets)

                        CollapsibleHeaderState.Expanded -> expandedHeader(topPadding)
                    }
                }
            } else {
                transition.AnimatedContent(
                    transitionSpec = {
                        (EnterTransition.None togetherWith ExitTransition.None) using null
                    }
                ) { animatedHeaderState ->
                    val scope = remember {
                        AnimatedCollapsibleHeaderScope(
                            sharedTransitionScope = this@AnimatableCollapsibleHeaderContainer,
                            animatedVisibilityScope = this
                        )
                    }

                    with(scope) {
                        when (animatedHeaderState) {
                            CollapsibleHeaderState.Collapsed -> collapsedHeader?.invoke(
                                this,
                                topPadding
                            )
                                ?: expandedHeader(topPadding)

                            CollapsibleHeaderState.Expanded -> expandedHeader(topPadding)
                        }
                    }
                }
            }
        },
        state = state,
        insets = insets,
        content = content
    )
}

@Composable
private fun DummyAnimatedCollapsibleHeaderScope(
    content: @Composable AnimatedCollapsibleHeaderScope.() -> Unit
) {
    SharedTransitionScope { modifier ->
        AnimatedVisibility(
            modifier = modifier,
            visible = true
        ) {
            val scope = remember(this@SharedTransitionScope, this) {
                AnimatedCollapsibleHeaderScope(
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this
                )
            }

            scope.content()
        }
    }
}

@Preview
@Composable
private fun CollapsibleHeaderContainerPreview() {
    StrataTheme {
        CollapsibleHeaderContainer(
            expandedHeader = { padding ->
                Text(
                    text = "Expanded header",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding)
                        .padding(all = 24.dp)
                )
            },
            collapsedHeader = { padding ->
                Text(
                    text = "Collapsed header",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding)
                        .padding(all = 16.dp)
                )
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) { padding ->
            LazyColumn(contentPadding = padding) {
                items(count = 30) { index ->
                    Text(
                        text = "Item $index",
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
            }
        }
    }
}