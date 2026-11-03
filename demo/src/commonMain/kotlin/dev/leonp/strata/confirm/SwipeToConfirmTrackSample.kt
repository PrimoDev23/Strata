package dev.leonp.strata.confirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.draggable.confirm.SwipeToConfirm
import dev.leonp.strata.draggable.confirm.SwipeToConfirmState
import dev.leonp.strata.draggable.confirm.rememberSwipeToConfirmState

private val TrackHeight = 72.dp
private val BadgeSize = 88.dp
private val TrackPadding = 8.dp

/** The opacity of the fill, low enough to keep the label readable while the fill passes behind it. */
private const val FILL_ALPHA = 0.35f

/**
 * A door lock that is opened by sliding the thumb to the end of the track. The track keeps its
 * neutral color and only `trackColor` draws the fill that follows the thumb, so no custom indicator
 * is needed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeToConfirmTrackSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var unlock by remember { mutableIntStateOf(0) }
    var isUnlocked by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Front door") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            DoorStatus(isUnlocked = isUnlocked)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "The colored fill behind the thumb is the track color of SwipeToConfirm.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )

            // A new unlock needs a fresh state, as the confirmed state cannot be reset
            key(unlock) {
                UnlockSlider(onUnlocked = { isUnlocked = true })
            }

            AnimatedVisibility(
                visible = isUnlocked,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                TextButton(
                    onClick = {
                        isUnlocked = false
                        unlock++
                    },
                ) {
                    Text(text = "Lock again")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DoorStatus(isUnlocked: Boolean, modifier: Modifier = Modifier) {
    val badgeColor by animateColorAsState(
        targetValue = if (isUnlocked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
    )
    val iconColor by animateColorAsState(
        targetValue = if (isUnlocked) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(BadgeSize)
                    .background(
                        color = badgeColor,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isUnlocked) DemoIcons.LockOpen else DemoIcons.Lock,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isUnlocked) "Unlocked" else "Locked",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = if (isUnlocked) "Anyone can open the front door." else "The front door is secured.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * The track that unlocks the door. Its background stays neutral while `trackColor` fills the area
 * the thumb has covered, and [onUnlocked] is called once the thumb reached the end.
 */
@Composable
private fun UnlockSlider(onUnlocked: () -> Unit, modifier: Modifier = Modifier) {
    val state = rememberSwipeToConfirmState()
    val progress = state.safeProgress()
    val isUnlocked by remember(state) { derivedStateOf { state.safeProgress() >= 1f } }

    val currentOnUnlocked by rememberUpdatedState(onUnlocked)

    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            currentOnUnlocked()
        }
    }

    SwipeToConfirm(
        thumb = { UnlockThumb(isUnlocked = isUnlocked) },
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = FILL_ALPHA),
        modifier = modifier
            .fillMaxWidth()
            .height(TrackHeight)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape,
            ),
        state = state,
        contentPadding = PaddingValues(all = TrackPadding),
    ) {
        Text(
            text = "Slide to unlock",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(1f - progress),
        )
    }
}

@Composable
private fun UnlockThumb(isUnlocked: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
            )
            .fillMaxHeight()
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isUnlocked) DemoIcons.LockOpen else DemoIcons.Lock,
            contentDescription = "Slide to unlock",
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** The progress of the thumb, which is zero until the track is measured and has its anchors. */
private fun SwipeToConfirmState.safeProgress(): Float = if (offset.isNaN()) 0f else progress
