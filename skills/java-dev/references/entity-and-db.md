# Entity And Database

## Entity Rules

- Put Entities in the database module.
- Annotate Entities with `@TableName`.
- Annotate ID fields with `@TableId`.
- Annotate database fields with `@TableField` when the column name is not automatically obvious or when fill rules are required.
- Annotate Entity classes and fields with `@Schema`; descriptions must be Chinese for new code.
- Do not return Entities from Controllers.

## ID Rules

- New database IDs use UUIDv7.
- Hide UUIDv7 generation behind a central `IdGenerator` or existing project ID utility.
- Do not scatter direct calls to random UUID generation through Services or Controllers.
- If an external system owns the primary key, document that exception in code and keep the field name aligned with the external contract.

Example utility shape:

```java
public interface IdGenerator {
    String nextId();
}
```

The implementation may use a UUIDv7 library selected by the project. Keep the dependency isolated so it can be replaced.

## Audit Fields

Default new Entity audit fields:

```text
id
created_at / createdAt
updated_at / updatedAt
created_by / createdBy
updated_by / updatedBy
deleted / deleted
```

- Use Java `String` for IDs unless the existing project uses another type.
- Use Java `Long` for create/update time fields.
- Store time as Unix milliseconds.
- Prefer Java field names `createdAt`, `updatedAt`, `createdBy`, and `updatedBy`.
- Follow existing names if a repository already uses different audit names.
- Configure logical delete using MyBatis-Plus conventions when the table has a delete marker.

## Audit Filling

Use MyBatis-Plus `MetaObjectHandler`.

On insert:

- Fill `id` with UUIDv7 if the Entity has an ID field and it is blank.
- Fill `createdAt` with current Unix milliseconds.
- Fill `updatedAt` with current Unix milliseconds.
- Fill `createdBy` with the current user ID, or `system`.
- Fill `updatedBy` with the current user ID, or `system`.

On update:

- Fill `updatedAt` with current Unix milliseconds.
- Fill `updatedBy` with the current user ID, or `system`.

Read the current user from the existing request context holder. If none exists, create one in the framework module.

## Mapper And DAO

- Mapper interfaces live in the database module.
- DAO interfaces and DAO implementations live in the database module.
- Normal database reads and writes go through DAO implementations.
- Mapper methods should stay focused on MyBatis-Plus base mapping and necessary annotation SQL.
- Raw SQL is allowed only when the Lambda or MPJ APIs cannot express the query cleanly.
- The database module must not become a dumping ground for every DTO or VO.
- Keep DTO/VO classes in the database module only when they are DAO or Mapper query projections owned by the data-access layer.
- Keep module-local API DTOs and response VOs in the owning business module, preferably under `model.dto` and `model.vo`, even when the data comes from DAOs.
- If multiple business modules need the same DTO/VO and it is not a DAO/Mapper projection, move it to the lowest shared module allowed by the dependency graph rather than defaulting to the database module.
