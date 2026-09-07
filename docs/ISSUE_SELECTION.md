# Issue Selection Standard

## Hard Gates

An issue should not enter implementation until these checks are complete.

### Status

- [ ] Issue is open or otherwise explicitly accepted for work.
- [ ] The reported behavior still applies to a relevant revision/version.
- [ ] Maintainer comments do not reject the proposed direction.

### Duplicate / Competition Check

- [ ] No merged PR already fixes the issue.
- [ ] No open PR substantially solves the same problem.
- [ ] Recent commits/releases do not silently resolve it.
- [ ] No active contributor claim creates unreasonable collision risk.

### Reproduction Potential

- [ ] A local or CI reproduction path is available.
- [ ] Expected behavior can be stated as a testable contract.
- [ ] Required environment/dependencies are feasible.

### Engineering Value

Prefer:
- correctness
- concurrency / ordering
- data consistency
- resource lifecycle
- protocol compatibility
- distributed state
- observability correctness
- runtime/framework semantics
- regressions

Deprioritize:
- typo-only changes
- formatting-only work
- mechanical dependency bumps
- cosmetic refactors
- vague feature requests with no acceptance criteria
- defects requiring inaccessible proprietary infrastructure

## Issue Score

| Dimension | 0-10 |
| --- | ---: |
| Technical depth | |
| Reproducibility | |
| Regression-test potential | |
| Scope control | |
| Transferable learning | |
| Upstream acceptance probability | |
| Career signal | |
| Competition risk (reverse scored) | |

## Decision

### GO
The issue is actionable, evidence can be built, and expected value justifies the effort.

### HOLD
Potentially valuable, but missing maintainer clarification, reproduction environment, external dependency, or other critical evidence.

### DROP
Already solved, duplicate, low-value, irreproducible after reasonable effort, structurally blocked, or disproportionate in scope.

## Before Coding

Write the decision and supporting evidence into the case's `ISSUE.md`.

A "GO" is authorization to investigate and reproduce, not proof that the issue report or proposed fix is correct.
