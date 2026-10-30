package dev.leonp.strata.animated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton

// Enough items to overflow the screen, so the column has to scroll.
private const val ITEM_COUNT = 20

private val ItemHeights = listOf(72.dp, 56.dp, 96.dp, 64.dp, 80.dp, 56.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReorderSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    // The ids are the identity of the items. Only their order changes.
    var ids by rememberSaveable { mutableStateOf((0 until ITEM_COUNT).toList()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Reordering") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = { ids = ids.shuffled() }) { Text(text = "Shuffle") }
                OutlinedButton(onClick = { ids = ids.reversed() }) { Text(text = "Reverse") }
                OutlinedButton(onClick = { ids = ids.drop(1) + ids.first() }) {
                    Text(text = "Rotate")
                }
            }

            AnimatedColumn(
                items = ids,
                key = { it },
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) { id ->
                ReorderItem(id = id)
            }
        }
    }
}

@Composable
private fun ReorderItem(id: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val containerColor = when (id % 3) {
        0 -> colors.primaryContainer
        1 -> colors.secondaryContainer
        else -> colors.tertiaryContainer
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 20.dp)
            .height(ItemHeights[id % ItemHeights.size]),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = "Item ${id + 1}",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
