# CodeGraph Tool Selection

## Context

Use `codegraph_context` for:

- "How does X work?"
- "Where is this behavior implemented?"
- "Why is this bug happening?"
- "What files are related to this feature?"
- "What should I inspect before changing this?"

## Trace

Use `codegraph_trace` for:

- Controller to service to DAO paths.
- Request handler to response rendering paths.
- Event handler to persistence paths.
- State mutation to UI update paths.

Example:

```text
from: submitAnswer
to: saveResult
```

## Impact

Use `codegraph_impact` before changing:

- Shared services.
- DTOs or response wrappers.
- Framework utilities.
- Context holders.
- Mapper/DAO abstractions.
- Public API methods.

## Search And Explore

Use `codegraph_search` for symbol discovery, then `codegraph_explore` to read related files together.

Good search terms:

```text
AuthService login session
OrderStatus payment callback
RequestContext trackId MDC
```

## Fallback

Fallback to `rg` and direct reads when:

- The target repo has no `.codegraph/` index.
- The question is about config files, generated files, docs, or exact string matches.
- The CodeGraph result appears stale or incomplete.
