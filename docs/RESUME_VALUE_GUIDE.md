# Resume Value Guide

## Purpose

Assess whether a contribution is meaningful enough to mention in a resume or interview.

## High Signal

A contribution is high-signal when you can explain:

```text
production-relevant failure
  -> reproducible evidence
  -> root cause
  -> engineering trade-off
  -> minimal fix
  -> regression protection
  -> upstream review outcome
```

Examples:
- concurrency/order defect with deterministic regression test
- protocol compatibility fix
- data-integrity/correctness bug
- resource lifecycle leak
- framework/runtime edge case
- distributed-state recovery defect

## Medium Signal

Useful contributions that show codebase navigation and disciplined delivery but limited systems depth:
- non-trivial API correctness
- test infrastructure improvement
- meaningful error-handling fix
- performance improvement with measurements

## Low Signal

Normally do not feature prominently:
- typo/docs-only PR
- formatting
- dependency bump
- generated changes
- trivial null check without deeper context
- PRs whose technical contribution cannot be explained clearly

## Status Wording

Be exact:

- merged -> "Merged upstream PR ..."
- open -> "Submitted upstream PR ..."
- closed/unmerged -> describe investigation/fix only if it remains technically valuable; do not imply acceptance.

Repository popularity amplifies a strong contribution, but does not transform a weak contribution into a strong one.
