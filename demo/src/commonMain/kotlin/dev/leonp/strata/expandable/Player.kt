package dev.leonp.strata.expandable

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.common.DragHandle
import dev.leonp.strata.common.SEEK_DURATION_MILLIS
import dev.leonp.strata.common.SeekBoundsTransform
import dev.leonp.strata.common.fadeInAtEnd
import dev.leonp.strata.common.fadeOutAtStart
import dev.leonp.strata.draggable.bottombar.ExpandableContent
import dev.leonp.strata.draggable.bottombar.ExpandableContentScope
import dev.leonp.strata.draggable.common.DraggableLayoutState

private const val ARTWORK_KEY = "Artwork"
private const val TRACK_TITLE_KEY = "TrackTitle"
private const val TRACK_ARTIST_KEY = "TrackArtist"
private const val TRACK_TITLE = "Midnight Layers"
private const val TRACK_ARTIST = "The Draggables"
private const val TRACK_PROGRESS = 0.35f
private const val MINI_CONTROLS_FADE_FRACTION = 0.2f
private const val FULL_CONTROLS_FADE_FRACTION = 0.3f

// The drag handles of both states fade at opposite ends of the drag, so only one is visible.
private const val DRAG_HANDLE_FADE_FRACTION = 0.2f

private val MiniArtworkCornerRadius = 8.dp
private val FullArtworkCornerRadius = 28.dp

/** The corner radius of the artwork in [QueuePlayer]. */
internal val QueueArtworkCornerRadius = 16.dp

/**
 * An [ExpandableContent] that switches between [MiniPlayer] and [FullPlayer].
 *
 * @param collapsedInsets the insets the mini player keeps clear, for example the navigation bar.
 * @param expandedInsets the insets the full player keeps clear, for example the system bars.
 */
