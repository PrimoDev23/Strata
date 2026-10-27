package dev.leonp.strata.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.leonp.strata.catalog.DemoCategory

internal val DemoCategory.icon: ImageVector
    get() = when (this) {
        DemoCategory.Animated -> DemoIcons.AutoAwesome
        DemoCategory.NonAnimated -> DemoIcons.SwapVert
        DemoCategory.Configuration -> DemoIcons.Tune
    }

@Composable
internal fun DemoCategory.containerColor(): Color = when (this) {
    DemoCategory.Animated -> MaterialTheme.colorScheme.primaryContainer
    DemoCategory.NonAnimated -> MaterialTheme.colorScheme.secondaryContainer
    DemoCategory.Configuration -> MaterialTheme.colorScheme.tertiaryContainer
}

@Composable
internal fun DemoCategory.contentColor(): Color = when (this) {
    DemoCategory.Animated -> MaterialTheme.colorScheme.onPrimaryContainer
    DemoCategory.NonAnimated -> MaterialTheme.colorScheme.onSecondaryContainer
    DemoCategory.Configuration -> MaterialTheme.colorScheme.onTertiaryContainer
}

/** The colored icon of a [DemoCategory]. */
@Composable
internal fun CategoryBadge(category: DemoCategory, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(category.containerColor()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = category.contentColor(),
            modifier = Modifier.size(20.dp),
        )
    }
}
