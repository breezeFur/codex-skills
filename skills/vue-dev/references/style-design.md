# Styling, UI Systems, And Design Taste

## Existing UI First

- Follow the current UI library and design system. Do not mix Element Plus, Ant Design Vue, Naive UI, Vuetify, shadcn-vue, Tailwind-only custom components, or other systems casually.
- Reuse existing layout, form, table, modal, message/toast, icon, and typography components before creating new ones.
- Keep token usage consistent: spacing, radius, color, shadows, z-index, and breakpoints.

## Product UI

For admin panels, dashboards, CRM, workflow tools, and operational systems:

- Prioritize scanning, comparison, repeated action, and predictable navigation.
- Use compact but readable density.
- Avoid oversized heroes, decorative cards, marketing sections, and one-note palettes.
- Tables/lists need loading, empty, error, pagination, sorting/filtering, and stable row keys when relevant.
- Forms need labels, validation, disabled/loading states, submit feedback, and unsaved-change handling when relevant.

## Marketing, Portfolio, Landing, Redesign

Use `design-taste-frontend` for:

- landing pages,
- portfolio pages,
- brand/product pages,
- marketing sections,
- redesigns where visual quality is the explicit goal.

Before using it, declare a one-line design read and whether the page is landing, portfolio, redesign, or another surface. If the task is a dashboard or data-heavy product UI, state that Taste-Skill is skipped except for local visual polish.

## Responsive And Accessibility

- Design mobile behavior explicitly; do not assume desktop grids collapse cleanly.
- Use semantic HTML first.
- Keep focus states visible.
- Check text contrast, disabled contrast, and hover/active states.
- Avoid layout shifts from dynamic content, loading labels, icons, or validation messages.
