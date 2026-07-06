# State, API, Routing, Auth, And Permissions

## API Clients

- Use the existing HTTP abstraction. If none exists, create a focused client such as `src/shared/api/http.ts`.
- Keep request/response DTOs typed. Match backend unified response wrappers such as `Result<T>` or project-specific equivalents.
- Centralize base URL, auth headers, timeout, retry, and global error handling.
- Do not swallow failed required requests by returning fake success or empty data.
- Map API DTOs to UI models when display fields, labels, permission flags, or derived states are needed.

## State

- Use component-local `ref`/`reactive` for local UI state.
- Use Pinia for cross-page state, authenticated user context, feature state shared across distant components, or cached domain data.
- Do not put every form field in a global store.
- Stores should expose business actions and derived getters, not arbitrary mutation scripts.

## Routing

- Use Vue Router for SPA route management.
- Keep route metadata typed when possible: title, auth requirement, permission code, layout, cache policy.
- Put auth and permission checks in route guards or a centralized permission layer, not scattered across templates.
- Preserve existing route names, paths, query keys, and params unless the user asks for a breaking change.
- For history mode, ensure deployment fallback is configured before relying on direct URL refresh.

## Permissions

- Hide unavailable actions only when product behavior requires it; otherwise show disabled actions with a clear reason.
- Never rely only on frontend permission checks for security.
- Keep permission constants centralized and named by business action.
