# Project Selection Standard

## Purpose

Choose projects where sustained contribution improves backend engineering capability and produces credible public evidence.

## Priority Domains

### Tier 1

- Java / Spring ecosystem
- messaging and streaming
- databases / CDC / storage
- distributed systems
- networking
- observability
- cloud-native infrastructure

### Tier 2

- Agent / AI infrastructure with strong backend/runtime relevance
- IoT / MQTT / device infrastructure
- search systems and data infrastructure

### Tier 3

- vertical-domain projects such as GIS, media, or specialized tooling, only when the issue has unusually strong engineering value or directly supports an active project.

## Scoring

Score each dimension from 0-10.

| Dimension | Weight | Question |
| --- | ---: | --- |
| Enterprise relevance | 20% | Is the project used in production or representative of production-grade engineering? |
| Backend relevance | 20% | Does contribution strengthen transferable backend/system skills? |
| Issue depth potential | 20% | Does the project contain meaningful correctness, runtime, protocol, data, concurrency, or systems work? |
| Reproducibility | 15% | Can issues normally be reproduced and tested locally/CI? |
| Testability | 10% | Can fixes be protected with regression tests? |
| Public career signal | 10% | Would a merged contribution be understandable and credible to an interviewer? |
| Cost efficiency | 5% | Is the setup/review burden reasonable relative to value? |

```text
weighted_score =
  enterprise * .20 +
  backend * .20 +
  depth * .20 +
  reproducibility * .15 +
  testability * .10 +
  career_signal * .10 +
  cost_efficiency * .05
```

## Interpretation

- **8.0-10.0**: strategic target
- **7.0-7.9**: strong candidate
- **6.0-6.9**: selective; issue quality must compensate
- **< 6.0**: normally deprioritize

## Important Correction

Project popularity is not a substitute for issue quality.

A smaller project with a reproducible concurrency defect and maintainable regression test can be more valuable than a trivial documentation PR in a famous repository.
