#!/usr/bin/env bash
# Checks that a release tag is ready to be released.
# Usage: check-release-version.sh <tag> [default-branch]
# A leading "v" on the tag is optional, the default branch is master.
set -euo pipefail

tag="${1:?usage: check-release-version.sh <tag> [default-branch]}"
branch="${2:-master}"
version="${tag#v}"
failed=0

fail() {
    echo "::error::$1"
    failed=1
}

if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[0-9A-Za-z.-]+)?$ ]]; then
    echo "::error::Tag '$tag' is not a semantic version like v1.2.3"
    exit 1
fi

# The tagged commit must be part of the default branch, so nothing unmerged gets released
if git merge-base --is-ancestor "${GITHUB_SHA:-HEAD}" "origin/$branch"; then
    echo "Tagged commit is on $branch"
else
    fail "The tagged commit is not on $branch, merge it before releasing"
fi

# Library version, shared by all published modules
library="$(sed -n 's/^VERSION_NAME=//p' gradle.properties | tr -d '\r')"
if [[ "$library" == "$version" ]]; then
    echo "gradle.properties: VERSION_NAME=$library matches the tag"
else
    fail "gradle.properties has VERSION_NAME='$library', but the tag is '$version'"
fi

# Every dependency snippet in the README, like dev.leonp.strata:gesture-draggable:0.1.0
mapfile -t snippets < <(grep -oE 'dev\.leonp\.strata:[A-Za-z0-9._-]+:[0-9][A-Za-z0-9.+-]*' README.md || true)
if [[ ${#snippets[@]} -eq 0 ]]; then
    fail "README.md has no dev.leonp.strata dependency snippet to check"
fi
for snippet in "${snippets[@]}"; do
    if [[ "${snippet##*:}" == "$version" ]]; then
        echo "README.md: $snippet matches the tag"
    else
        fail "README.md has '$snippet', but the tag is '$version'"
    fi
done

# The release needs a section in the changelog, like "## [1.2.3] - 2026-01-31"
if grep -E '^## \[' CHANGELOG.md 2>/dev/null | grep -qF -- "[$version]"; then
    echo "CHANGELOG.md: has a section for $version"
else
    fail "CHANGELOG.md has no '## [$version]' section"
fi

# Maven Central never allows a version to be replaced, so it must not exist yet
group="$(sed -n 's/^GROUP=//p' gradle.properties | tr -d '\r')"
mapfile -t artifacts < <(git grep -hoE 'coordinates\(artifactId = "[^"]+"' -- '*/build.gradle.kts' | sed 's/.*"\(.*\)"/\1/')
if [[ ${#artifacts[@]} -eq 0 ]]; then
    fail "No published module found, expected coordinates(artifactId = ...) in a build file"
fi
for artifact in "${artifacts[@]}"; do
    url="https://repo1.maven.org/maven2/${group//.//}/$artifact/$version/$artifact-$version.pom"
    status="$(curl -s -o /dev/null -w '%{http_code}' --retry 3 "$url" || true)"
    case "$status" in
        404) echo "Maven Central: $group:$artifact:$version does not exist yet" ;;
        200) fail "$group:$artifact:$version is already on Maven Central" ;;
        *) fail "Could not check Maven Central for $group:$artifact:$version, got HTTP '$status' from $url" ;;
    esac
done

exit "$failed"
