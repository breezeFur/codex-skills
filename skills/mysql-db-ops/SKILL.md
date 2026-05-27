---
name: mysql-db-ops
description: Safely inspect and operate MySQL databases from Codex using runtime-provided credentials. Use when querying schemas, reading tables, checking data quality, writing SQL, explaining MySQL behavior, exporting sample rows, or making cautious data changes. Requires credentials to be supplied at task time or through environment variables; never store database passwords in skill files, scripts, logs, or committed artifacts.
---

# MySQL DB Ops

## Safety First

- Do not store hostnames, usernames, passwords, or production-like secrets in this skill or generated files.
- Use credentials only from the current user request or environment variables.
- Default to read-only inspection. Treat `INSERT`, `UPDATE`, `DELETE`, `TRUNCATE`, `ALTER`, `DROP`, and data imports as high-risk.
- Before any write operation, show the SQL, explain expected row impact, and ask for explicit confirmation unless the user already gave a precise, immediate write instruction.
- Prefer transactions for writes: `START TRANSACTION`, verify affected rows, then `COMMIT` only after confirmation. Use `ROLLBACK` when uncertain.
- Never run destructive broad writes without a restrictive `WHERE` clause and a preview `SELECT`.
- Avoid printing passwords in commands or final answers.

## Runtime Connection

Use runtime variables when possible:

```powershell
$env:MYSQL_HOST = '<host-from-user>'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = '<database-from-user>'
$env:MYSQL_USER = '<user-from-user>'
$env:MYSQL_PWD = '<password-from-user>'
```

Prefer `MYSQL_PWD` over putting `-p<password>` directly in command text because shell logs and terminal output may expose command arguments. Clear it after use when practical:

```powershell
Remove-Item Env:MYSQL_PWD
```

If a MySQL CLI is unavailable, use a project-local Java, Node, or Python database client already present in the workspace. If tools must be installed, install them under a dedicated D drive tools directory and add only tool binaries to PATH; do not persist database passwords.

## Inspection Workflow

1. Confirm the target database name and whether the task is read-only or may write.
2. Check connectivity without exposing the password.
3. Inspect schema before writing SQL:
   - `SHOW TABLES;`
   - `SHOW CREATE TABLE table_name;`
   - `DESCRIBE table_name;`
   - `SHOW INDEX FROM table_name;`
4. For data reads, start with small samples and explicit columns.
5. Use `LIMIT` for exploratory queries.
6. For joins, inspect indexes and cardinality before large queries.
7. For writes, produce a preview `SELECT` using the same `WHERE` condition.

## SQL Style

- Use explicit column lists; avoid `SELECT *` except for tiny ad hoc samples.
- Use backticks for reserved identifiers.
- Prefer parameterized SQL in application code.
- For one-off CLI queries, quote string literals carefully and avoid shell interpolation surprises.
- Include `ORDER BY` when using `LIMIT` and stable order matters.
- Use `EXPLAIN` for potentially expensive queries.
- Use `COUNT(*)` preview queries before bulk changes.

## Output Rules

- Summarize results instead of dumping large tables.
- Show small result samples only when useful.
- Redact secrets, tokens, phone numbers, emails, and other sensitive fields unless the user explicitly needs them.
- When giving SQL to the user, separate read-only SQL from write SQL.
- State whether a command was actually run or only drafted.

## References

- Read `references/mysql-powershell.md` for safe PowerShell MySQL CLI patterns.
- Read `references/change-checklist.md` before data-changing operations.
