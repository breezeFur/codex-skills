# Module Conventions

Follow existing naming first. Use these defaults for new services or modules.

## Package Layout

Use the repository package root, then module responsibility:

```text
<package-root>.<business>.controller
<package-root>.<business>.service
<package-root>.<business>.service.impl
<package-root>.<business>.model.dto
<package-root>.<business>.model.vo
<package-root>.db.entity
<package-root>.db.mapper
<package-root>.db.dao
<package-root>.db.dao.impl
<package-root>.framework.web
<package-root>.framework.exception
<package-root>.framework.context
<package-root>.framework.interceptor
<package-root>.framework.constants
```

## Class Names

- Controller: `XxxController`
- Service interface: `XxxService`
- Service implementation: `XxxServiceImpl`
- Entity: prefer `XxxEntity`; follow existing style if the project uses plain `Xxx`
- Mapper: `XxxMapper` or existing entity-based mapper style
- DAO interface: `XxxDao`
- DAO implementation: `XxxDaoImpl`
- Create request: `XxxCreateRequest`
- Update request: `XxxUpdateRequest`
- Page request: `XxxPageRequest`
- Response VO: `XxxVo`
- Detail response VO: `XxxDetailVo`
- Page response VO: `XxxPageVo`

## Placement Rules

- Put cross-class or cross-module shared constants in the framework module under the `constants` package so every business module can reuse them through the framework dependency.
- Put business-local enums in the owning business module.
- Put cross-module enums in the framework module when they are part of shared infrastructure or shared business contracts; keep highly domain-specific enums in the owning business module unless multiple modules need them.
- Put module-local DTOs and VOs in the owning business module, preferably under `model.dto` and `model.vo`.
- DTO/VO placement follows ownership and dependency direction, not where the data originally came from.
- Start with the owning business module. If another business module also needs the same DTO/VO, move it down to the lowest shared module that both callers can depend on.
- Move a DTO/VO to the database module only when it is a true DAO/Mapper query projection owned by the data-access layer, or when the shared contract is intentionally a data-access projection.
- Do not put a DTO/VO into the database module just because a Service uses DAO data to build it.
- If a business module calls DAO-provided Lambda/MPJ-capable methods and assembles a response for one use case, keep that response VO in the business module's `model.vo` package.
- Do not put Controller-only request classes in the database module for new code; keep request/command DTOs in the owning business module's `model.dto` package unless the existing project uses a different local convention.

## DTO/VO Placement Decision Tree

1. Is the DTO/VO used by only one business module?
   Put it in that business module under `model.dto` or `model.vo`.
2. Is the DTO/VO shared by multiple business modules but not owned by DAO/Mapper queries?
   Put it in the lowest shared module allowed by the dependency graph, such as a shared contract/framework/common module already depended on by those business modules.
3. Is the DTO/VO the direct projection shape of a DAO/Mapper query?
   Put it in the database module only if the data-access layer owns that projection.
4. Is the DTO/VO a Controller request or API response for one use case?
   Keep it in the business module even if the Service reads from DAOs.
5. Would moving the DTO/VO create a reverse dependency from a lower module to a business module?
   Do not move it there; choose a lower shared module or keep separate module-local DTO/VO classes.

## Code Style

For constants, magic values, logging, comments, Lombok consistency, enum style, and naming cleanup, use `java-code-style`.
