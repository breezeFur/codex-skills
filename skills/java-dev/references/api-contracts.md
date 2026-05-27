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
  - `Result<PageResult<XxxVo>>`
  - `Result<Void>`

Do not return raw `String`, `Boolean`, `List`, `IPage`, `Map`, or Entity from Controllers in new code.

## Request And Response Objects

- Do not use `Map` as API request or response DTO.
- Use direct parameters only when there are 5 or fewer simple scalar inputs.
- Use a typed request class when inputs exceed 5 fields, have nested structure, or carry business meaning.
- Return typed VO classes instead of Entities.
- Use `PageResult<T>` or the existing project page wrapper for pagination output.
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
