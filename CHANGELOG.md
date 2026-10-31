# Changelog

All notable changes to Strata are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-10-28

### Added

- `CollapsibleHeaderContainer`: a header above the content that collapses with scrolling and
  switches between an expanded and a collapsed header.
- `AnimatableCollapsibleHeaderContainer`: the same container, with a transition between both
  headers that follows the drag.
- `ExpandableContent`: a bottom layout that is dragged from a compact bar into a full screen, like
  a mini player. `ExpandableContentState` can expand and collapse it programmatically.
- Android and iOS support for `gesture-draggable`.
