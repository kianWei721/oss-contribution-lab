# AGENTS.md

## Mission

This repository defines the operating procedure for high-quality open-source contribution work.

When working on a candidate project or issue, do not jump directly to implementation. Follow the quality gates in order and leave durable evidence in this repository.

## Non-Negotiable Rules

1. **Do not assume the issue report is correct.**
   Reproduce the behavior and verify the affected version/path.

2. **Check for duplicate work first.**
   Search issues, open/closed PRs, recent commits, release notes, and relevant discussions before implementing.

3. **Separate observation from explanation.**
   Record:
   - observed behavior,
   - expected behavior,
   - confirmed facts,
   - hypotheses,
   - experiments,
   - conclusion.

4. **No fix without a causal model.**
   A change that makes one test pass is insufficient if the underlying mechanism is not understood.

5. **Prefer minimal patches.**
   Avoid unrelated refactors, formatting churn, opportunistic cleanup, dependency upgrades, or API redesign unless required by the fix.

6. **Validation must cover the claim.**
   At minimum:
   - failing behavior before the fix,
   - passing behavior after the fix,
   - regression test where feasible,
   - relevant upstream test suite.
   Add boundary, concurrency, compatibility, performance, randomized, or property-based tests when the failure mode requires them.

7. **Respect upstream conventions.**
   Read CONTRIBUTING, test instructions, commit/PR conventions, style rules, and maintainer guidance before preparing a PR.

8. **Audit the final diff.**
   Verify every changed file is necessary and that generated/local/secret files are absent.

9. **Do not expose private information.**
   Never commit tokens, credentials, private infrastructure, proprietary source, internal hostnames, personal secrets, or local machine-specific data.

10. **Evidence over confidence.**
    If a conclusion is uncertain, state the uncertainty and specify the missing evidence.

## Required Workflow

```text
G0 Qualification
 -> G1 Reproduction
 -> G2 Root Cause
 -> G3 Solution Design
 -> G4 Validation
 -> G5 PR Readiness
 -> G6 Outcome Tracking
```

Do not skip a gate silently.

## Decision Labels

- **GO**: evidence supports active investment.
- **HOLD**: promising, but blocked by missing information, environment, maintainer direction, or competing work.
- **DROP**: already solved, duplicate, irreproducible after reasonable investigation, too low value, or disproportionate scope.

## Commit Discipline

Changes to this lab should be small and attributable to one purpose:
- workflow/doc improvement,
- candidate screening batch,
- one issue case update,
- one contribution outcome update.

## Working With Upstream Repositories

Upstream source code should normally live in a separate clone/fork. This repository records the evidence and process, not a vendored copy of the upstream project.
