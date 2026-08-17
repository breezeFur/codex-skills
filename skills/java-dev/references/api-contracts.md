# API Contracts

## Controller Responsibilities

Controllers only:

- Receive and validate parameters.
- Read authentication or request context.
- Call Service methods.
- Wrap or return the unified response type required by the project.

Business logic belongs in Services. Data access belongs behind DAOs.

## Unified Response

Use a typed unified response wrapper for all new Controller responses.

- New service default: `Result<T>`.
- Existing service: reuse the existing wrapper, for example `ApiResponse<T>`, if one already exists.
- Typical return shapes:
  - `Result<XxxVo>`
  - `Result<List<XxxVo>>`
  - `Result<YPage<XxxVo>>`
  - `Result<Void>`

Do not return raw `String`, `Boolean`, `List`, `IPage`, `Map`, or Entity from Controllers in new code.

## Request And Response Objects

- Do not use `Map` as API request or response DTO.
- For every GET query endpoint, use direct `@RequestParam`/`@PathVariable` parameters when there are fewer than 5 simple business inputs. Add a Chinese `@Parameter(description = "...")` to each direct parameter. A `YPage<T>` page parameter is framework pagination and does not count toward the five-input limit.
- For GET endpoints with 5 or more business inputs, nested/structured criteria, repeated sorting, or a meaningful query object, use a typed query request class such as `XxxQuery` or `XxxPageRequest`.
- For POST, PUT, and PATCH endpoints, default to a typed `@Valid @RequestBody` request DTO regardless of field count.
- Return typed VO classes instead of Entities.
- Use `YPage<T>` or the existing project page wrapper for pagination output.
- When `YPage<T>` extends MyBatis-Plus `Page<T>` or implements `IPage<T>`, a simple GET endpoint may bind `@Valid YPage<XxxVo>` directly. The client sends `current` and `size`; generic type `T`, `records`, `total`, and calculated page metadata are backend concerns.
- Keep fewer than 5 simple filters as separately annotated path/query parameters beside `YPage<T>`. Use a dedicated `XxxQuery` or `XxxPageRequest` for structured filters, nested conditions, or 5 or more business fields; do not add business filters to the reusable page type.
- Pass the same page object from Controller through Service to the DAO. For direct MPJ DTO pagination, the Service may construct the MPJ wrapper and call the DAO's inherited `selectJoinListPage(page, dtoType, wrapper)`; Controllers must not call Mapper or `baseMapper`. Do not create a second page or use `YPage.convert(...)` for this path.
- If a request or response object is implemented as a Java `record`, treat it the same as a DTO/VO class: add class-level and component-level Chinese `@Schema` annotations.

## Default Result Shape

Use this only when the project does not already define a unified response wrapper:

```java
@Schema(description = "统一接口返回结果")
public class Result<T> {
    @Schema(description = "业务状态码")
    private Integer code;

    @Schema(description = "返回消息")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "请求是否成功")
    private Boolean success;
}
```

Provide static helpers such as `success`, `fail`, and `empty` according to project style.

## Validation

- Use `jakarta.validation` annotations on request objects.
- Add `@Valid` to `@RequestBody` parameters.
- Convert validation errors in the global exception handler.
- Keep error messages specific and user-facing where possible.
