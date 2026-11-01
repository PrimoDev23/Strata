package dev.leonp.strata.draggable.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.compare.CompareSlider
import dev.leonp.strata.theme.StrataTheme

@Preview
@Composable
private fun CompareSliderPreview() {
    StrataTheme {
        CompareSlider(
            before = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Before",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            },
            after = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "After",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            },
            thumb = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
