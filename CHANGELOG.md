# Changelog

All notable changes to Strata are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.1] - 2026-10-31

### Added

- `layout-animated`: a new artifact with `AnimatedColumn`, a column that animates its items to
  their new position when the list is sorted, filtered or changed. `animateItem` fades items in
  and out and slides them to their place, and takes a spec for each animation.

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
