# Issue #6325 — Qualification

## Identity

- Project: LangChain4j
- Issue: #6325
- Title: `[BUG] Elasticsearch script search loses vector matches when text-only documents are indexed`
- URL: https://github.com/langchain4j/langchain4j/issues/6325
- State at check: **OPEN**
- Checked: 2026-09-07 UTC
- Reporter: `liziing`

## Reported Contract

The report states that Elasticsearch script-score vector search can execute cosine similarity against documents that have no vector field. Text-only documents are supported by public store APIs, so a mixed index can cause:

- a full shard/search failure; or
- partial shard failure that returns HTTP 200 while silently losing valid vector matches.

The expected behavior is to exclude documents without a vector from script vector scoring while preserving text-only documents for full-text retrieval.

## G0 Upstream Evidence

### Issue discussion

A second contributor asked to be assigned.

The reporter then explicitly replied that the fix and regression tests had already been implemented and that a PR was being prepared.

### Existing fix

An active upstream pull request now exists:

- PR: #6326
- Title: `fix(elasticsearch): skip text-only documents in script vector search`
- URL: https://github.com/langchain4j/langchain4j/pull/6326
- Author: `liziing`
- State at check: **OPEN**
- Draft: no
- Linked with: `Closes #6325`
- Changed files: 2
- Commits: 1

The PR directly implements the same fix direction described by the issue and adds multiple integration regressions.

## Competition / Duplicate Decision

This is not merely another contributor expressing interest. The issue reporter has already submitted a concrete fix PR.

Continuing to implement the same patch would duplicate active upstream work and create avoidable collision.

## Decision

**DROP**

Reason: **active upstream fix already exists before our implementation phase.**

This case is intentionally stopped at **G0 — Qualification**.

## Re-entry Condition

Only reconsider this issue if all of the following become true:

1. PR #6326 is closed without merge or replacement;
2. issue #6325 remains actionable;
3. maintainer feedback indicates additional work is needed;
4. our proposed work is materially distinct from the abandoned/rejected approach.

Until then, do not implement a competing patch.
