package dev.leonp.strata.collapsible

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.SeekBoundsTransform
import dev.leonp.strata.common.fadeInAtEnd
import dev.leonp.strata.common.fadeOutAtStart
import dev.leonp.strata.draggable.header.animatable.AnimatableCollapsibleHeaderContainer

private const val BACK_BUTTON_KEY = "BackButton"
private const val TITLE_KEY = "Title"
private const val SUBTITLE_FADE_FRACTION = 0.4f

/** A large header with a title and a subtitle below a back button. */
@Composable
internal fun ExpandedTitleHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    backButtonModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    subtitleModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(padding)
            .padding(bottom = 28.dp),
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier
                .padding(all = 4.dp)
                .then(backButtonModifier),
            tint = contentColor,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = contentColor,
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp, end = 24.dp)
                .then(titleModifier),
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            modifier = Modifier
                .padding(start = 24.dp, top = 8.dp, end = 24.dp)
                .then(subtitleModifier),
        )
    }
}

/** A toolbar sized header with a back button and a title. */
@Composable
internal fun CollapsedTitleHeader(
    title: String,
    onBack: () -> Unit,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    backButtonModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(padding)
            .padding(all = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackButton(
            onClick = onBack,
            modifier = backButtonModifier,
            tint = contentColor,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor,
            modifier = Modifier
                .padding(start = 4.dp)
                .then(titleModifier),
        )
    }
}

/**
 * A large gradient header with the title at its bottom. It is at least 220 dp high, so a header
 * that is squeezed keeps the title at its bottom edge, while a clipped header hides it first.
 */
@Composable
internal fun HeroHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(colors.primary, colors.tertiary)))
            .padding(padding)
            .heightIn(min = 220.dp),
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(all = 4.dp),
            tint = colors.onPrimary,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(all = 24.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = colors.onPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/** A banner card used as a header that collapses completely. */
@Composable
internal fun PromoBanner(padding: PaddingValues, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding)
            .padding(all = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(all = 20.dp)) {
            Text(
                text = "Featured",
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "This banner scrolls away completely",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = "The top app bar already handles the status bar, so the header has no top " +
                    "insets and collapses to zero height.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/**
 * An [AnimatableCollapsibleHeaderContainer] whose back button and title move between both headers
 * as shared elements, while the subtitle fades out.
 */
@Composable
internal fun SharedTitleContainer(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    insets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit,
) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatableCollapsibleHeaderContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            expandedHeader = { padding ->
                ExpandedTitleHeader(
                    title = title,
                    subtitle = subtitle,
                    onBack = onBack,
                    padding = padding,
                    backButtonModifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(BACK_BUTTON_KEY),
                        animatedVisibilityScope = this,
                        boundsTransform = SeekBoundsTransform,
                    ),
                    titleModifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(TITLE_KEY),
                        animatedVisibilityScope = this,
                        enter = fadeInAtEnd(),
                        exit = fadeOutAtStart(),
                        boundsTransform = SeekBoundsTransform,
                    ),
                    subtitleModifier = Modifier.animateEnterExit(
                        enter = fadeInAtEnd(fraction = SUBTITLE_FADE_FRACTION),
                        exit = fadeOutAtStart(fraction = SUBTITLE_FADE_FRACTION),
                    ),
                )
            },
            collapsedHeader = { padding ->
                CollapsedTitleHeader(
                    title = title,
                    onBack = onBack,
                    padding = padding,
                    backButtonModifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(BACK_BUTTON_KEY),
                        animatedVisibilityScope = this,
                        boundsTransform = SeekBoundsTransform,
                    ),
                    titleModifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(TITLE_KEY),
                        animatedVisibilityScope = this,
                        enter = fadeInAtEnd(),
                        exit = fadeOutAtStart(),
                        boundsTransform = SeekBoundsTransform,
                    ),
                )
            },
            insets = insets,
            content = content,
        )
    }
}
