package dev.leonp.strata.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.leonp.strata.animated.ReorderSample
import dev.leonp.strata.catalog.DemoSample
import dev.leonp.strata.collapsible.AnimatedWithoutCollapsedHeaderSample
import dev.leonp.strata.collapsible.BelowTopAppBarSample
import dev.leonp.strata.collapsible.CrossfadeSample
import dev.leonp.strata.collapsible.NonScrollableContentSample
import dev.leonp.strata.collapsible.SharedTitleSample
import dev.leonp.strata.collapsible.ShortListSample
import dev.leonp.strata.collapsible.SwitchingHeadersSample
import dev.leonp.strata.collapsible.WithoutCollapsedHeaderSample
import dev.leonp.strata.expandable.AboveNavigationBarSample
import dev.leonp.strata.expandable.FadeThroughSample
import dev.leonp.strata.expandable.InstantSwitchSample
import dev.leonp.strata.expandable.MusicPlayerSample
import dev.leonp.strata.expandable.ScrollableExpandedContentSample
import dev.leonp.strata.expandable.WithCollapsibleHeaderSample

/** Shows the demo of [sample]. */
@Composable
fun SampleScreen(sample: DemoSample, onBack: () -> Unit, modifier: Modifier = Modifier) {
    // The surface provides the content color for samples that draw outside of a Scaffold.
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        SampleContent(sample = sample, onBack = onBack)
    }
}

@Composable
private fun SampleContent(sample: DemoSample, onBack: () -> Unit) {
    val sampleModifier = Modifier.fillMaxSize()

    when (sample) {
        DemoSample.HeaderSharedTitle -> SharedTitleSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.HeaderCrossfade -> CrossfadeSample(onBack = onBack, modifier = sampleModifier)

        DemoSample.HeaderAnimatedWithoutCollapsed -> AnimatedWithoutCollapsedHeaderSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.HeaderSwitching -> SwitchingHeadersSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.HeaderWithoutCollapsed -> WithoutCollapsedHeaderSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.HeaderNonScrollable -> NonScrollableContentSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.HeaderShortList -> ShortListSample(onBack = onBack, modifier = sampleModifier)

        DemoSample.HeaderBelowTopAppBar -> BelowTopAppBarSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.ExpandablePlayer -> MusicPlayerSample(onBack = onBack, modifier = sampleModifier)

        DemoSample.ExpandableFadeThrough -> FadeThroughSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.ExpandableInstantSwitch -> InstantSwitchSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.ExpandableAboveNavigationBar -> AboveNavigationBarSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.ExpandableWithCollapsibleHeader -> WithCollapsibleHeaderSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.ExpandableScrollableContent -> ScrollableExpandedContentSample(
            onBack = onBack,
            modifier = sampleModifier,
        )

        DemoSample.AnimatedColumnReorder -> ReorderSample(
            onBack = onBack,
            modifier = sampleModifier,
        )
    }
}
