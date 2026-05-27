# MySQL From PowerShell

Use `powershell-codex` patterns for encoding and clean command output.

## Connection Environment

Set credentials only for the current session:

```powershell
$env:MYSQL_HOST = '<host>'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = '<database>'
$env:MYSQL_USER = '<user>'
$env:MYSQL_PWD = '<password>'
```

Clear the password after use:

```powershell
Remove-Item Env:MYSQL_PWD
```

## MySQL CLI Pattern

```powershell
mysql --host=$env:MYSQL_HOST --port=$env:MYSQL_PORT --user=$env:MYSQL_USER --database=$env:MYSQL_DATABASE --default-character-set=utf8mb4 --batch --raw --execute "SHOW TABLES;"
```

Use `--default-character-set=utf8mb4` for Chinese text.

Use `--batch --raw` for machine-readable output and fewer table formatting artifacts.

## Safe Reads

Schema:

```powershell
mysql --host=$env:MYSQL_HOST --port=$env:MYSQL_PORT --user=$env:MYSQL_USER --database=$env:MYSQL_DATABASE --default-character-set=utf8mb4 --batch --raw --execute "SHOW CREATE TABLE table_name;"
```

Sample rows:

```powershell
mysql --host=$env:MYSQL_HOST --port=$env:MYSQL_PORT --user=$env:MYSQL_USER --database=$env:MYSQL_DATABASE --default-character-set=utf8mb4 --batch --raw --execute "SELECT id, created_at FROM table_name ORDER BY created_at DESC LIMIT 20;"
```

## Quoting

- Prefer double quotes around the `--execute` SQL string in PowerShell.
- Escape internal double quotes or use single quotes inside SQL string literals.
- Avoid embedding passwords in the command.

## Missing CLI

If `mysql` is not installed:

1. Check whether the repository has a Java, Node, or Python database utility dependency.
2. Use a local script only when needed.
3. If a CLI tool needs to be installed, install it under a dedicated D drive tools directory such as `D:\tools\mysql-client` or `D:\dev-tools\mysql-client`.
4. Add the tool `bin` directory to the current session PATH first:

```powershell
$env:Path = 'D:\tools\mysql-client\bin;' + $env:Path
```

5. If the user wants persistent access, write only the tool path to the user PATH. Do not persist `MYSQL_PWD` or other database passwords:

```powershell
[Environment]::SetEnvironmentVariable(
    'Path',
    'D:\tools\mysql-client\bin;' + [Environment]::GetEnvironmentVariable('Path', 'User'),
    'User'
)
```

6. Do not add database tooling dependencies to the user project just to inspect a database unless requested.
