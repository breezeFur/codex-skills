# Project Architecture

Use the requested service name as the module prefix for new services. For existing repositories, detect and follow the current module prefix and package root.

## Standard Modules

- `<service-name>-main`: application startup class, runtime configuration files, environment profiles, global scan entry, executable packaging.
- `<service-name>-common`: optional module for pure shared contracts such as unified API response classes, basic DTOs, common enums, and small value objects. Do not put web interceptors or business logic here.
- `<service-name>-framework`: web infrastructure, exception handling, interceptors, request context, ThreadLocal holders, constants, framework configuration, shared utility classes.
- `<service-name>-db`: database entities, mappers, DAOs, DAO implementations, database configuration, MyBatis-Plus configuration, MPJ setup, audit filling, and DAO-owned query projections.
- `<service-name>-<business>`: business module containing Controllers, Services, Service implementations, module-local DTOs, response VOs, business-local enums, and module-specific configuration. Prefer `model.dto` and `model.vo` for DTO/VO classes that belong only to that business module. Shared DTO/VO classes should move down to the lowest shared module allowed by dependencies; they should reach the database module only when they are data-access projections.

## Dependency Direction

```text
<service-name>-main
  depends on <service-name>-framework
  depends on <service-name>-db
  depends on business modules

business modules
  depend on <service-name>-framework
  depend on <service-name>-db

<service-name>-db
  may depend on <service-name>-common
  may depend on <service-name>-framework only when audit or shared exception/context types require it

<service-name>-framework
  may depend on <service-name>-common
  must not depend on business modules

<service-name>-common
  must not depend on framework, db, or business modules
```

Avoid circular dependencies. When a dependency cycle appears, move pure shared contracts to the common module or move behavior back into the owning module.

## Full Service Generation

When creating a new service:

1. Create the root Maven parent with dependency management.
2. Create main, common when needed, framework, db, and requested business modules.
3. Put the startup class only in the main module.
4. Put API response, page response, exceptions, request context, audit filling, and database configuration in their owning modules.
5. Keep business modules focused on use cases rather than infrastructure.

## Existing Repository Changes

When modifying an existing service:

- Detect the module prefix from the root Maven modules.
- Reuse the existing package root and module names.
- Reuse the existing response wrapper, ID utility, exception hierarchy, and request context.
- Apply these rules as a target direction, but do not rename modules or rewrite architecture unless the user asks.
