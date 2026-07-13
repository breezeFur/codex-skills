---
name: codegraph
description: Use local CodeGraph tools for repository code understanding. Use when the user asks how code works, architecture questions, bug flow, call chains, callers/callees, impact analysis, related symbols, project file layout, or before making code changes that depend on understanding cross-file relationships. Prefer CodeGraph before broad text search when a .codegraph index is available.
---

# CodeGraph

## Overview

Use this skill to inspect code structure with local CodeGraph before doing broad manual file search. It is especially useful for architecture, bug tracing, call flow, and impact analysis.

## Priority

For Java backend development, pair CodeGraph with `java-dev`, `java-code-style`, and `OpenSpec` by default. CodeGraph explains the current repository structure, symbols, callers, callees, and impact surface; `java-dev` and `java-code-style` still govern Java architecture, module placement, constants, logging, comments, exceptions, and style rules. OpenSpec participates by default as a lower-priority helper and must not replace CodeGraph for code understanding when a `.codegraph/` index exists.

## First Move

For any "how does this work", architecture, bug, or flow question, call `codegraph_context` first with a concise task description.

Good task descriptions:

```text
How does request authentication reach UserContext?
Why can order status remain pending after payment callback?
Find the code path from Controller submitAnswer to result persistence.
What changes are impacted if UserService.createUser changes?
```

Use the returned files, symbols, callers, callees, and snippets as the first source of truth.

## Tool Selection

- Use `codegraph_context` first for broad task context, architecture questions, bug investigation, and feature changes.
- Use `codegraph_trace` when the question is "how does A reach B" or a request/update/render/data path needs a chain.
- Use `codegraph_callers` to find who invokes a function, method, or class.
- Use `codegraph_callees` to see what a function, method, or class invokes.
- Use `codegraph_impact` before refactoring or changing shared symbols.
- Use `codegraph_search` to find candidate symbols by name before exploring them.
- Use `codegraph_explore` to read several related symbols/files in one capped call after finding relevant names.
- Use `codegraph_node` for one symbol's signature, location, callers/callees, and body.
- Use `codegraph_files` for indexed file layout before `rg --files` when the project has CodeGraph initialized.
- Use `codegraph_status` only when debugging whether the index exists or looks stale.

## Project Path

If the user names a repository outside the current workspace, pass `projectPath` to CodeGraph tools when that repository has `.codegraph/` initialized.

If CodeGraph has no index for the target project, say that briefly and fall back to normal repository inspection with `rg`, `rg --files`, and direct file reads.

## Reading Rules

- Do not re-open files or snippets that CodeGraph already returned unless you need additional surrounding lines.
- Prefer one CodeGraph call that gathers related context over many narrow shell reads.
- After CodeGraph identifies relevant files, use `rg` or direct reads only for exact text, configuration, generated files, or unindexed content.
- When CodeGraph and text search disagree, inspect the actual source file before editing.

## Editing Workflow

Before code edits that may affect multiple files:

1. Use `codegraph_context` for task context.
2. Use `codegraph_impact` on the symbol being changed when it is shared.
3. Use `codegraph_trace` for request/data flow changes.
4. Edit only after the dependency shape is clear.
5. Verify with focused tests, compile, or targeted static checks.

## References

- Read `references/tool-selection.md` for compact examples of which CodeGraph tool to use.
