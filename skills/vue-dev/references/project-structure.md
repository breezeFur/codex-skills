# Project Structure And Module Boundaries

Use existing project layout first. For new Vue SPA projects, prefer a feature-oriented structure:

```text
src/
  app/                 # app bootstrap, providers, global setup
  router/              # route records, guards, lazy route modules
  pages/               # route-level page shells
  features/<name>/     # feature UI, composables, stores, api, model
  shared/
    api/               # shared HTTP client, interceptors, generated types
    components/        # cross-feature components
    composables/       # cross-feature composables
    constants/         # shared constants
    model/             # shared TypeScript types
    utils/             # pure helpers
  assets/
  styles/
```

## Placement Rules

- Route pages compose features and layout; they should not contain large business logic.
- Feature-local DTOs, VOs, UI models, stores, and constants stay inside the owning feature.
- Cross-feature types, constants, API clients, and composables move down to `shared` only when at least two features use them.
- Keep generated API types in a dedicated location such as `src/shared/api/generated` and do not manually edit generated files.
- Keep test files near the unit under test or in the existing test folder, matching project convention.

## Naming

- Components: `PascalCase.vue`.
- Composables: `useXxx.ts`.
- Stores: `useXxxStore.ts`.
- API modules: `<feature>Api.ts` or existing convention.
- Types: meaningful domain names, not `IThing` or `ThingType` unless the project already uses that style.
