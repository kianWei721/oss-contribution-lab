# OSS Contribution Lab

A reproducible engineering workflow for discovering, evaluating, reproducing, fixing, validating, and upstreaming high-value open-source issues.

This repository is **not** a collection of cloned upstream source trees. It stores the contribution process, evidence, decisions, experiments, and outcomes for each case.

## Goals

- Select OSS projects and issues with meaningful engineering value.
- Avoid duplicate or already-solved work before implementation.
- Require reproducible evidence before changing code.
- Separate symptom, root cause, fix, and validation.
- Prefer regression tests and minimal, upstream-friendly patches.
- Track PR review, CI, merge status, and lessons learned.
- Make the workflow executable by humans and coding agents.

## Core Pipeline

```text
Discover
  -> Project Qualification
  -> Issue Qualification
  -> Duplicate / Existing-Fix Check
  -> Reproduction
  -> Root-Cause Analysis
  -> Solution Comparison
  -> Implementation
  -> Regression / Boundary / Compatibility Validation
  -> Diff Audit
  -> Upstream PR
  -> Review / CI / Merge Tracking
  -> Knowledge Capture
```

A contribution is not considered complete merely because a patch compiles.

## Quality Gates

| Gate | Required evidence |
| --- | --- |
| G0 - Qualification | Issue is open, relevant, scoped, and not already solved |
| G1 - Reproduction | Failure reproduced with a minimal or controlled case |
| G2 - Root Cause | Causal chain identified and competing hypotheses excluded |
| G3 - Fix Design | Alternatives compared; patch scope justified |
| G4 - Validation | Regression, related tests, boundaries, compatibility as applicable |
| G5 - Upstream Readiness | Clean diff, contribution rules checked, PR narrative prepared |
| G6 - Outcome | CI/review/merge status and follow-up recorded |

## Repository Layout

```text
.
├── AGENTS.md
├── CLAUDE.md
├── docs/
│   ├── CONTRIBUTION_WORKFLOW.md
│   ├── PROJECT_SELECTION.md
│   ├── ISSUE_SELECTION.md
│   ├── VALIDATION_STANDARD.md
│   ├── PR_STANDARD.md
│   └── RESUME_VALUE_GUIDE.md
├── templates/
│   ├── project-review.md
│   ├── issue-review.md
│   ├── reproduction-plan.md
│   ├── root-cause-analysis.md
│   ├── validation-report.md
│   ├── pr-review.md
│   └── contribution-report.md
├── candidates/
├── projects/
├── knowledge/
└── reports/
```

## Target Project Priorities

Primary focus:

1. Java / Spring ecosystem
2. Databases, CDC, caches, messaging
3. Distributed systems and networking
4. Observability
5. Cloud native / CI/CD
6. Agent and AI infrastructure with strong backend relevance
7. IoT / MQTT
8. Vertical domains only when the issue has unusually high technical value

Issue quality matters more than stars.

## What We Prefer

High-value cases include:

- correctness bugs
- concurrency or ordering defects
- data consistency / integrity problems
- resource leaks
- protocol or compatibility defects
- distributed-state issues
- observability correctness
- framework/runtime edge cases
- regressions with a clear testable contract

Low-priority work includes trivial typos, formatting-only changes, mechanical dependency bumps, or patches with little diagnostic value.

## Case Structure

Each issue is recorded as an evidence-backed case:

```text
projects/<project>/issues/<issue-id>/
├── ISSUE.md
├── REPRODUCTION.md
├── ROOT_CAUSE.md
├── SOLUTION.md
├── VALIDATION.md
└── PR.md
```

## Principle

> Reproduce before fixing. Prove the root cause. Keep the patch minimal. Make the validation stronger than the implementation claim.
