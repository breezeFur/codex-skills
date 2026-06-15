---
name: java-dev
description: Generate and maintain Java backend services and modules using Maven, JDK 21, Spring Boot 3.4, Lombok, MySQL, MyBatis-Plus, MyBatis-Plus-Join, Redis, SpringDoc OpenAPI, typed unified API responses, service-name-based multi-module architecture, DAO/Mapper/Entity data layering, MetaObjectHandler audit filling, UUIDv7 IDs, Long millisecond timestamps, Chinese OpenAPI descriptions, framework interceptors, ThreadLocal context, exception handling, MDC trackId log format, Controller/Service business modules, and tests. Use when creating or modifying Java projects, modules, controllers, services, DAOs, mappers, entities, request/response DTOs, VO objects, database access, Redis cache, OpenAPI annotations, framework infrastructure, or fixing Java code to match these conventions. For Java coding style, constants, logging statements, comments, Lombok consistency, magic values, and naming cleanup, use java-code-style.
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
7. For database reverse-generation requests, copy `assets/templates/db-code-generator/DbCodeGenerator.java` into the target database module, usually `<db-module>/src/test/java/<package-path>/db/generator/DbCodeGenerator.java`. Fill the constants at the top from the current project's datasource config or user-provided database information, set `DB_MODULE_NAME` and `BASE_PACKAGE`, keep `OVERWRITE_EXISTING_FILES=false` unless the user explicitly asks to overwrite, then use it to generate Entity, Mapper, DAO, and DAO implementation files directly under the database module.
8. When Java, SQL, YAML, Markdown, or OpenAPI text may contain Chinese, apply the Chinese encoding guard below before reading or editing nearby text.
9. Run the smallest relevant verification command available: single-file `javac` for the generator, compile, module test, focused test, or static inspection. Report any verification you could not run.

## Non-Negotiable Rules

- Do not invent a new architecture when an existing project already has one.
- Put the application startup class in the service main module.
- Put database entities, mappers, DAOs, DAO implementations, and database configuration in the service database module.
- Do not store real database credentials in skill assets or synced skill repositories. Only fill real datasource values in the project-local generated `DbCodeGenerator.java` when needed for the current task.
- Put interceptors, ThreadLocal holders, constants, exception handling, custom exceptions, and shared web infrastructure in the service framework module.
- Do not hide business failures behind fallback defaults. When a required business rule, parameter, entity, or dependency result is invalid or missing, fail explicitly with the project's custom exception such as `BizException` plus `ErrorCode`.
- Use fallback only for explicitly degradable paths such as cache miss/cache failure, metrics, optional recommendations, or non-critical notifications. Log useful context and keep the fallback narrow.
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
- Treat mojibake or replacement glyphs in Chinese comments, annotations, SQL, YAML, JSON, or Markdown as an encoding defect. Do not copy garbled terminal output into source files.
- Configure generated services so console logs include an MDC `trackId` between thread and level, for example:

  ```text
  2026-06-02 09:35:30.013 [scheduling-1] [d952b913fb5a42378747b3c119a10fe3] DEBUG c.d.k.c.web.ScheduleTraceIdAspect:43    message
  ```

  Use Logback/Spring Boot pattern `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{trackId:-}] %-5level %logger{36}:%line    %msg%n` or the existing project's equivalent. New framework code should put the request or scheduled-task identifier into MDC key `trackId` and clear it in `finally` or request completion. If an existing project already uses `traceId`, preserve compatibility but also bridge the value into MDC `trackId` so the log format stays stable.
- Apply `java-code-style` for constants, logging, comments, Lombok consistency, naming cleanup, and magic value cleanup.

## Chinese Encoding And Mojibake Guard

Use this guard whenever a Java backend task touches files or terminal output that may contain Chinese.

- Keep Maven projects explicitly UTF-8: root `pom.xml` should include `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`, and generated Java/SQL/YAML/Markdown files should be written as UTF-8 without BOM unless the existing project requires otherwise.
- On Windows/PowerShell, inspect text with UTF-8 output enabled and explicit file encoding, for example: `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8; Get-Content -LiteralPath '<file>' -Encoding UTF8`.
- If UTF-8 reads still show garbled fragments such as `鐨`, `閿`, `鍦`, `瑁`, `鈥`, `�`, or Chinese-looking nonsense around otherwise normal punctuation, do not infer the intended Chinese from that output. Check Git history, IDE local history, backups, or the original product document before rewriting nearby comments, OpenAPI descriptions, SQL, YAML, or string literals.
- Before applying a patch near Chinese text, get one clean UTF-8 line-numbered read of the target file. If the clean read is garbled, limit the change to code that does not require rewriting the garbled text, or first recover the intended text from a trusted source.
- After edits, run a lightweight scan for common mojibake signals, for example `rg -n "�|鐨|閿|鍦|瑁|鈥" <project> -g '*.java' -g '*.sql' -g '*.yml' -g '*.yaml' -g '*.md' -g '!target/**'`. Treat the result as a review cue, not an automatic replace list.
- Do not convert, normalize, or rewrite a whole file just to fix encoding unless the user asked for that cleanup and a trusted clean source is available.

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
