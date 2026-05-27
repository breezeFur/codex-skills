---
name: java-code-style
description: Java coding style and cleanup rules for backend projects. Use when reviewing, generating, or refactoring Java code for constants instead of magic values, SLF4J logging, meaningful comments, Lombok consistency, naming, enum placement, shared constants under the framework constants package, avoiding noisy comments, and aligning code style with an existing Java repository.
---

# Java Code Style

## Workflow

1. Inspect nearby code first: naming, Lombok usage, logger style, constants location, enum style, and comment language.
2. Follow existing project style unless it conflicts with the rules below.
3. Keep changes local and mechanical for style-only tasks.
4. Prefer clear names and small constants over clever abstractions.
5. Run the smallest relevant compile or test command when style changes can affect behavior.

## Constants And Magic Values

- Do not leave meaningful magic values in business code.
- Extract meaningful numbers, strings, Redis keys, status codes, default values, limits, retry counts, provider names, header names, request attribute names, field names, and TTLs into named constants.
- Keep constants close to their usage when only one class uses them: `private static final`.
- Move constants shared across classes or modules into the framework module under the `constants` package so every business module can reuse them through the framework dependency.
- Prefer enums for closed business states instead of loose string constants.
- Keep highly domain-specific enums in the owning business module unless multiple modules need them.
- Do not extract trivial loop counters or universally obvious values such as `0` and `1` unless they carry business meaning.
- Name constants by meaning, not by value. Use `DEFAULT_PAGE_SIZE`, not `TEN`.

## Logging

- Use SLF4J only.
- Prefer Lombok `@Slf4j` when the project uses Lombok logging; otherwise use `LoggerFactory`.
- Do not use `System.out`, `System.err`, `printStackTrace`, or ad hoc console logging.
- Log expected business exceptions at `warn` when they represent abnormal user input or state.
- Log uncaught exceptions at `error` with the exception object.
- Include stable identifiers such as user ID, request ID, entity ID, job ID, or cache key when useful.
- Do not log secrets, passwords, access tokens, refresh tokens, authorization headers, or full sensitive payloads.

## Comments

- Add concise comments for key business rules, non-obvious branches, important SQL/cache decisions, concurrency behavior, and integration assumptions.
- Explain why a block exists or what invariant it protects.
- Do not add comments that merely restate Java syntax.
- Do not comment obvious getters, setters, constructors, simple assignments, or one-line plumbing.
- Preserve the existing project comment language. Use Chinese comments when the surrounding code and OpenAPI descriptions are Chinese.

## Lombok And Data Classes

- Use Lombok consistently with nearby code.
- Use `@Data` for simple mutable DTO, VO, and Entity classes when this matches project style.
- Use `@Getter`, `@Setter`, `@Builder`, or records only when the project already prefers them.
- Do not mix record-style and mutable Lombok style randomly in the same module.
- Avoid Lombok on classes with important custom invariants unless the generated methods are clearly safe.

## Naming

- Prefer names that describe business meaning rather than implementation detail.
- Name booleans as predicates, for example `enabled`, `active`, `deleted`, `supportsRetry`.
- Name methods by action and result, for example `listActiveUsers`, `getRequiredProfile`, `refreshToken`.
- Avoid abbreviations unless they are already project-standard.

## Review Checklist

- No meaningful magic values remain inline.
- Shared constants are in the framework `constants` package.
- Closed business states use enums where appropriate.
- Logs use SLF4J and do not expose sensitive data.
- Comments explain key rules rather than syntax.
- Lombok usage matches nearby code.
- Style-only changes do not alter behavior.
