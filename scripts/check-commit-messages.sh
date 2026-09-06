#!/usr/bin/env bash
# Fail if commits in range have IDE/agent Co-authored-by trailers
# or unexpected author identity.
set -euo pipefail

BASE="${1:-}"
HEAD="${2:-HEAD}"

if [[ -z "$BASE" ]]; then
  echo "usage: $0 <base-sha> [head-sha]" >&2
  exit 2
fi

EXPECTED_NAME="Kleilson Santos"
EXPECTED_EMAIL="kdsdesign1@gmail.com"
fail=0

while IFS= read -r hash; do
  [[ -z "$hash" ]] && continue
  body="$(git log -1 --format='%B' "$hash")"
  author="$(git log -1 --format='%an <%ae>' "$hash")"
  if printf '%s' "$body" | grep -Ei 'Co-authored-by:.*Cursor|cursoragent@cursor\.com' >/dev/null; then
    echo "FAIL $hash  IDE/agent Co-authored-by trailer (author is ${EXPECTED_NAME} <${EXPECTED_EMAIL}> only)"
    fail=1
  fi
  if [[ "$author" != "${EXPECTED_NAME} <${EXPECTED_EMAIL}>" ]]; then
    echo "FAIL $hash  unexpected author '$author' (want ${EXPECTED_NAME} <${EXPECTED_EMAIL}>)"
    fail=1
  fi
done < <(git rev-list "${BASE}..${HEAD}")

if [[ "$fail" -ne 0 ]]; then
  exit 1
fi

echo "OK commit attribution ${BASE}..${HEAD}"
