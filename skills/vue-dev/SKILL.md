---
name: vue-dev
description: Generate and maintain Vue frontend applications using Vue 3, TypeScript, Vite, Vue Router, Pinia, Vitest, Playwright, API clients, component/module boundaries, admin/product UI conventions, landing/redesign visual quality, Chinese text safety, and verification. Use when creating or modifying Vue projects, pages, routes, components, composables, stores, API calls, forms, tables, dashboards, layouts, styles, tests, or frontend code that must align with existing project conventions. Default Vue workflow includes codegraph, OpenSpec, and design-taste-frontend where visual design applies.
---

# Vue Dev

## Priority And Skill Routing

For Vue frontend work, use this skill together with `codegraph`, `OpenSpec`, and `design-taste-frontend` by default:

- `vue-dev` owns Vue architecture, file placement, TypeScript boundaries, component contracts, state/API/routing choices, frontend error handling, and verification.
- `codegraph` owns repository understanding when `.codegraph/` exists: use it before broad text search for route flow, shared components, stores, API clients, and impact analysis.
- `OpenSpec` owns project specification and long-term memory checks: read or update specs for user flows, API contracts, permission rules, cross-module behavior, and long-lived product decisions. For local bug fixes or simple edits, explicitly state that no OpenSpec change is needed.
- `design-taste-frontend` owns visual taste for landing pages, portfolios, marketing pages, and redesigns. Do not force it onto dense dashboards, data tables, admin panels, or multi-step product UI unless the task includes visual redesign.

OpenSpec and design-taste-frontend are default participants but lower-priority helpers. They must be named in the task kickoff for Vue frontend work, with their intended role or skip reason. They may not override `vue-dev`, project `AGENTS.md`, existing project conventions, or direct user instructions. Choose design, planning, TDD, debugging, review, and verification depth directly from task risk and project needs without requiring a separate methodology skill.

## Workflow

1. Start by naming the default Vue development stack: `vue-dev`, `codegraph`, `OpenSpec`, and `design-taste-frontend`. State whether OpenSpec needs a spec read/update and whether design-taste applies or is skipped.
2. Inspect the project before creating or changing files. When `.codegraph/` exists, use `codegraph` first for route/component/store/API flow and impact analysis; then read `package.json`, router setup, app entry, representative pages, shared components, API clients, stores, and style config.
3. Preserve the existing framework and package manager. Do not switch Vue CLI/Vite/Nuxt, npm/pnpm/yarn/bun, UI library, state library, routing mode, CSS system, or lint/test tools unless the user asks or the current setup is broken.
4. Decide whether the task is greenfield app setup, new feature/page, local bug fix, API integration, visual redesign, or spec-worthy behavior/architecture work.
5. Read only the references needed for the task:
   - Project structure and module boundaries: `references/project-structure.md`
   - Component, composable, and TypeScript patterns: `references/component-patterns.md`
   - API, routing, state, auth, and permissions: `references/state-api-routing.md`
   - Styling, UI systems, responsive design, and Taste-Skill boundaries: `references/style-design.md`
   - Tests, builds, browser verification, and delivery checks: `references/testing-verification.md`
6. For new Vue apps, prefer official Vue/Vite scaffolding with TypeScript, Vue Router, Pinia, Vitest, ESLint, and Prettier unless the user or project dictates otherwise.
7. When touching Chinese text in Vue, TypeScript, JSON, YAML, Markdown, or API mock data, apply the Chinese encoding guard below before reading or editing nearby text.
8. Run the smallest relevant verification command available: type-check, lint, focused unit/component test, build, e2e test, or browser screenshot/interaction check. Report any verification you could not run.

## Non-Negotiable Rules

