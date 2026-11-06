# Changelog

All notable changes to Strata are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- `gesture-draggable`: `CompareSlider`, a layout that compares two slots by dragging a slider
  between them. Both slots keep their full size and are clipped at the slider. Its position lives
  in a `CompareSliderState` from `rememberCompareSliderState(initialFraction)`, which can be moved
  with `snapTo` and `animateTo`. `enabled`, `interactionSource` and a `SliderIndicatorStroke` for
  the line are configurable. The thumb is an adjustable range for screen readers, named by the
  required `contentDescription`.
- `gesture-draggable`: `SwipeToConfirm`, a track with a thumb that is slid to its end to confirm an
  action. The thumb confirms once it was dragged past `confirmationThreshold`, no matter how fast it
  is released, and the track can draw a progress fill behind the thumb through
  `SwipeToConfirmDefaults.colors`. Screen readers see it as a button that confirms when activated,
  and `confirmedStateDescription` is announced once confirmed.
- `SwipeToRevealState.reveal()`, `hide()` and `isRevealed` reveal and hide the actions of
  `SwipeToReveal` from code and tell whether they have settled revealed.
- `SwipeToRevealContentScope.revealSemantics()` offers custom accessibility actions that reveal and
  hide the actions of `SwipeToReveal`. Apply it to the root of the content, as content that merges
  its own semantics is focused on its own by screen readers.
- The demo has a mail inbox sample for `SwipeToReveal`, also controlled from code.
- `CollapsibleHeaderContainerState.expand()`, `collapse()` and `isCollapsed` expand and collapse the
  header from code and tell whether it has settled collapsed.
- `rememberCollapsibleHeaderContainerState` and `rememberExpandableContentState` take an
  `initialValue`.
- `ExpandableContentState.isExpanded` tells whether the layout has settled expanded, and
  `ExpandableContent` takes `enabled` to turn off dragging.
- The demo has samples that control the header and the expandable content from code.

### Changed

- `AnimatedColumn` describes itself to accessibility services as a list with one row per item, and
  hides items from them while they animate out.
- The `insets`, `collapsedHeader` and state documentation of the header containers is more
  detailed, and the slot lambdas name their `PaddingValues` parameters `headerInsets` and
  `contentInsets`.

## [0.2.1] - 2026-10-31

### Added

- `layout-animated`: a new artifact with `AnimatedColumn`, a column that animates its items to
  their new position when the list is sorted, filtered or changed. `animateItem` fades items in
  and out and slides them to their place, and takes a spec for each animation.
- A baseline profile for `layout-animated`.

### Changed

- The baseline profile of `gesture-draggable` is regenerated for the changes since 0.1.0.

## [0.2.0] - 2026-10-31

### Added

- `ExpandableContentState.expand()` and `collapse()` animate the layout to a state from code.

### Fixed

- `ExpandableContent` and the header containers throw a clear `UnsupportedOperationException` when
  they are measured with intrinsics, instead of failing with an unrelated error.
- `ExpandableContent` no longer reads an unset drag offset before the first layout pass, which
  could drive the transition with an invalid progress.
- `ExpandableContent` anchors follow the drag target, so a programmatic move keeps its target when
  the content is measured again.
- `AnimatableCollapsibleHeaderContainer` creates a new header scope when its transition scope
  changes, instead of keeping a stale one.

## [0.1.0] - 2026-10-28

### Added

- `CollapsibleHeaderContainer`: a header above the content that collapses with scrolling and
  switches between an expanded and a collapsed header.
- `AnimatableCollapsibleHeaderContainer`: the same container, with a transition between both
  headers that follows the drag.
- `ExpandableContent`: a bottom layout that is dragged from a compact bar into a full screen, like
  a mini player.
- Android and iOS support for `gesture-draggable`.