@Composable
internal fun SharedTransitionScope.PlayerExpandableContent(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    collapsedInsets: WindowInsets,
    expandedInsets: WindowInsets,
    modifier: Modifier = Modifier,
) {
    ExpandableContent(
        modifier = modifier
            .shadow(elevation = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) { layoutState ->
        when (layoutState) {
            DraggableLayoutState.Collapsed -> MiniPlayer(
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                modifier = Modifier.windowInsetsPadding(collapsedInsets),
            )

            DraggableLayoutState.Expanded -> FullPlayer(
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                modifier = Modifier.windowInsetsPadding(expandedInsets),
            )
        }
    }
}

/** The collapsed player bar. The artwork and the track info are shared with the expanded states. */
@Composable
internal fun ExpandableContentScope.MiniPlayer(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    expandedArtworkCornerRadius: Dp = FullArtworkCornerRadius,
) {
    val controlsModifier = Modifier.animateEnterExit(
        enter = fadeInAtEnd(fraction = MINI_CONTROLS_FADE_FRACTION),
        exit = fadeOutAtStart(fraction = MINI_CONTROLS_FADE_FRACTION),
    )

    Column(modifier = modifier.fillMaxWidth()) {
        FadingDragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SharedArtwork(
                cornerRadius = MiniArtworkCornerRadius,
                otherCornerRadius = expandedArtworkCornerRadius,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            TrackInfo(
                titleStyle = MaterialTheme.typography.titleMedium,
                artistStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = onPlayPauseClick,
                modifier = controlsModifier,
            )
        }
    }
}

/** The expanded player with a large artwork and all controls. */
@Composable
internal fun ExpandableContentScope.FullPlayer(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controlsModifier = Modifier.animateEnterExit(
        enter = fadeInAtEnd(fraction = FULL_CONTROLS_FADE_FRACTION),
        exit = fadeOutAtStart(fraction = FULL_CONTROLS_FADE_FRACTION),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FadingDragHandle(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = "Now playing",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = controlsModifier.padding(top = 16.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            // Takes the largest square that fits, so small screens still show all controls.
            SharedArtwork(
                cornerRadius = FullArtworkCornerRadius,
                otherCornerRadius = MiniArtworkCornerRadius,
                modifier = Modifier
                    .weight(weight = 1f, fill = false)
                    .aspectRatio(1f),
            )
            TrackInfo(
                titleStyle = MaterialTheme.typography.headlineSmall,
                artistStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            )
            Column(modifier = controlsModifier.padding(top = 24.dp, bottom = 32.dp)) {
                LinearProgressIndicator(
                    progress = { TRACK_PROGRESS },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "1:24", style = MaterialTheme.typography.labelMedium)
                    Text(text = "3:58", style = MaterialTheme.typography.labelMedium)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = DemoIcons.SkipPrevious,
                            contentDescription = "Previous",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    PlayPauseButton(
                        isPlaying = isPlaying,
                        onClick = onPlayPauseClick,
                        isLarge = true,
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = DemoIcons.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
        }
    }
}

/** An expanded player that shows a scrollable queue below the current track. */
@Composable
internal fun ExpandableContentScope.QueuePlayer(modifier: Modifier = Modifier) {
    val queueModifier = Modifier.animateEnterExit(
        enter = fadeInAtEnd(fraction = FULL_CONTROLS_FADE_FRACTION),
        exit = fadeOutAtStart(fraction = FULL_CONTROLS_FADE_FRACTION),
    )

    Column(modifier = modifier.fillMaxSize()) {
        FadingDragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )
        Row(
            modifier = Modifier.padding(all = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SharedArtwork(
                cornerRadius = QueueArtworkCornerRadius,
                otherCornerRadius = MiniArtworkCornerRadius,
                modifier = Modifier.size(88.dp),
            )
            Spacer(modifier = Modifier.width(20.dp))
            TrackInfo(
                titleStyle = MaterialTheme.typography.titleLarge,
                artistStyle = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(
            text = "Up next",
            style = MaterialTheme.typography.titleMedium,
            modifier = queueModifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        LazyColumn(modifier = queueModifier.weight(1f)) {
            items(count = 30) { index ->
                ListItem(
                    headlineContent = { Text(text = "Track ${index + 2}") },
                    supportingContent = { Text(text = TRACK_ARTIST) },
                    leadingContent = {
                        Artwork(
                            cornerRadius = 8.dp,
                            modifier = Modifier.size(40.dp),
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

/**
 * A drag handle that fades out at the start of a drag away from its state and fades in at the end
 * of a drag towards it.
 */
@Composable
private fun ExpandableContentScope.FadingDragHandle(modifier: Modifier = Modifier) {
    DragHandle(
        modifier = Modifier
            .animateEnterExit(
                enter = fadeInAtEnd(fraction = DRAG_HANDLE_FADE_FRACTION),
                exit = fadeOutAtStart(fraction = DRAG_HANDLE_FADE_FRACTION),
            )
            .then(modifier),
    )
}

/**
 * The artwork shared between all player states.
 *
 * A shared element only draws the content of the state it moves to, laid out with the animated
 * size. The corner radius therefore animates from [otherCornerRadius] to [cornerRadius] along the
 * same timeline as the size, instead of applying the final radius to the small starting size.
 *
 * @param cornerRadius the corner radius in this state.
 * @param otherCornerRadius the corner radius in the state the transition comes from or goes to.
 */
@Composable
private fun ExpandableContentScope.SharedArtwork(
    cornerRadius: Dp,
    otherCornerRadius: Dp,
    modifier: Modifier = Modifier,
) {
    val animatedCornerRadius by transition.animateDp(
        transitionSpec = {
            tween(
                durationMillis = SEEK_DURATION_MILLIS,
                easing = LinearEasing,
            )
        },
        label = "ArtworkCornerRadius",
    ) { enterExitState ->
        if (enterExitState == EnterExitState.Visible) cornerRadius else otherCornerRadius
    }

    Artwork(
        cornerRadius = animatedCornerRadius,
        modifier = Modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(ARTWORK_KEY),
                animatedVisibilityScope = this,
                boundsTransform = SeekBoundsTransform,
            )
            .then(modifier),
    )
}

@Composable
private fun ExpandableContentScope.TrackInfo(
    titleStyle: TextStyle,
    artistStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = TRACK_TITLE,
            style = titleStyle,
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(TRACK_TITLE_KEY),
                animatedVisibilityScope = this@TrackInfo,
                enter = fadeInAtEnd(),
                exit = fadeOutAtStart(),
                boundsTransform = SeekBoundsTransform,
            ),
        )
        Text(
            text = TRACK_ARTIST,
            style = artistStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(TRACK_ARTIST_KEY),
                animatedVisibilityScope = this@TrackInfo,
                enter = fadeInAtEnd(),
                exit = fadeOutAtStart(),
                boundsTransform = SeekBoundsTransform,
            ),
        )
    }
}

@Composable
private fun Artwork(cornerRadius: Dp, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Brush.linearGradient(listOf(colors.tertiary, colors.primary))),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = 0.4f)
                .clip(CircleShape)
                .background(colors.onPrimary.copy(alpha = 0.25f)),
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false,
) {
    val icon = if (isPlaying) DemoIcons.Pause else DemoIcons.Play
    val contentDescription = if (isPlaying) "Pause" else "Play"

    if (isLarge) {
        FilledIconButton(
            onClick = onClick,
            modifier = modifier.size(72.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(36.dp),
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