- Do not invent a new frontend architecture when an existing project already has one.
- Do not let OpenSpec or design-taste-frontend change Vue architecture, package manager, UI library, module placement, API error handling, state placement, routing rules, or verification rules defined by `vue-dev` and the current project.
- Use Vue 3 Composition API with `<script setup lang="ts">` for new Single File Components unless the existing project standard differs.
- Prefer TypeScript for new code. Avoid `any`; use `unknown`, typed API DTOs, discriminated unions, generics, or narrowed runtime checks.
- Keep route-level pages thin. Put reusable UI in `components`, feature logic in feature modules, cross-feature logic in `shared` or `common`, and side-effectful logic in composables, stores, or API clients.
- Keep constants close to one component when local. Move cross-page or cross-module constants to `src/shared/constants` or the existing shared constants location. Do not scatter meaningful magic values in templates, stores, API clients, or styles.
- Do not silently hide required product failures behind fallback UI. Required API, auth, permission, route param, or domain-state failures should surface through the project-standard error state, route guard, toast/message, or typed exception/result path.
- Fallback UI is allowed only for explicitly degradable paths such as optional decorations, analytics, non-critical recommendations, skeleton loading, image placeholders, or cached stale data. Keep it narrow and visible to maintainers.
- Do not put raw API calls directly in templates or random components. Use the existing API client/service layer or create one under the feature or shared API layer.
- Do not expose backend Entity-shaped objects directly to UI when a page-specific view model is clearer. Map API DTOs to typed UI models for presentation-specific fields.
- Forms must use typed models, explicit validation, disabled/loading states, error states, and submit idempotency where relevant.
- Tables and lists must handle loading, empty, error, pagination or infinite-scroll boundaries, and stable row keys.
- Use accessible HTML first: labels for inputs, real buttons for actions, links for navigation, keyboard focus states, ARIA only when native semantics are insufficient.
- For admin, dashboard, CRM, and operational UIs, favor dense but organized information, restrained styling, predictable navigation, and efficient scanning. Do not apply marketing-page hero/card-heavy composition to work surfaces.
- For landing pages, portfolios, branded pages, and redesigns, use `design-taste-frontend` after declaring the design read. Keep its scope to visual composition, typography, motion, spacing, and pre-flight checks.
- Do not hand-roll SVG icons when the project already has an icon library. Use the existing icon set; otherwise choose one library and keep it consistent.
- Do not use `console.log`, `debugger`, or ad hoc browser globals in committed code. Use project-standard logging/debug utilities or remove debug traces.
- Do not put secrets, tokens, backend base URLs with credentials, or private endpoints in frontend source. Use `import.meta.env` and document required env keys without real values.
- Preserve existing route names, query parameters, form field names, analytics event names, and i18n keys unless the user explicitly asks to change them.

## Chinese Encoding And Mojibake Guard

Use this guard whenever a Vue frontend task touches files or terminal output that may contain Chinese.

- Keep frontend projects explicitly UTF-8. Vue SFCs, TypeScript, JSON, YAML, Markdown, CSS, and mock files should be written as UTF-8 without BOM unless the existing project requires otherwise.
- On Windows/PowerShell, inspect text with UTF-8 output enabled and explicit file encoding, for example: `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8; Get-Content -LiteralPath '<file>' -Encoding UTF8`.
- If UTF-8 reads show garbled fragments such as `鐨`, `閿`, `鍦`, `瑁`, `鈥`, `�`, or Chinese-looking nonsense, do not infer the intended Chinese from that output. Check Git history, IDE local history, backups, product copy, or i18n source files before rewriting nearby copy.
- Before applying a patch near Chinese text, get one clean UTF-8 line-numbered read of the target file.
- After edits, run a lightweight scan for common mojibake signals, for example `rg -n "�|鐨|閿|鍦|瑁|鈥" <project> -g '*.vue' -g '*.ts' -g '*.js' -g '*.json' -g '*.md' -g '!node_modules/**' -g '!dist/**'`.
- Do not convert, normalize, or rewrite a whole file just to fix encoding unless the user asked for that cleanup and a trusted clean source is available.

## Defaults

- Framework: Vue 3 with Composition API and SFCs.
- Build: Vite for new SPA projects unless existing project uses Nuxt or another framework.
- Language: TypeScript.
- Routing: Vue Router for SPA route management.
- State: Pinia for shared client state; component-local state for local UI.
- Tests: Vitest for unit/component tests; Playwright for e2e or browser verification when present.
- Styling: follow the existing project. For new apps, use the user's chosen design system or Tailwind/CSS modules; do not mix UI systems casually.
- Visual quality: use `design-taste-frontend` for marketing/portfolio/redesign surfaces, but keep admin/product UI quiet, scannable, and task-focused.
