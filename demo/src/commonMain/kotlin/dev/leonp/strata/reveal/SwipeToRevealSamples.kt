package dev.leonp.strata.reveal

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.InfoCard
import dev.leonp.strata.common.StateControls
import dev.leonp.strata.draggable.reveal.RevealState
import dev.leonp.strata.draggable.reveal.SwipeToReveal
import dev.leonp.strata.draggable.reveal.SwipeToRevealGroup
import dev.leonp.strata.draggable.reveal.SwipeToRevealLabels
import dev.leonp.strata.draggable.reveal.SwipeToRevealState
import dev.leonp.strata.draggable.reveal.rememberSwipeToRevealGroup
import dev.leonp.strata.draggable.reveal.rememberSwipeToRevealState
import kotlinx.coroutines.launch

private const val ANIMATION_DURATION_MILLIS = 300

private val MessageHeight = 72.dp
private val ActionWidth = 88.dp

private class Message(val sender: String, val subject: String, val preview: String)

private val Inbox = listOf(
    Message(
        sender = "Basalt Coffee Roasters",
        subject = "Your order has shipped",
        preview = "2 x Ethiopia Guji will arrive on Thursday.",
    ),
    Message(
        sender = "Marta Lindqvist",
        subject = "Trail map for Saturday",
        preview = "I attached the route. We start at the lower parking lot.",
    ),
    Message(
        sender = "Granite Bank",
        subject = "Your statement is ready",
        preview = "The statement for October is available in the app.",
    ),
    Message(
        sender = "Tomas Weber",
        subject = "Re: Quarterly planning",
        preview = "Works for me. Can we move the sync to 10:30?",
    ),
    Message(
        sender = "Slate Fitness",
        subject = "Your class was booked",
        preview = "See you on Monday at 18:00 for Morning Mobility.",
    ),
    Message(
        sender = "Priya Nair",
        subject = "Photos from the weekend",
        preview = "Finally sorted them. The sunset ones turned out great.",
    ),
    Message(
        sender = "Limestone Library",
        subject = "Books are due soon",
        preview = "Two items are due in three days. Renew them online.",
    ),
    Message(
        sender = "Jonas Albrecht",
        subject = "Invoice 2026-114",
        preview = "Please find the invoice for the October consulting hours.",
    ),
)

/** Animates the content to [target] with the animation that all samples share. */
private suspend fun SwipeToRevealState.moveTo(target: RevealState) {
    animateTo(
        targetValue = target,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MILLIS),
    )
}

/**
 * A mail list where every message is swiped to the left to archive or delete it and to the right
 * to pin it. Revealing one message hides the actions of all others.
 */
@Composable
internal fun InboxSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    InboxScreen(
        onBack = onBack,
        controlled = false,
        modifier = modifier,
    )
}

/**
 * The same mail list with buttons that reveal the start or end actions of all messages and hide them
 * again from code, and a switch that disables swiping.
 */
