#!/usr/bin/env bash
# Fail if commits in range have IDE/agent Co-authored-by trailers
# or unexpected author identity on content commits.
set -euo pipefail

BASE="${1:-}"
HEAD="${2:-HEAD}"

if [[ -z "$BASE" ]]; then
  echo "usage: $0 <base-sha> [head-sha]" >&2
  exit 2
fi

EXPECTED_NAME="Kleilson Santos"
EXPECTED_EMAIL="kdsdesign1@gmail.com"
# GitHub "Merge pull request" / gh pr merge --merge authors as account noreply.
GITHUB_NOREPLY='kleilsonsantos <63037173+KleilsonSantos@users.noreply.github.com>'
DEPENDABOT='dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>'
fail=0

while IFS= read -r hash; do
  [[ -z "$hash" ]] && continue
  body="$(git log -1 --format='%B' "$hash")"
  subject="$(git log -1 --format='%s' "$hash")"
  author="$(git log -1 --format='%an <%ae>' "$hash")"
  if printf '%s' "$body" | grep -Ei 'Co-authored-by:.*Cursor|cursoragent@cursor\.com' >/dev/null; then
    echo "FAIL $hash  IDE/agent Co-authored-by trailer (author is ${EXPECTED_NAME} <${EXPECTED_EMAIL}> only)"
    fail=1
  fi
  if [[ "$author" == "${EXPECTED_NAME} <${EXPECTED_EMAIL}>" ]]; then
    continue
  fi
  if [[ "$subject" == merge:* && "$author" == "$GITHUB_NOREPLY" ]]; then
    continue
  fi
  # Dependabot version bumps (chore(deps): / chore(deps-dev):)
  if [[ "$author" == "$DEPENDABOT" && ( "$subject" == chore\(deps\):* || "$subject" == chore\(deps-dev\):* ) ]]; then
    continue
  fi
  echo "FAIL $hash  unexpected author '$author' (want ${EXPECTED_NAME} <${EXPECTED_EMAIL}>)"
  fail=1
done < <(git rev-list "${BASE}..${HEAD}")

if [[ "$fail" -ne 0 ]]; then
  exit 1
fi

echo "OK commit attribution ${BASE}..${HEAD}"
