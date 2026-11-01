# Contributing to Strata

Thanks for your interest in Strata. Bug reports, ideas and pull requests are welcome.

## Getting started

- Open an issue before you start on a larger change, so we can agree on the direction first.
- The [demo](./demo) module shows every component in all its states. Use it to try your change.
- Run `./gradlew detekt` before you commit. The `.githooks/pre-commit` hook runs it for you, enable
  it with `git config core.hooksPath .githooks`.
- Changes that users of the library can notice are listed in the [CHANGELOG](./CHANGELOG.md). The
  maintainer adds the entry when preparing a release.

## How Strata is written

Most of the code of the components is written by hand. AI tools, currently Claude Code, support the
work: they help with research, boilerplate, tests, documentation and review. They don't design the
components or decide what gets merged. The maintainer reads and understands every line before it is
committed, and is responsible for it.

Commits that were made with the help of an AI tool can carry a `Co-Authored-By` trailer, but they
don't have to. The trailer says nothing about how much of a change came from a tool, and its
absence doesn't mean that no tool was used. This page is the disclosure for the whole project.

## Using AI tools in your contributions

You are welcome to use AI tools for your pull requests, under the same rules as everyone else:

- **You are the author.** You are responsible for the change, whichever tools helped to write it.
  Understand it well enough to explain and defend it in review.
- **Test it.** Run the code and try it in the demo app. Don't submit changes that you haven't run.
- **Mind the license.** Strata is licensed under [Apache 2.0](./LICENSE). Only submit code that you
  may contribute under this license, and don't paste in output that reproduces code under an
  incompatible license.
- **Keep it small.** Pull requests that are large, generated and unreviewed will be closed. Smaller
  changes that you understand are easier to review and merge.
- **Be open when it matters.** If a tool wrote a substantial part of your change, say so in the pull
  request description. You don't need to add a trailer to your commits for this.

## Pull requests

- Keep a pull request focused on one change.
- Follow the style of the surrounding code and the detekt rules in [`config`](./config).
- Describe what changed and why, and mention how you tested it.

By contributing, you agree that your contribution is licensed under the Apache License 2.0.
