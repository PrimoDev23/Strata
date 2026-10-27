package dev.leonp.strata.draggable.previews

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.leonp.strata.draggable.bottombar.ExpandableContent
import dev.leonp.strata.draggable.common.DraggableLayoutState
import dev.leonp.strata.theme.StrataTheme

private const val PREVIEW_SEEK_DURATION = 1000
private const val PREVIEW_FADE_DURATION = 200

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun ExpandableContentPreview() {
    StrataTheme {
        SharedTransitionLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // The background belongs to the container, so it grows with the drag right away
                // while only the content fades.
                ExpandableContent(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) { layoutState ->
                    // Seeking maps the drag progress onto the total duration of all animations, so
                    // both states share one timeline: the outgoing content fades out during the
                    // first 20 % of the drag and the incoming content fades in during the last 20 %.
                    val contentModifier = Modifier.animateEnterExit(
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = PREVIEW_FADE_DURATION,
                                delayMillis = PREVIEW_SEEK_DURATION - PREVIEW_FADE_DURATION,
                                easing = LinearEasing,
                            ),
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = PREVIEW_FADE_DURATION,
                                easing = LinearEasing,
                            ),
                        ),
                    )

                    when (layoutState) {
                        DraggableLayoutState.Collapsed -> Text(
                            text = "Collapsed content",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = contentModifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                        )

                        DraggableLayoutState.Expanded -> Text(
                            text = "Expanded content",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = contentModifier
                                .fillMaxSize()
                                .padding(all = 24.dp),
                        )
                    }
                }
            }
        }
    }
}
