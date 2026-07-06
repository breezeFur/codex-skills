# Testing And Verification

Inspect `package.json` first and use the project's scripts. Common verification sequence:

```powershell
npm run type-check
npm run lint
npm run test:unit
npm run build
```

Use the matching package manager: `pnpm`, `npm`, `yarn`, or `bun`.

## Focused Checks

- Component or composable change: run the focused unit/component test if present.
- API/state change: test success, loading, empty, error, and permission states.
- Route change: test navigation, params/query parsing, guards, and direct refresh behavior when possible.
- Styling/layout change: run a browser check with desktop and mobile viewport screenshots when a dev server can run.
- Regression fix: write or update a failing test first when feasible, then make it pass.

## Browser Verification

For visible UI changes:

- Start the dev server unless the app can be opened as static HTML.
- Use Playwright or the available browser tool for screenshots and interaction checks.
- Check desktop and mobile widths.
- Verify no obvious overlap, clipped text, broken loading states, or console errors.

## Completion Rule

Do not claim the UI is fixed or polished until at least one relevant automated or browser verification has actually run. If verification is blocked, report the exact command and blocker.
