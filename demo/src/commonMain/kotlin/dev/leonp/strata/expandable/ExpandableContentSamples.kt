package dev.leonp.strata.expandable

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.leonp.strata.collapsible.SharedTitleContainer
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.common.SampleList
import dev.leonp.strata.common.fadeInAtEnd
import dev.leonp.strata.common.fadeOutAtStart
import dev.leonp.strata.draggable.bottombar.ExpandableContent
import dev.leonp.strata.draggable.common.DraggableLayoutState

private const val FADE_THROUGH_FRACTION = 0.2f

// Keeps the end of the background lists clear of the collapsed content.
private val CollapsedContentClearance = 96.dp

private val NavigationTabs = listOf(
    "Home" to DemoIcons.Home,
    "Search" to DemoIcons.Search,
    "Library" to DemoIcons.Library,
)

@Composable
internal fun MusicPlayerSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    // Hoisted, because the content of ExpandableContent is composed more than once.
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    SharedTransitionLayout(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            SampleScaffold(
                title = "Music player",
                onBack = onBack,
            )
            PlayerExpandableContent(
                isPlaying = isPlaying,
                onPlayPauseClick = { isPlaying = !isPlaying },
                collapsedInsets = WindowInsets.navigationBars,
                expandedInsets = WindowInsets.systemBars,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
internal fun FadeThroughSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTransitionLayout(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            SampleScaffold(
                title = "Fade through",
                onBack = onBack,
            )
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                ExpandableContent(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 8.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                ) { layoutState ->
                    // Both states share one timeline: the outgoing state fades out during the
                    // first 20 % of the drag and the incoming state fades in during the last 20 %.
                    val fadeModifier = Modifier.animateEnterExit(
                        enter = fadeInAtEnd(fraction = FADE_THROUGH_FRACTION),
                        exit = fadeOutAtStart(fraction = FADE_THROUGH_FRACTION),
                    )

                    when (layoutState) {
                        DraggableLayoutState.Collapsed -> OrderSummary(
                            modifier = fadeModifier.windowInsetsPadding(
                                WindowInsets.navigationBars,
                            ),
                        )

                        DraggableLayoutState.Expanded -> OrderDetails(
                            modifier = fadeModifier.windowInsetsPadding(WindowInsets.systemBars),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun InstantSwitchSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SharedTransitionLayout(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            SampleScaffold(
                title = "Instant switch",
                onBack = onBack,
            )
            // Each state draws its own background, so the state on top covers the other one.
            ExpandableContent(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 8.dp),
            ) { layoutState ->
                when (layoutState) {
                    DraggableLayoutState.Collapsed -> QuickNoteBar(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .windowInsetsPadding(WindowInsets.navigationBars),
                    )

                    DraggableLayoutState.Expanded -> NoteEditor(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .windowInsetsPadding(WindowInsets.systemBars),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AboveNavigationBarSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    SharedTransitionLayout(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // The layout expands to the full height of its parent, which ends above the bar.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                SampleScaffold(
                    title = NavigationTabs[selectedTab].first,
                    onBack = onBack,
                )
                PlayerExpandableContent(
                    isPlaying = isPlaying,
                    onPlayPauseClick = { isPlaying = !isPlaying },
                    collapsedInsets = WindowInsets(0, 0, 0, 0),
                    expandedInsets = WindowInsets.statusBars,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
            NavigationBar {
                NavigationTabs.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = index == selectedTab,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(text = label) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun WithCollapsibleHeaderSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    SharedTransitionLayout(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            SharedTitleContainer(
                title = "Both at once",
                subtitle = "A collapsible header and a player",
                onBack = onBack,
            ) { padding ->
                SampleList(
                    contentPadding = padding,
                    extraBottomPadding = CollapsedContentClearance,
                )
            }
            PlayerExpandableContent(
                isPlaying = isPlaying,
                onPlayPauseClick = { isPlaying = !isPlaying },
                collapsedInsets = WindowInsets.navigationBars,
                expandedInsets = WindowInsets.systemBars,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
internal fun ScrollableExpandedContentSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    SharedTransitionLayout(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            SampleScaffold(
                title = "Scrollable content",
                onBack = onBack,
            )
            ExpandableContent(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) { layoutState ->
                when (layoutState) {
                    DraggableLayoutState.Collapsed -> MiniPlayer(
                        isPlaying = isPlaying,
                        onPlayPauseClick = { isPlaying = !isPlaying },
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                        expandedArtworkCornerRadius = QueueArtworkCornerRadius,
                    )

                    DraggableLayoutState.Expanded -> QueuePlayer(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                    )
                }
            }
        }
    }
}

/** A screen with a top app bar and a list, shown behind the expandable content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    extraBottomPadding: Dp = CollapsedContentClearance,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        SampleList(
            contentPadding = padding,
            extraBottomPadding = extraBottomPadding,
        )
    }
}
