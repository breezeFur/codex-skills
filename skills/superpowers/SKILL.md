---
name: superpowers
description: Bridge to installed Superpowers methodology skills for design and execution discipline. Use when the user mentions Superpowers, brainstorming, implementation plans, TDD, systematic debugging, subagent-driven development, code review, verification before completion, finishing a development branch, or when a complex task needs structured design and execution. User custom skills and AGENTS.md rules take priority over Superpowers.
---

# Superpowers

## Overview

Use this skill as a thin bridge to the installed Superpowers workflow skills. Do not vendor the upstream Superpowers repository into the custom skill sync repo; keep this local skill as the stable routing and priority rule.

## Priority

- User instructions, project `AGENTS.md`, global `AGENTS.md`, and user-authored skills under `$env:USERPROFILE\.codex\skills` take precedence.
- For Java backend development, participate by default alongside `java-dev` + `java-code-style` + `codegraph` + `OpenSpec`. Superpowers may shape the work process, but it does not decide Java architecture, module placement, constants, logging, comments, exceptions, DTO/VO placement, DAO rules, or verification strategy.
- At task kickoff, name the relevant Superpowers method such as brainstorming, writing plans, TDD, systematic debugging, code review, or verification. For small clear tasks, state that only lightweight verification discipline is needed.
- Superpowers is an execution-methodology layer, not a replacement for local skills such as `java-dev`, `java-code-style`, `powershell-codex`, `mysql-db-ops`, `codegraph`, `openspec`, or `skill-manage-github`.
- Simple and explicit tasks do not require the full Superpowers workflow unless the user asks for it.

## Installed Location

The local Superpowers checkout is expected at:

```text
$env:USERPROFILE\.codex\superpowers
```

The cross-runtime skills junction is expected at:

```text
$env:USERPROFILE\.agents\skills\superpowers
```

## Routing

- New idea, unclear product direction, or "help me design this": use Superpowers brainstorming style.
- Approved design that needs implementation steps: use writing-plans.
- Executing an existing plan: use executing-plans or subagent-driven-development when subagents are available and useful.
- Bug investigation: use systematic-debugging before changing code.
- Behavior change with meaningful risk: use test-driven-development where the repository supports tests.
- Before finishing non-trivial work: use verification-before-completion.
- Branch completion or handoff: use finishing-a-development-branch.

If native Superpowers skills are visible in the current Codex session, prefer those specific skills. If they are not visible, follow the matching workflow principles from this bridge without pretending a missing tool exists.

## Boundaries

- Do not allow Superpowers to override local Java, PowerShell, MySQL, CodeGraph, OpenSpec, or skill-sync conventions.
- Do not add plan/TDD overhead to very small, clear tasks unless risk justifies it.
- Do not copy or commit the upstream `$env:USERPROFILE\.codex\superpowers` repository into the custom skills sync repo.
- Do not fabricate unavailable subagent, todo, or review tools. Use Codex-native tools when available; otherwise continue inline and report the limitation.
