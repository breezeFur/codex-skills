# Data Change Checklist

Use this before any data-changing SQL.

## Required Steps

1. Confirm the exact target database.
2. Identify the table and primary key.
3. Inspect table schema and relevant indexes.
4. Draft the write SQL.
5. Draft a preview `SELECT` with the same `WHERE` clause.
6. Run the preview and record expected row count.
7. Use a transaction when supported.
8. Run the write.
9. Check affected row count.
10. Verify with a post-change `SELECT`.
11. Commit only when the result matches the user's intent.

## Guardrails

- No broad `UPDATE` or `DELETE` without `WHERE`.
- No `TRUNCATE`, `DROP`, or irreversible DDL unless the user explicitly asks for that exact operation.
- No schema migrations without checking application expectations.
- No mass changes based only on a guessed condition.
- Back up important rows before changing them when practical:

```sql
CREATE TABLE backup_table_name AS
SELECT *
FROM target_table
WHERE ...;
```

Use a timestamped backup table name and confirm storage impact before large backups.

## Response Format

After a write, report:

- SQL category: insert/update/delete/ddl
- target table
- affected rows
- verification query result summary
- transaction status: committed or rolled back

