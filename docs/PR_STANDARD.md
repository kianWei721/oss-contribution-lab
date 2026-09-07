# Pull Request Standard

## Before Creating an Upstream PR

### Upstream Rules
- [ ] Read `CONTRIBUTING.md` and relevant developer docs.
- [ ] Inspect recent accepted PRs in the same subsystem.
- [ ] Confirm required tests and formatting/static-analysis commands.
- [ ] Confirm issue-linking and commit-message conventions.

### Diff Audit
- [ ] Every changed file is necessary.
- [ ] No unrelated refactor or formatting churn.
- [ ] No debug output.
- [ ] No local IDE/environment files.
- [ ] No credentials, tokens, private URLs, or personal paths.
- [ ] Generated/lock files changed only when required.
- [ ] Public API behavior is intentionally changed, if changed at all.

### Validation
- [ ] Original reproduction demonstrated before patch.
- [ ] Regression passes after patch.
- [ ] Relevant module tests pass.
- [ ] Project-required checks pass.
- [ ] Risk-specific tests completed or explicitly documented as not applicable.

## PR Narrative

A strong PR description should answer:

1. **Problem** — What observable behavior is wrong?
2. **Root cause** — Why does it happen?
3. **Fix** — What invariant/behavior does the patch restore?
4. **Tests** — How was the defect reproduced and the fix validated?
5. **Scope** — What intentionally was not changed?

Prefer concise, falsifiable claims over promotional language.

## Patch Shape

Prefer:
- one problem,
- one coherent patch,
- one regression test,
- minimal API surface change.

Avoid bundling cleanup simply because the affected file was already open.

## After Submission

Track:
- CI
- review comments
- requested changes
- maintainer reasoning
- merge/close outcome
- release inclusion if relevant

If maintainers reject the implementation, record whether the root-cause analysis was still valid and what architectural constraint changed the preferred solution.
