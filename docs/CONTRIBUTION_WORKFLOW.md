# Contribution Workflow

## Overview

Every contribution moves through seven gates. The purpose is to prevent wasted effort, weak root-cause claims, overbroad patches, and low-quality PRs.

## G0 — Qualification

Before modifying source:

- Confirm repository and issue identity.
- Confirm issue is still actionable.
- Inspect issue comments and maintainer guidance.
- Search open and closed PRs for an existing fix.
- Search recent commits/releases for silent resolution.
- Check whether another contributor is actively working on it.
- Estimate scope, environment requirements, and regression-test feasibility.
- Decide GO / HOLD / DROP.

Deliverables:
- `ISSUE.md`
- explicit decision and rationale

## G1 — Reproduction

Create a controlled reproduction.

Preferred order:

1. existing failing upstream test
2. minimal unit/integration test
3. minimal standalone reproducer
4. controlled real-world reproduction

Record:
- exact revision/version
- runtime/toolchain
- inputs and configuration
- expected behavior
- actual behavior
- logs / assertions / traces
- repeatability

A reproduction should be deterministic when the defect itself is deterministic.

## G2 — Root Cause

Build the causal chain from input to failure.

Required:
- affected call path
- relevant state transitions
- invariant or contract being violated
- exact point where incorrect state/behavior originates
- experiments that distinguish the leading hypothesis from alternatives

Avoid statements such as "this line is wrong" without explaining why it produces the reported behavior.

## G3 — Solution Design

Compare at least the plausible alternatives when more than one exists.

Evaluate:
- semantic correctness
- backward compatibility
- API behavior
- failure modes
- implementation scope
- maintainability
- performance
- testability
- consistency with upstream architecture

Choose the smallest solution that restores the intended invariant without creating a second problem.

## G4 — Validation

Validation must be proportional to the risk.

Baseline:
- reproduction fails before patch
- reproduction passes after patch
- regression test added where feasible
- directly related tests pass
- project-required checks pass

Risk-driven additions:
- boundary tests
- null/empty/error paths
- concurrency or ordering tests
- repeated/stress tests
- version/platform matrix
- performance comparison
- randomized/property-based testing
- protocol interoperability
- long-running leak/resource tests

Record both positive and negative evidence.

## G5 — Upstream Readiness

Before PR creation:

- read CONTRIBUTING and relevant maintainer docs
- inspect comparable recent accepted PRs
- audit final diff
- remove unrelated changes
- check generated files and lockfiles
- confirm tests from a clean state where practical
- prepare concise problem/root-cause/fix/test narrative
- link the issue using the upstream project's preferred convention

## G6 — Outcome Tracking

After submission record:

- PR URL
- branch / commits
- CI results
- review comments
- requested changes
- final state: OPEN / MERGED / CLOSED
- merge commit/release if applicable
- lessons learned
- resume-worthy value, if any

A closed/rejected PR can still be a valuable case if the technical investigation was strong. Record why it was not accepted.
