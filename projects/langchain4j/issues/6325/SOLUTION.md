# Solution

## Status

**NOT DESIGNED — stopped at G0.**

PR #6326 already implements the reported fix direction by requiring the vector field to exist before script scoring and composing that condition with metadata filtering.

This lab did not independently evaluate that implementation against alternatives because a competing upstream patch was already active.

If the issue becomes actionable again, compare at minimum:

1. filtering documents by vector-field existence before script scoring;
2. guarding the script itself against missing vectors, if supported and semantically appropriate;
3. any Elasticsearch query structure recommended by the supported-version contract.

Evaluate behavior, compatibility, metadata-filter composition, score semantics, and testability before choosing.
