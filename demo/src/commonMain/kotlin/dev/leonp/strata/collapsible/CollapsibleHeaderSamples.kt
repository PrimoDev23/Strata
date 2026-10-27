package dev.leonp.strata.collapsible

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.InfoCard
import dev.leonp.strata.common.SampleList
import dev.leonp.strata.common.fadeInAtEnd
import dev.leonp.strata.common.fadeOutAtStart
import dev.leonp.strata.draggable.header.CollapsibleHeaderContainer
import dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer

@Composable
internal fun SharedTitleSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTitleContainer(
        title = "Shared title",
        subtitle = "Scroll the list to collapse this header",
        onBack = onBack,
        modifier = modifier,
    ) { padding ->
        SampleList(contentPadding = padding)
    }
}

@Composable
internal fun CrossfadeSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatableCollapsibleHeaderContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            // Both headers fade along the whole drag, so they crossfade into each other.
            expandedHeader = { padding ->
                HeroHeader(
                    title = "Crossfade",
                    subtitle = "Both headers fade along the drag",
                    onBack = onBack,
                    padding = padding,
                    modifier = Modifier.animateEnterExit(
                        enter = fadeInAtEnd(),
                        exit = fadeOutAtStart(),
                    ),
                )
            },
            collapsedHeader = { padding ->
                CollapsedTitleHeader(
                    title = "Crossfade",
                    onBack = onBack,
                    padding = padding,
                    modifier = Modifier.animateEnterExit(
                        enter = fadeInAtEnd(),
                        exit = fadeOutAtStart(),
                    ),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            },
        ) { padding ->
            SampleList(contentPadding = padding)
        }
    }
}

@Composable
internal fun AnimatedWithoutCollapsedHeaderSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatableCollapsibleHeaderContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            expandedHeader = { padding ->
                HeroHeader(
                    title = "Clipped",
                    subtitle = "The header keeps its natural height",
                    onBack = onBack,
                    padding = padding,
                )
            },
        ) { padding ->
            SampleList(contentPadding = padding)
        }
    }
}

@Composable
internal fun SwitchingHeadersSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    CollapsibleHeaderContainer(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        expandedHeader = { padding ->
            ExpandedTitleHeader(
                title = "Switching headers",
                subtitle = "The header switches halfway through the drag",
                onBack = onBack,
                padding = padding,
            )
        },
        collapsedHeader = { padding ->
            CollapsedTitleHeader(
                title = "Switching headers",
                onBack = onBack,
                padding = padding,
            )
        },
    ) { padding ->
        SampleList(contentPadding = padding)
    }
}

@Composable
internal fun WithoutCollapsedHeaderSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    CollapsibleHeaderContainer(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        expandedHeader = { padding ->
            HeroHeader(
                title = "Squeezed",
                subtitle = "The header takes the current height",
                onBack = onBack,
                padding = padding,
            )
        },
    ) { padding ->
        SampleList(contentPadding = padding)
    }
}

@Composable
internal fun NonScrollableContentSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTitleContainer(
        title = "Drag me",
        subtitle = "The content below is not scrollable",
        onBack = onBack,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoCard(
                title = "Drag anywhere",
                text = "Without a scrollable child, dragging the content collapses and expands the header directly.",
            )
            InfoCard(
                title = "Settling",
                text = "Releasing between both states settles at one of them, based on the position and the velocity.",
            )
            InfoCard(
                title = "Restoration",
                text = "The settled state is saved. Go back and open this sample again to see it restored.",
            )
        }
    }
}

@Composable
internal fun ShortListSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTitleContainer(
        title = "Short list",
        subtitle = "Three items fit on the screen",
        onBack = onBack,
        modifier = modifier,
    ) { padding ->
        SampleList(
            contentPadding = padding,
            itemCount = 3,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BelowTopAppBarSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Below a top app bar") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { scaffoldPadding ->
        SharedTransitionLayout(
            modifier = Modifier.padding(top = scaffoldPadding.calculateTopPadding()),
        ) {
            // The top app bar handles the status bar, so only the navigation bar is left. Without
            // top insets and a collapsed header, the header collapses to zero height.
            AnimatableCollapsibleHeaderContainer(
                modifier = Modifier.fillMaxSize(),
                expandedHeader = { padding -> PromoBanner(padding = padding) },
                insets = WindowInsets.navigationBars,
            ) { padding ->
                SampleList(contentPadding = padding)
            }
        }
    }
}
