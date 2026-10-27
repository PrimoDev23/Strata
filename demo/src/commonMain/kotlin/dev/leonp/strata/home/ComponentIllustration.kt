package dev.leonp.strata.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.leonp.strata.catalog.DemoComponent

private val LineFractions = listOf(1f, 0.7f, 0.85f, 0.55f, 0.9f, 0.65f)

/** A tiny phone sketch that shows where a [DemoComponent] lives on the screen. */
@Composable
internal fun ComponentIllustration(component: DemoComponent, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .size(width = 56.dp, height = 88.dp)
            .clip(shape)
            .background(colors.surface)
            .border(width = 1.dp, color = colors.outlineVariant, shape = shape),
    ) {
        if (component == DemoComponent.CollapsibleHeader) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(colors.primary),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(all = 7.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            val lineCount = if (component == DemoComponent.CollapsibleHeader) 5 else 6

            LineFractions.take(lineCount).forEach { fraction ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(colors.outlineVariant),
                )
            }
        }

        if (component == DemoComponent.ExpandableContent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(colors.tertiary),
            )
        }
    }
}
