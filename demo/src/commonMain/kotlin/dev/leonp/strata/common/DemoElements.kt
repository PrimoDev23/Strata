package dev.leonp.strata.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val AVATAR_COLOR_COUNT = 3

private val LayerNames = listOf(
    "Bedrock", "Basalt", "Granite", "Limestone", "Sandstone",
    "Shale", "Slate", "Marble", "Quartzite", "Sediment",
)

@Composable
internal fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = DemoIcons.ArrowBack,
            contentDescription = "Back",
            tint = tint,
        )
    }
}

/** A long list of placeholder rows, used as the scrollable content of the samples. */
@Composable
internal fun SampleList(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    itemCount: Int = 40,
    extraBottomPadding: Dp = 0.dp,
) {
    val layoutDirection = LocalLayoutDirection.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(layoutDirection),
            top = contentPadding.calculateTopPadding() + 8.dp,
            end = contentPadding.calculateEndPadding(layoutDirection),
            bottom = contentPadding.calculateBottomPadding() + extraBottomPadding + 8.dp,
        ),
    ) {
        items(count = itemCount) { index ->
            val name = LayerNames[index % LayerNames.size]

            ListItem(
                headlineContent = { Text(text = name) },
                supportingContent = { Text(text = "Layer ${index + 1}") },
                leadingContent = { LayerAvatar(index = index, name = name) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

@Composable
private fun LayerAvatar(index: Int, name: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (index % AVATAR_COLOR_COUNT) {
        0 -> colors.primaryContainer to colors.onPrimaryContainer
        1 -> colors.secondaryContainer to colors.onSecondaryContainer
        else -> colors.tertiaryContainer to colors.onTertiaryContainer
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.take(1),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
        )
    }
}

/** A card that explains a sample. */
@Composable
internal fun InfoCard(title: String, text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(all = 20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** The pill that hints that a layout can be dragged. */
@Composable
internal fun DragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
    )
}
