package dev.leonp.strata.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.leonp.strata.catalog.DemoCategory
import dev.leonp.strata.catalog.DemoComponent
import dev.leonp.strata.catalog.DemoSample
import dev.leonp.strata.common.CategoryBadge
import dev.leonp.strata.common.DemoIcons

/** Lists every [DemoComponent] with its [DemoCategory] sub menus. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (DemoComponent, DemoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Strata") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Text(
                    text = "Draggable layouts for Compose Multiplatform. Pick a component and try its variants.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            items(items = DemoComponent.entries) { component ->
                ComponentSection(
                    component = component,
                    onCategoryClick = { category -> onCategoryClick(component, category) },
                )
            }
        }
    }
}

@Composable
private fun ComponentSection(
    component: DemoComponent,
    onCategoryClick: (DemoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(all = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComponentIllustration(component = component)
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = component.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = component.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            DemoCategory.entries.forEach { category ->
                CategoryRow(
                    category = category,
                    sampleCount = DemoSample.of(component = component, category = category).size,
                    onClick = { onCategoryClick(category) },
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: DemoCategory,
    sampleCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        headlineContent = { Text(text = category.title) },
        supportingContent = { Text(text = category.description) },
        leadingContent = { CategoryBadge(category = category) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sampleCount.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = DemoIcons.ChevronRight,
                    contentDescription = null,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