@Composable
internal fun ControlledInboxSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    InboxScreen(
        onBack = onBack,
        controlled = true,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InboxScreen(onBack: () -> Unit, controlled: Boolean, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf(*Inbox.toTypedArray()) }
    var enabled by rememberSaveable { mutableStateOf(true) }

    // Every message keeps its state, so the controls can reveal and hide the actions of all of them
    val states = Inbox.map { rememberSwipeToRevealState() }
    val pinned = remember { mutableStateListOf<Message>() }

    // The controls reveal all messages at once, which would immediately be undone by a group
    val group = rememberSwipeToRevealGroup().takeIf { !controlled }
    val settledValues = messages.map { message -> states[Inbox.indexOf(message)].settledValue }
    val startRevealedCount = settledValues.count { value ->
        value == RevealState.StartActionsVisible
    }
    val endRevealedCount = settledValues.count { value -> value == RevealState.EndActionsVisible }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Inbox") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (controlled) {
                StateControls(
                    settledText = "start = $startRevealedCount, end = $endRevealedCount " +
                        "of ${messages.size}",
                    onExpand = {
                        messages.forEach { message ->
                            val state = states[Inbox.indexOf(message)]
                            coroutineScope.launch { state.moveTo(RevealState.EndActionsVisible) }
                        }
                    },
                    onCollapse = {
                        messages.forEach { message ->
                            val state = states[Inbox.indexOf(message)]
                            coroutineScope.launch { state.moveTo(RevealState.NoActionsVisible) }
                        }
                    },
                    expandLabel = "End",
                    collapseLabel = "Hide all",
                    enabled = enabled,
                    onEnabledChange = { enabled = it },
                    extraLabel = "Start",
                    onExtra = {
                        messages.forEach { message ->
                            val state = states[Inbox.indexOf(message)]
                            coroutineScope.launch { state.moveTo(RevealState.StartActionsVisible) }
                        }
                    },
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = messages,
                    key = { message -> message.subject },
                ) { message ->
                    val state = states[Inbox.indexOf(message)]

                    MessageRow(
                        message = message,
                        state = state,
                        enabled = enabled,
                        group = group,
                        isPinned = message in pinned,
                        onPin = {
                            if (message in pinned) {
                                pinned.remove(message)
                            } else {
                                pinned.add(message)
                            }
                            coroutineScope.launch { state.moveTo(RevealState.NoActionsVisible) }
                        },
                        onArchive = {
                            // Hide first, so the message returns closed when it is restored
                            coroutineScope.launch { state.moveTo(RevealState.NoActionsVisible) }
                            messages.remove(message)
                        },
                        onDelete = {
                            coroutineScope.launch { state.moveTo(RevealState.NoActionsVisible) }
                            messages.remove(message)
                        },
                        modifier = Modifier.animateItem(),
                    )
                    HorizontalDivider()
                }

                item {
                    Footer(
                        isEmpty = messages.isEmpty(),
                        canRestore = messages.size < Inbox.size,
                        onRestore = {
                            messages.clear()
                            messages.addAll(Inbox)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageRow(
    message: Message,
    state: SwipeToRevealState,
    enabled: Boolean,
    group: SwipeToRevealGroup?,
    isPinned: Boolean,
    onPin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SwipeToReveal(
        modifier = modifier.fillMaxWidth(),
        startActions = {
            ActionButton(
                text = if (isPinned) "Unpin" else "Pin",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onPin,
            )
        },
        endActions = {
            // The actions slot has no layout of its own, so the buttons are arranged here
            Row {
                ActionButton(
                    text = "Archive",
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    onClick = onArchive,
                )
                ActionButton(
                    text = "Delete",
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    onClick = onDelete,
                )
            }
        },
        state = state,
        group = group,
        enabled = enabled,
        labels = SwipeToRevealLabels(
            revealStartActions = if (isPinned) "Show unpin action" else "Show pin action",
            revealEndActions = "Show archive and delete actions",
            hideActions = "Hide actions",
            revealedStateDescription = "Actions shown",
        ),
    ) {
        // The content must be opaque, as it is drawn on top of the actions
        ListItem(
            headlineContent = { Text(text = message.subject) },
            supportingContent = {
                Text(
                    text = message.preview,
                    maxLines = 1,
                )
            },
            overlineContent = {
                Text(text = if (isPinned) "${message.sender} · Pinned" else message.sender)
            },
            leadingContent = { SenderAvatar(sender = message.sender) },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier
                .fillMaxWidth()
                .height(MessageHeight),
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(ActionWidth)
            .height(MessageHeight)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

@Composable
private fun SenderAvatar(sender: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = sender.take(1),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun Footer(
    isEmpty: Boolean,
    canRestore: Boolean,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isEmpty) {
            Text(
                text = "Inbox zero",
                style = MaterialTheme.typography.titleMedium,
            )
        } else {
            InfoCard(
                title = "Swipe in either direction",
                text = "Swipe to the left for Archive and Delete, to the right for Pin. " +
                    "The actions sit behind the message and are revealed as it moves aside. " +
                    "A message that is released halfway settles at the closer position.",
            )
        }

        if (canRestore) {
            TextButton(onClick = onRestore) {
                Text(text = "Restore messages")
            }
        }
    }
}
