package dev.leonp.strata.expandable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.DragHandle

private val OrderSteps = listOf(
    "Order placed" to true,
    "Packed" to true,
    "On the way" to true,
    "Delivered" to false,
)

/** The collapsed state of the order tracker. */
@Composable
internal fun OrderSummary(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        DragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your order is on the way",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Arrives in 12 minutes",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                text = "Track",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

/** The expanded state of the order tracker. */
@Composable
internal fun OrderDetails(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        DragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )
        Text(
            text = "Order #2711",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = "Arrives in 12 minutes",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            OrderSteps.forEach { (step, isDone) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotModifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)

                    Box(
                        modifier = if (isDone) {
                            dotModifier.background(MaterialTheme.colorScheme.tertiary)
                        } else {
                            dotModifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape,
                            )
                        },
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

/** The collapsed state of the note sample, drawn on a secondary container background. */
@Composable
internal fun QuickNoteBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Quick note",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            text = "Drag up to start writing",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

/** The expanded state of the note sample. */
@Composable
internal fun NoteEditor(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        DragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )
        Text(
            text = "New note",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text =
                "Neither state is animated. As soon as a drag starts, the state that is dragged " +
                    "towards is composed and drawn on top, clipped to the current height.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
