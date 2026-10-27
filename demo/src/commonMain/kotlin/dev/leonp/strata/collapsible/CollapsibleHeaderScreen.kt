package dev.leonp.strata.collapsible

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer

private const val HEADER_TEXT_KEY = "HeaderText"

@Composable
fun CollapsibleHeaderScreen(modifier: Modifier = Modifier) {
    SharedTransitionLayout {
        AnimatableCollapsibleHeaderContainer(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            expandedHeader = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                ) {
                    Text(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(HEADER_TEXT_KEY),
                            animatedVisibilityScope = this@AnimatableCollapsibleHeaderContainer,
                        ),
                        text = "Collapsible header",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        modifier = Modifier.animateEnterExit(),
                        text = "Scroll the list to collapse this header",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            },
            collapsedHeader = { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(padding),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(HEADER_TEXT_KEY),
                                animatedVisibilityScope = this@AnimatableCollapsibleHeaderContainer,
                            ),
                        text = "Collapsible header",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            },
            insets = WindowInsets.safeContent,
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding,
            ) {
                items(count = 50) { index ->
                    ListItem(headlineContent = { Text(text = "Item ${index + 1}") })
                    HorizontalDivider()
                }
            }
        }
    }
}
