# Framework Components

Create these components in the service framework module unless the existing project already places them elsewhere.

## Core Components

```text
framework/
  web/
    Result.java
    PageResult.java
    WebMvcConfig.java
  exception/
    BizException.java
    ErrorCode.java
    GlobalExceptionHandler.java
  context/
    RequestContext.java
    RequestContextHolder.java
  interceptor/
    RequestContextInterceptor.java
  constants/
    CommonConstants.java
```

If the project has a common module, pure response contracts such as `Result` may live there. Web configuration and interceptors stay in framework.

## Result And PageResult

- Provide a typed API response wrapper when no project wrapper exists.
- Provide a typed pagination wrapper with total count, page number, page size, and records.
- Add Chinese `@Schema` descriptions.

## Exceptions

- Use `BizException` for expected business failures.
- Use `ErrorCode` or an existing project error-code type for reusable error definitions.
- Prefer explicit custom exceptions over defensive fallback when the business cannot continue safely.
- Required data that is missing, invalid state transitions, failed permission checks, invalid parameters, and violated business invariants should fail fast with a meaningful error code.
- Do not catch broad `Exception` and return `null`, an empty collection, a default object, or a fake success unless the feature is explicitly designed as degradable.
- Allowed fallback paths must be narrow and intentional, for example cache failures, metrics, optional recommendation data, or notification side effects. Log enough context to diagnose the degradation.
- Add exception logging according to `java-code-style`.
- Global exception handling should convert:
  - business exceptions
  - validation exceptions
  - authentication/authorization exceptions when present
  - uncaught exceptions

Do not leak stack traces or internal messages in API responses unless the existing project explicitly does so.

## Request Context

- Use a ThreadLocal holder for current request/user context.
- Populate it in an interceptor or filter.
- Always clear ThreadLocal state in `finally` or `afterCompletion`.
- Provide a safe fallback user such as `system` for scheduled tasks and audit filling.
- For new code, name the request correlation field `trackId` and write it to MDC key `trackId`. If existing code already exposes `traceId`, keep that API stable but mirror the same value to `trackId` in MDC.
- Web requests should read an incoming track ID header when the project has one, otherwise generate a new value.
- Scheduled jobs must also create a `trackId`, for example in a scheduling aspect or task decorator, so scheduler logs are correlated.
- Clear both ThreadLocal context and MDC in `finally` or `afterCompletion`.

## Logging Pattern

Generated services should configure console logs with the track ID between thread name and log level:

```text
2026-06-02 09:35:30.013 [scheduling-1] [d952b913fb5a42378747b3c119a10fe3] DEBUG c.d.k.c.web.ScheduleTraceIdAspect:43    message
```

For Spring Boot / Logback, use this pattern unless the existing project has an equivalent:

```text
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{trackId:-}] %-5level %logger{36}:%line    %msg%n
```

## Web Configuration

Include only what the project needs:

- Interceptor registration.
- Jackson configuration.
- CORS configuration if requested.
- Track ID or request logging if the project uses it.
- OpenAPI grouping if the project uses grouped docs.
