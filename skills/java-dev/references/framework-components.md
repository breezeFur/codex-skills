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

## Web Configuration

Include only what the project needs:

- Interceptor registration.
- Jackson configuration.
- CORS configuration if requested.
- Trace ID or request logging if the project uses it.
- OpenAPI grouping if the project uses grouped docs.
