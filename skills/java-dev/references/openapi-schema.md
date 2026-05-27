# OpenAPI Schema

Use SpringDoc OpenAPI annotations for new API code.

## Required Annotations

- Controller class: `@Tag`
- API method: `@Operation`
- Path/query parameters when helpful: `@Parameter`
- Entity, Request, VO, Result, and PageResult classes or records: `@Schema`
- Important fields and record components: `@Schema(description = "...")`

## Language Rules

- Use Chinese for `@Tag` names and descriptions in new code.
- Use Chinese for `@Operation` summary and description in new code.
- Use Chinese for all `@Schema(description = "...")` text in new code.
- Avoid meaningless descriptions such as `字段`, `数据`, `对象`, or `信息`.
- Prefer business-specific descriptions such as `用户ID`, `岗位名称`, `创建时间，Unix 毫秒时间戳`.

## Controller Example

```java
@Tag(name = "用户管理", description = "用户资料查询和维护接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户基础资料")
    @GetMapping("/{id}")
    public Result<UserDetailVo> detail(@Parameter(description = "用户ID") @PathVariable String id) {
        return Result.success(userService.detail(id));
    }
}
```

If the existing project uses another response wrapper, keep the annotation style but return that wrapper.

## DTO Example

```java
@Data
@Schema(description = "创建用户请求")
public class UserCreateRequest {
    @NotBlank
    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;
}
```

## Record DTO/VO Example

When a Java `record` is used as an API request, API response, DTO, VO, Result, or PageResult, annotate the record itself and every record component with Chinese `@Schema` descriptions.

```java
@Schema(description = "用户详情响应")
public record UserDetailVo(
        @Schema(description = "用户ID")
        String userId,

        @Schema(description = "用户姓名")
        String username
) {
}
```

Internal temporary records that never appear in OpenAPI contracts may omit `@Schema`.
