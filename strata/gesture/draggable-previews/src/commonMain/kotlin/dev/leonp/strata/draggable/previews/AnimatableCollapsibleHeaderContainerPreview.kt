package dev.leonp.strata.draggable.previews

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer
import dev.leonp.strata.theme.StrataTheme

@Preview
@Composable
private fun AnimatableCollapsibleHeaderContainerPreview() {
    StrataTheme {
        SharedTransitionLayout {
            AnimatableCollapsibleHeaderContainer(
                expandedHeader = { padding ->
                    Text(
                        text = "Expanded header",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(padding)
                            .padding(all = 24.dp),
                    )
                },
                collapsedHeader = { padding ->
                    Text(
                        text = "Collapsed header",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(padding)
                            .padding(all = 16.dp),
                    )
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            ) { padding ->
                LazyColumn(contentPadding = padding) {
                    items(count = 30) { index ->
                        Text(
                            text = "Item $index",
                            modifier = Modifier.padding(all = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
