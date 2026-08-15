# MyBatis-Plus-Join

## Layer Boundary

Use these layers for database code:

- `entity`: database table entities.
- `mapper`: MyBatis-Plus Mapper interfaces and necessary annotation SQL.
- `dao`: business-facing data-access interfaces.
- `dao.impl`: DAO implementations for custom persistence behavior.

Controllers call Services. Services call DAOs. Controllers must not call Mapper or `baseMapper`.

For a direct MPJ DTO list/page query, the Service may build the `MPJLambdaWrapper` and call the
official MPJ service method inherited by the DAO. The DAO implementation may use `baseMapper`
for other persistence details, but it must not force the business layer through a custom Mapper
proxy when MPJ already exposes the required service method.

## Pagination Ownership

- Put a reusable `PageResult<T>` in the repository's existing shared MyBatis integration/starter module.
- If no shared MyBatis module exists, put it in the service database module, for example `db.result.PageResult`.
- Do not put a MyBatis-coupled page type in a generic base, common, or framework module.
- Make `PageResult<T>` extend MyBatis-Plus `Page<T>` or implement `IPage<T>`. Prefer extending `Page<T>` so MP and MPJ can fill it directly.
- Validate `current >= 1` and bound `size` with the project's maximum page size.
- Let MyBatis-Plus or MPJ populate `records`, `total`, and page metadata on the page object supplied by the caller.
- Do not create a second page, manually copy `records`/`total`/`current`/`size`, or use `PageResult.convert(...)` in a new direct MPJ DTO pagination path.

Use `assets/templates/service-db/.../db/result/PageResult.java` as the default for a new service
without a shared MyBatis starter.

## Official MPJ Service Path

When a DAO directly owns an MPJ DTO query, inherit the official MPJ service interface and
implementation:

```java
import com.github.yulichang.base.MPJBaseService;

public interface UserDao extends MPJBaseService<User> {
}
```

```java
import com.github.yulichang.base.MPJBaseServiceImpl;

@Repository
public class UserDaoImpl extends MPJBaseServiceImpl<UserMapper, User> implements UserDao {
}
```

The official service method is the direct DTO page entry point:

```java
<D, P extends IPage<D>> P selectJoinListPage(
        P page,
        Class<D> dtoType,
        MPJBaseJoin<T> wrapper)
```

Use `selectJoinListPage` on the DAO. Do not replace it with `baseMapper.selectJoinPage`, and do
not add a redundant method such as `pageByRoleId` that only forwards arguments to the official
MPJ method. A semantic DAO method is still appropriate when it owns genuinely reusable query
logic or persistence rules; the method should return the same caller-provided page when it is a
direct DTO query.

## Mapper Convention

Use the repository's existing Mapper convention. A thin project alias is acceptable when the
repository already centralizes MPJ Mapper inheritance:

```java
import com.github.yulichang.base.MPJBaseMapper;

public interface MpjBaseMapper<T> extends MPJBaseMapper<T> {
}
```

The alias must not add wrapper-first overloads or reimplement official MPJ pagination methods.
If the project does not need an alias, table Mappers may extend the official `MPJBaseMapper<T>`
directly. The business pagination path remains the DAO's `MPJBaseService` API above.

## End-To-End Page Flow

For a simple paginated GET endpoint, the Controller may bind the shared page object directly.
The client sends only `current` and `size`; the Java generic type is selected by backend code and
is never sent over HTTP.

```java
@GetMapping("/{roleId}/users")
public Result<PageResult<UserDetailVo>> listUsers(
        @PathVariable String roleId,
        @Valid PageResult<UserDetailVo> page,
        @RequestParam(required = false) String username) {
    return Result.success(userService.listUsers(roleId, username, page));
}
```

The Service builds the wrapper and passes the same page object to the official DAO method:

```java
public PageResult<UserDetailVo> listUsers(
        String roleId,
        String username,
        PageResult<UserDetailVo> page) {
    requireRole(roleId);
    MPJLambdaWrapper<User> wrapper = JoinWrappers.lambda(User.class)
            .selectAll(User.class)
            .innerJoin(UserRole.class, UserRole::getUserId, User::getUserId)
            .eq(UserRole::getRoleId, roleId)
            .like(username != null && !username.isBlank(), User::getUsername, username)
            .orderByDesc(User::getCreatedAt)
            .orderByAsc(User::getUserId);
    return userDao.selectJoinListPage(page, UserDetailVo.class, wrapper);
}
```

This flow has one page object from Controller to MPJ and back:

`Controller PageResult<VO> -> Service wrapper -> DAO selectJoinListPage -> same PageResult<VO>`

Do not call `page.convert(...)`, do not create `PageResult.of(page.getCurrent(), page.getSize())`
inside the direct DTO path, and do not manually copy records or pagination metadata. MPJ writes
the mutable DTO records and page metadata into the supplied page.

For a simple single-table Entity page, a DAO may call the normal MyBatis-Plus page method with
the supplied page object. When the API response is a VO, prefer direct MPJ projection so the
database query fills the VO type directly instead of adding an Entity-to-VO conversion step.

## DTO Mutability And Mapping

- A direct MPJ DTO target must be writable: use a class with a no-argument constructor and setters,
  commonly Lombok `@Data`.
- Do not use a Java `record` as the direct MPJ page target because MPJ needs to assign projected
  fields. Keep the external JSON contract unchanged with an equivalent writable response class
  when a record currently exists.
- Keep one-module response VOs in the owning business module's `model.vo` package.
- Move a projection down according to dependency direction only when another module consumes it;
  do not move every VO into the database module merely because MPJ loads it.
- Add `@Schema` to the VO class and every exposed field. If the project uses records elsewhere,
  annotate the record and every record component as required by `openapi-schema.md`.

## List And Page Ordering

- Add a deterministic create-time descending default to MPJ list and page queries.
- Prefer the repository's audit getter, such as `getCreatedAt` or `getCreateTime`.
- If the request provides an explicit sort or the business rule requires another order, use it instead.
- Add a stable tie-breaker such as primary-key ascending/descending when create times can be equal.
- If the entity has no create-time field, use the repository's stable timestamp or ID order and make the reason clear when it is not obvious.

## Projection And SQL Rules

- Keep Mappers thin.
- Use annotation SQL only when raw SQL is necessary or clearer than Lambda/MPJ APIs.
- Do not expose wrappers to Controllers.
- Let the Service own a small, endpoint-specific wrapper when it calls official MPJ Service APIs.
- Put reusable, complex, or persistence-specific query logic in the DAO implementation.
- Keep `baseMapper` calls inside DAO implementations; never move them into Service or Controller.

## Audit Filling

MPJ does not replace audit filling. Use MyBatis-Plus `MetaObjectHandler` for insert/update audit
fields and preserve the repository's existing audit interceptor behavior.
