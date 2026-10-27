package dev.leonp.strata.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.leonp.strata.catalog.DemoCategory
import dev.leonp.strata.catalog.DemoComponent
import dev.leonp.strata.catalog.DemoSample
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.CategoryBadge
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.common.containerColor
import dev.leonp.strata.common.contentColor

/** The sub menu [category] of [component], listing its samples. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    component: DemoComponent,
    category: DemoCategory,
    onSampleClick: (DemoSample) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val samples = remember(component, category) {
        DemoSample.of(component = component, category = category)
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = category.title) },
                navigationIcon = { BackButton(onClick = onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding(),
                end = 16.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CategoryBadge(category = category)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = component.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            itemsIndexed(items = samples) { index, sample ->
                SampleCard(
                    number = index + 1,
                    sample = sample,
                    onClick = { onSampleClick(sample) },
                )
            }
        }
    }
}

@Composable
private fun SampleCard(
    number: Int,
    sample: DemoSample,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(all = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(sample.category.containerColor()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = sample.category.contentColor(),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sample.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = sample.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = DemoIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
