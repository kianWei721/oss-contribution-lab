# CLAUDE.md

Use `AGENTS.md` as the primary operating policy for this repository.

## Working Style

When asked to evaluate or work on an OSS issue:

1. Read the repository workflow and relevant templates first.
2. Gather current upstream evidence before making implementation claims.
3. Do not treat issue text, comments, or previous analysis as ground truth without verification.
4. Prefer experiments that can falsify a hypothesis.
5. Keep a clear distinction between:
   - fact,
   - inference,
   - hypothesis,
   - recommendation.
6. Stop and mark HOLD/DROP when evidence shows the issue is solved, duplicated, misreported, or disproportionately scoped.
7. Do not create an upstream PR until G0-G5 are satisfied.
8. Record important findings in the case directory so another agent can continue without reconstructing the investigation.

## Output Preference

For technical investigations, lead with the decision or confirmed finding, then show the evidence and implications.

Do not inflate weak changes into "high-impact" contributions. Resume value is earned by the engineering problem and evidence, not by repository popularity alone.
