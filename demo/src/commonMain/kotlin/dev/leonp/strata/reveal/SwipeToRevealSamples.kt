package dev.leonp.strata.reveal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import dev.leonp.strata.draggable.reveal.SwipeToReveal
import dev.leonp.strata.draggable.reveal.SwipeToRevealState
import dev.leonp.strata.draggable.reveal.rememberSwipeToRevealState
import kotlinx.coroutines.launch

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

/** A mail list where every message is swiped to the left to archive or delete it. */
@Composable
internal fun InboxSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    InboxScreen(
        onBack = onBack,
        controlled = false,
        modifier = modifier,
    )
}

/**
 * The same mail list with buttons that reveal and hide the actions of all messages from code and a
 * switch that disables swiping.
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
    val revealedCount = messages.count { message -> states[Inbox.indexOf(message)].isRevealed }

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
                    settledText = "isRevealed = $revealedCount of ${messages.size}",
                    onExpand = {
                        messages.forEach { message ->
                            coroutineScope.launch { states[Inbox.indexOf(message)].reveal() }
                        }
                    },
                    onCollapse = {
                        messages.forEach { message ->
                            coroutineScope.launch { states[Inbox.indexOf(message)].hide() }
                        }
                    },
                    expandLabel = "Reveal all",
                    collapseLabel = "Hide all",
                    enabled = enabled,
                    onEnabledChange = { enabled = it },
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
                        onArchive = {
                            // Hide first, so the message returns closed when it is restored
                            coroutineScope.launch { state.hide() }
                            messages.remove(message)
                        },
                        onDelete = {
                            coroutineScope.launch { state.hide() }
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
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SwipeToReveal(
        actions = {
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
        },
        modifier = modifier.fillMaxWidth(),
        state = state,
        enabled = enabled,
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
            overlineContent = { Text(text = message.sender) },
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
                title = "Swipe to the left",
                text = "The actions sit behind the message and are revealed as it moves aside. " +
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
