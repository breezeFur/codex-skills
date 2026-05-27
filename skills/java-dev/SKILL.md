---
name: java-dev
description: Generate and maintain Java backend services and modules using Maven, JDK 21, Spring Boot 3.4, Lombok, MySQL, MyBatis-Plus, MyBatis-Plus-Join, Redis, SpringDoc OpenAPI, typed unified API responses, service-name-based multi-module architecture, DAO/Mapper/Entity data layering, MetaObjectHandler audit filling, UUIDv7 IDs, Long millisecond timestamps, Chinese OpenAPI descriptions, framework interceptors, ThreadLocal context, exception handling, Controller/Service business modules, and tests. Use when creating or modifying Java projects, modules, controllers, services, DAOs, mappers, entities, request/response DTOs, VO objects, database access, Redis cache, OpenAPI annotations, framework infrastructure, or fixing Java code to match these conventions. For Java coding style, constants, logging, comments, Lombok consistency, magic values, and naming cleanup, use java-code-style.
---

# Java Dev

## Workflow

1. Inspect the repository before creating or changing files: read the root build file, module list, package names, existing response wrapper, existing ID utilities, and representative Controller/Service/DAO/Entity classes.
2. Decide whether the task is full service generation, a new module, or local code changes.
3. Prefer the existing project's naming, package layout, response wrapper, timestamp field names, and module prefix. For new services, use the requested service name as the module prefix.
4. Use Maven, JDK 21, Spring Boot 3.4, Lombok, MySQL, MyBatis-Plus, MyBatis-Plus-Join, Redis, and SpringDoc unless the existing project already pins compatible versions.
5. Read only the references needed for the task:
   - Architecture and module boundaries: `references/project-architecture.md`
   - Naming and package conventions: `references/module-conventions.md`
   - Controller and API contracts: `references/api-contracts.md`
   - Entity, ID, audit, Mapper, DAO rules: `references/entity-and-db.md`
   - Framework infrastructure: `references/framework-components.md`
   - MPJ and data access patterns: `references/mpj.md`
   - OpenAPI annotation rules: `references/openapi-schema.md`
   - Redis cache rules: `references/redis-cache.md`
   - Code style, constants, logging, comments, Lombok consistency, magic values, and naming cleanup: use `java-code-style`.
6. Reuse `assets/templates/` when generating a new service or module, then adapt package names, module names, and existing project conventions.
7. Run the smallest relevant verification command available: compile, module test, focused test, or static inspection. Report any verification you could not run.

## Non-Negotiable Rules

- Do not invent a new architecture when an existing project already has one.
- Put the application startup class in the service main module.
- Put database entities, mappers, DAOs, DAO implementations, and database configuration in the service database module.
- Put interceptors, ThreadLocal holders, constants, exception handling, custom exceptions, and shared web infrastructure in the service framework module.
- Put business Controllers, Services, module-local DTOs, response VOs, and business-local enums in business modules. Use the business module's `model.dto` and `model.vo` packages for DTO/VO classes that are only used by that module.
- Keep normal database operations in DAO/DAO implementation classes using Lambda-style MyBatis-Plus or MPJ APIs.
- Use Mapper annotation SQL only when raw SQL is necessary.
- Services call DAOs, not Mappers, unless the existing project has an explicit local exception.
- Do not expose QueryWrapper, LambdaQueryWrapper, LambdaUpdateWrapper, or MPJLambdaWrapper to Controllers.
- Controller responses must use a typed unified response wrapper. In new services, create `Result<T>`; in existing services, reuse the existing wrapper such as `ApiResponse<T>`.
- Do not use `Map` as request or response DTO. Define typed request objects and VOs.
- Use direct method parameters only for 5 or fewer simple inputs. Use a typed request object for more than 5 inputs or any structured/business object input.
- Do not return database Entity classes from Controllers.
- Use UUIDv7 for new database IDs through a central ID generator abstraction. If the project already has an ID utility, extend or reuse it.
- Use `Long` millisecond Unix timestamps for new create/update time fields unless the existing table already uses another type.
- Prefer audit field names `createdAt`, `updatedAt`, `createdBy`, and `updatedBy`; follow existing project names if different.
- Use MyBatis-Plus `MetaObjectHandler` for audit field filling.
- Annotate Controllers with `@Tag`, API methods with `@Operation`, and Entity/Request/VO/Result/PageResult classes or records with `@Schema`.
- Write OpenAPI names, summaries, and descriptions in Chinese for new code.
- Apply `java-code-style` for constants, logging, comments, Lombok consistency, naming cleanup, and magic value cleanup.

## Defaults

- Build: Maven multi-module.
- Java: JDK 21.
- Framework: Spring Boot 3.4.
- Database: MySQL.
- Persistence: MyBatis-Plus and MyBatis-Plus-Join.
- Cache: Redis with `StringRedisTemplate` or project-standard Redis abstraction.
- API docs: SpringDoc OpenAPI.
- ID generation: UUIDv7 hidden behind `IdGenerator`.
- Audit filling: MyBatis-Plus `MetaObjectHandler`.
