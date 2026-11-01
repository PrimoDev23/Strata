# AGENTS.md

Guidelines for working in Strata, a Kotlin Multiplatform (Android + iOS) Compose library of draggable,
layered layouts. They are extracted from the existing code, so follow the surrounding code when in doubt.

## Project layout

- `strata/gesture/draggable`: published library (`CollapsibleHeaderContainer`,
  `AnimatableCollapsibleHeaderContainer`, `ExpandableContent`). Packages: `bottombar`, `header`
  (`base`, `animatable`, `common`), `common`.
- `strata/layout/animated`: published library (`AnimatedColumn`).
- `strata/**/*-previews`, `strata/**/*-baselineprofile`: previews and baseline profile generators of a
  library. Every published module has a `*-baselineprofile` sibling.
- `demo`: Compose Multiplatform demo app. `androidApp` and `iosApp` are thin shells around it.
- `theme`: `StrataTheme`, shared by the demo and the previews.
- Versions live in `gradle/libs.versions.toml`, publishing metadata (`GROUP`, `VERSION_NAME`, POM) in the
  root `gradle.properties`.

## Verification

Run these before finishing a change; CI (`.github/workflows/pr-verification.yml`) runs the same set:

```bash
./gradlew detekt checkKotlinAbi compileAndroidMain compileDebugKotlin lint allTests --continue --console=plain -Pkotlin.native.ignoreDisabledTargets=true
```

- `./gradlew detektFormat` auto corrects formatting. The pre-commit hook (`.githooks/pre-commit`) runs
  `detekt`, so fix findings instead of bypassing the hook.
- Public API changes require regenerating the ABI dumps with `./gradlew updateKotlinAbi` and committing
  `api/` files.
- Add an entry to `CHANGELOG.md` (Keep a Changelog format) for user visible changes.

## Code style

Formatting is enforced by detekt with ktlint (`android_studio` code style) and the Compose rules; see
`config/detekt/detekt.yml`.

- 4 space indentation, lines up to 120 characters; KDoc and comments wrap at roughly 100.
- **Trailing commas** on every call site and declaration site that spans multiple lines.
- Use **named arguments** for calls with more than one argument and put each on its own line
  (`foo(
    state = state,
    modifier = modifier,
)`).
- Multi-line `if`/`else` always use braces. Wildcard imports are allowed but not used; imports are explicit.
- Long strings are split with a trailing `+` and the continuation indented by four spaces.
- Expression body for single-expression functions that fit; otherwise block body with an explicit
  `return`.
- Early returns for guard clauses (`if (delta >= 0) { return Offset.Zero }`) with a blank line after.
- Blank line between declarations and between logical blocks inside a function.
- `@Suppress` is used sparingly and only for known rules (e.g. `"ContentSlotReused"` where a slot is
  deliberately composed more than once). Explain such suppressions in a comment.
- `// TODO:` comments are tolerated but should say what and why.

## Naming

- Types are `PascalCase`; `@Composable` functions are `PascalCase` (detekt `FunctionNaming` ignores them).
- Remember functions are `rememberXxx` and live in the same file as the type they create.
- Constants are `UPPER_SNAKE_CASE` `private const val` at the top of the file, below the imports.
- Layout ids are an `internal enum class XxxLayoutId` in their own file with a KDoc per entry.
- Use full names, no abbreviations (`draggableState`, `currentHeaderHeight`).
- One top-level public type per file, named after the file. Related internal helpers
  (measure policy, scroll connection, layout ids, scope) get their own file with the type name.
- Demo samples are listed in `DemoSample` (enum with `component`, `category`, `title`, `description`);
  a new demo is a new entry plus a screen under its own package in `demo`.

## Library API design

- Libraries use **`explicitApi()`**: every public declaration has an explicit `public` modifier and
  everything not meant for consumers is `internal` or `private`. The demo and theme modules do not use
  explicit API.
- Keep the public surface minimal. State classes have an `internal constructor`, are created through a
  `rememberXxxState()` function and expose only intentional functions (`expand()`, `collapse()`).
- Scope classes (`ExpandableContentScope`) are public classes with an `internal constructor` that
  delegate (`by`) to the underlying scopes.
- State holders are annotated `@Stable` (or `@Immutable` for plain data) and are `rememberSaveable` with
  a `Saver` in a `companion object` when the settled state should survive configuration change and process
  death.
- The standard composable signature is: required parameters, then `modifier: Modifier = Modifier`, then
  optional parameters with defaults (including `state`), then the trailing content lambda.
- Content slots are `@Composable` lambdas, possibly with a receiver scope.
- Layout is done with custom `MeasurePolicy`s (an `internal class` plus a `rememberXxxMeasurePolicy`
  function). Read drag offsets during measurement, not composition, so dragging remeasures without
  recomposing. Unsupported intrinsics throw `UnsupportedOperationException` with an explanation.
- Validate preconditions with `require(...)` and missing pieces with `error("... was not defined ...")`
  and clear messages.
- Shared code lives in `commonMain`; there is only little platform code.

## Documentation

- Every public declaration has KDoc. Internal declarations that carry non-obvious behavior do too, one or
  two sentences.
- KDoc explains behavior and consequences, not just restates the name: what the user observes, side
  effects, caveats (for example that slot content is composed more than once). Use `[Type]` links,
  `@param`, `@receiver`, `@return`, bullet lists and `##` sections for longer docs.
- Inline comments explain **why**, not what, and are written as full sentences without a trailing period
  for single-line comments.
- Keep `README.md` snippets and versions in sync with `VERSION_NAME`; the release check script verifies it.

## Compose conventions

- Prefer `remember(keys)` with explicit keys for scopes and policies; pass `Modifier` first among optional
  parameters and apply the incoming `modifier` to the outermost layout.
- Use `Modifier` chains with one call per line.
- Animations must follow the drag: use `SeekableTransitionState` and `animateEnterExit`/`sharedElement`
  in `ExpandableContentScope`, with `tween` specs on one shared timeline instead of springs.
- Collect flows exposed by state classes with `collectAsState`.
- Previews are `private` functions annotated `@Preview` without size parameters, wrapped in
  `StrataTheme`, live in the `*-previews` module (one file per component) and may use `private const val`
  for their timings.

## Demo app

- Package per sample area (`collapsible`, `expandable`, `reorder`, `home`, `navigation`, `catalog`,
  `common`). Shared pieces go to `common`.
- Navigation 3 with serializable `Route`s (`sealed interface Route : NavKey`).
- Baseline profile generators locate demo screens by text. Keep demo package names from starting with a
  library package (`dev.leonp.strata.animated`), or demo classes end up in the library profile.

## Build files

- Version catalog aliases only (`libs.…`); no hardcoded versions in module build files.
- New published modules need `mavenPublish` with `coordinates`, the `group` line, `abiValidation()`,
  committed ABI dumps, a `*-baselineprofile` sibling listed in `settings.gradle.kts` and a `baselineProfile`
  block with a package filter. Copy them from an existing module.
- Comments in build files explain why a setting exists.

## Git and releases

- Work on a branch off `master` and open a PR; `master` is released by tags (`0.2.1`, no `v` prefix).
- Commit messages are short imperative sentences ("Add a release pipeline that ...").
- Releases are prepared with the `publish-library` skill (`.claude/skills/publish-library/SKILL.md`),
  which never pushes or uploads on its own.
