# MyBatis-Plus-Join

## Data Access Layers

Use these layers for database code:

- `entity`: database table entities.
- `mapper`: MyBatis-Plus Mapper interfaces and necessary annotation SQL.
- `dao`: business-facing data access interfaces.
- `dao.impl`: DAO implementations using MyBatis-Plus and MPJ APIs.

Service code calls DAO interfaces. Controllers never access DAO, Mapper, or Wrapper objects directly.

## DAO Implementation Rules

- Prefer MyBatis-Plus Lambda APIs for single-table operations.
- Prefer `MPJLambdaWrapper` for multi-table queries.
- Keep wrapper construction inside DAO implementations or query-specific Services.
- Return query projection DTOs/VOs from DAO implementations when the DAO owns the query shape or multiple modules share the projection.
- Keep one-module response VOs in the owning business module's `model.vo` package; do not move them to the database module only because the data was loaded through DAO calls.
- If the existing DAO API exposes Lambda/MPJ-capable operations and a business Service assembles a module-specific response, the Service may keep that module-specific VO in the business module.
- Shared business DTOs/VOs should move down according to dependency direction, but only DAO/Mapper-owned query projections belong in the database module.
- Do not expose `QueryWrapper`, `LambdaQueryWrapper`, `LambdaUpdateWrapper`, or `MPJLambdaWrapper` outside the data access or query layer.

## Mapper Rules

- Keep Mappers thin.
- Use Mapper annotation SQL only for raw SQL that is clearer or necessary.
- Allowed annotations include `@Select`, `@Update`, `@Insert`, and `@Delete`.
- Do not put ordinary business queries in Mapper methods when a DAO implementation can express them with Lambda or MPJ APIs.

## MPJ Query Pattern

Use the existing project style first. A typical pattern:

```java
MPJLambdaWrapper<OrderEntity> wrapper = new MPJLambdaWrapper<OrderEntity>()
        .selectAs(OrderEntity::getId, OrderDetailVo::getId)
        .selectAs(UserEntity::getName, OrderDetailVo::getUserName)
        .leftJoin(UserEntity.class, UserEntity::getId, OrderEntity::getUserId)
        .eq(OrderEntity::getId, id);

return selectJoinOne(OrderDetailVo.class, wrapper);
```

Adapt the base class and method names to the project's MPJ version and DAO superclass.

## List And Page Ordering

- For `MPJLambdaWrapper` or project-style `mpjLambda` list and paginated queries, add default create-time descending order.
- Prefer the project's audit field name, such as `Entity::getCreatedAt`, `Entity::getCreateTime`, or the existing create-time getter.
- If the request already provides a sort field, or the business rule requires another stable order, use the explicit order instead.
- If the entity has no create-time field, use the existing project-standard stable timestamp or ID order and make the reason clear in review or a short code comment when it is not obvious.

```java
MPJLambdaWrapper<OrderEntity> wrapper = new MPJLambdaWrapper<OrderEntity>()
        .selectAs(OrderEntity::getId, OrderListVo::getId)
        .selectAs(UserEntity::getName, OrderListVo::getUserName)
        .leftJoin(UserEntity.class, UserEntity::getId, OrderEntity::getUserId)
        .eq(OrderEntity::getStatus, request.getStatus())
        .orderByDesc(OrderEntity::getCreatedAt);

return selectJoinPage(page, OrderListVo.class, wrapper);
```

## Pagination

- Use the project page request and page response types.
- Do not return MyBatis-Plus `IPage` directly from Controllers.
- Convert database page results to typed `PageResult<XxxVo>` or the existing project page wrapper.

## Audit Filling

MPJ does not replace audit filling. Use MyBatis-Plus `MetaObjectHandler` for insert/update audit fields. If a project already has an interceptor chain for audit behavior, keep the behavior consistent with `MetaObjectHandler`.
