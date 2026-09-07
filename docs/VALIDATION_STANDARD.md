# Validation Standard

## Principle

Validation strength must match the claim and the failure mode. A patch is not "verified" merely because compilation succeeds or one test passes.

## Evidence Levels

### L1 — Problem Existence

Prove that the reported defect exists on a relevant revision.

Acceptable evidence:
- upstream failing test
- minimal deterministic test
- standalone reproducer
- controlled integration reproduction
- trace/log evidence tied to a testable contract

Record both expected and actual behavior.

### L2 — Root Cause

Prove why the failure occurs.

Required:
- causal call/state path
- violated invariant/contract
- exact origin of incorrect behavior
- experiments that distinguish the leading explanation from plausible alternatives

Useful techniques:
- targeted instrumentation
- controlled input mutation
- state inspection
- branch forcing
- version comparison
- git bisect
- dependency isolation
- protocol capture

### L3 — Fix Correctness

Show that the proposed change restores the intended invariant rather than merely masking the symptom.

Where multiple solutions exist, compare them for:
- semantic correctness
- scope
- compatibility
- failure behavior
- maintainability
- performance
- architectural fit

### L4 — Regression Safety

Baseline requirements:
- [ ] reproduction fails before patch
- [ ] reproduction passes after patch
- [ ] regression test added where feasible
- [ ] directly related upstream tests pass
- [ ] required project checks pass

Add tests based on risk:

| Failure mode | Additional validation |
| --- | --- |
| concurrency / race | repeated, barrier/latch controlled, stress, ordering assertions |
| parsing / protocol | malformed, boundary, interoperability, version matrix |
| state machine | invalid transitions, retries, duplicate events, recovery |
| resource leak | repeated lifecycle, cleanup assertions, long-running/stress |
| compatibility | supported runtime/JDK/dependency/platform matrix |
| performance | before/after benchmark with controlled workload |
| numeric/algorithmic | boundary, randomized/property-based tests |
| persistence/data | rollback, idempotency, migration/backward compatibility |

## Experiment Quality

A good experiment:
- changes one important variable at a time,
- has a clear predicted result,
- can disprove the hypothesis,
- is repeatable,
- records environment and revision.

Avoid post-hoc explanations that fit every possible result.

## External References

Use papers, specifications, Javadocs, protocol standards, issue history, or upstream design docs when they materially strengthen the reasoning.

External authority does not replace local reproduction when the bug is reproducible.

## Reporting

`VALIDATION.md` should distinguish:

- **Confirmed**: directly observed/tested.
- **Supported inference**: evidence strongly indicates, but not directly observed.
- **Unknown**: not tested or unavailable.
- **Not applicable**: explain why.

Do not turn an untested assumption into a green checkmark.
