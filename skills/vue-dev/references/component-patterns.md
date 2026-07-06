# Component, Composable, And TypeScript Patterns

## Vue SFCs

- Use `<script setup lang="ts">` for new components.
- Keep templates readable: avoid complex inline expressions, nested ternaries, and large object literals in templates.
- Use `computed` for derived display values and `watch` only for side effects.
- Prefer `defineProps` and `defineEmits` with explicit types. Use `withDefaults` for optional prop defaults.
- Use `defineModel` only when the project already uses it or the two-way contract is genuinely simple.

## Component Boundaries

- Split a component when one file mixes unrelated concerns, grows hard to scan, or repeats UI logic.
- Do not split purely by line count; split by responsibility: container, presentational component, form, table/list, dialog, or stateful interaction.
- Keep presentational components prop-driven and side-effect free.
- Put fetch, route, permission, or store orchestration in page/feature containers or composables.

## Composables

- Use composables for reusable reactive behavior, not as dumping grounds.
- Name returned fields by business meaning.
- Clean up timers, event listeners, observers, and subscriptions in `onUnmounted`.
- Avoid hidden global mutable state in composables unless the composable is intentionally a singleton and documented by name.

## TypeScript

- Avoid `any`; prefer `unknown` and narrow it.
- Type API responses, route params, form models, table rows, emits payloads, and store state.
- Use string literal unions or enums for closed UI/domain states.
- Avoid boolean prop pairs that can conflict. Prefer one mode prop or a discriminated union.
- Keep business constants named by meaning, not value.
