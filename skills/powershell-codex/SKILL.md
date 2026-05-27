---
name: powershell-codex
description: Guide Codex to use reliable PowerShell commands on Windows for code reading, file search, encoding-safe Chinese text output, pipelines, quoting, path handling, and command verification. Use when Codex inspects code with PowerShell, runs rg/Get-Content/Get-ChildItem/Select-String, sees garbled Chinese text, hits PowerShell pipeline or quoting errors, needs clean reruns after failed scan commands, or works in Windows repositories with Chinese filenames or UTF-8 source files.
---

# PowerShell Codex

## First Move

For any PowerShell command that may print source code, Chinese text, JSON, YAML, Markdown, or logs, start the command with:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8;
```

Use `Get-Content -Encoding UTF8` when reading text files. If UTF-8 still looks wrong, retry with `-Encoding Default` and say the file may not be UTF-8.

## Search Rules

- Prefer `rg` and `rg --files` for repository search.
- Use `rg --files <path>` to list files.
- Use `rg -n "pattern" <path>` to search content.
- Quote paths with single quotes: `'D:/path/with spaces'`.
- For literal patterns with regex metacharacters, use `rg -n -F "literal text" <path>`.
- Exclude noisy folders with repeated `-g` flags, for example `-g '!target/**' -g '!node_modules/**' -g '!logs/**'`.
- Keep search commands simple. If a pipeline becomes clever, split it into two commands.

## Read Rules

Use direct, encoding-explicit reads:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8; Get-Content -LiteralPath 'D:/repo/src/App.java' -Encoding UTF8 -TotalCount 160
```

For line-numbered snippets, use this stable pattern:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8; $i=0; Get-Content -LiteralPath 'D:/repo/src/App.java' -Encoding UTF8 | ForEach-Object { $i++; if ($i -ge 40 -and $i -le 90) { '{0}: {1}' -f $i, $_ } }
```

Prefer `-LiteralPath` for paths supplied by the user or paths that may contain brackets, wildcard characters, spaces, or Chinese characters.

## Pipeline Rules

- Do not mix aliases like `%`, `?`, `cat`, `ls`, or `gc` in skill-guided commands. Use full cmdlet names.
- Do not put complex script blocks after native commands unless needed.
- Avoid chaining multiple unrelated operations with `;`. One setup prefix plus one clear operation is fine.
- When filtering objects, use `Where-Object` and `Select-Object` with explicit properties.
- When a pipeline fails, rerun a smaller command instead of patching an increasingly complex one.

Good:

```powershell
rg --files 'D:/repo' | Select-Object -First 80
```

Good:

```powershell
Get-ChildItem -LiteralPath 'D:/repo' -Recurse -Filter pom.xml | Select-Object -ExpandProperty FullName
```

Avoid:

```powershell
rg --files D:/repo | % { $_ -replace ... } | ? { ... } | select -first 80
```

## Encoding And Garbled Text

If output is garbled:

1. Rerun with the UTF-8 prefix and `Get-Content -Encoding UTF8`.
2. If the file itself may be legacy encoded, rerun with `Get-Content -Encoding Default`.
3. For JSON or structured data, prefer reading the raw file with `Get-Content -Raw -Encoding UTF8` before parsing.
4. Do not draw conclusions from garbled comments, annotations, SQL, or docs.

## Mojibake Safety

Treat repeated mojibake-looking fragments, replacement glyphs, private-use glyphs, or Chinese-looking nonsense produced by decoding UTF-8 as a legacy Windows code page as an encoding failure, not as reliable source content.

- Do not use garbled Chinese text as context for code edits.
- Do not copy garbled Chinese from terminal output into `apply_patch`.
- Do not rewrite comments, string literals, annotations, SQL, JSON, or documentation based on garbled output.
- Before editing a file that contains Chinese, get one clean UTF-8 read with `Get-Content -LiteralPath ... -Encoding UTF8`.
- If clean UTF-8 reading still shows mojibake, assume the file content may already be corrupted. Check Git history, IDE local history, backups, or another trusted source before changing nearby code.
- If an edit accidentally duplicated code around garbled Chinese, inspect the file with line numbers from a clean read before removing or rewriting anything.

Typical mojibake shape:

```text
garbled Chinese-looking text with repeated nonsense fragments around otherwise readable numbers or punctuation
```

This likely represents UTF-8 Chinese that was decoded as a legacy Windows code page. It must not be treated as the intended Java string literal.

## Safe File Inspection Patterns

List top-level directory:

```powershell
Get-ChildItem -Force -LiteralPath 'D:/repo'
```

Find files by name:

```powershell
Get-ChildItem -LiteralPath 'D:/repo' -Recurse -Filter '*.java' | Select-Object -First 80 -ExpandProperty FullName
```

Search Java annotations:

```powershell
rg -n "@RestController|@Service|@Mapper|@Schema|@Operation" 'D:/repo' -g '*.java' -g '!target/**'
```

Read Maven properties:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8; Select-String -LiteralPath 'D:/repo/pom.xml' -Encoding UTF8 -Pattern '<java.version>|<spring-boot.version>|<mybatis-plus.version>' -Context 0,1
```

## Failure Recovery

When a command fails with a parser, pipeline, or encoding issue:

- State the specific failure briefly.
- Rerun a simpler command with explicit UTF-8 encoding.
- Prefer separate commands over dense one-liners.
- Do not claim a file lacks content until a clean read succeeds.

## Edit Rules

This skill is for inspection commands. For file edits, follow the active coding instructions: prefer `apply_patch` for manual edits and avoid shell write tricks.

When editing files with Chinese text, only patch from clean UTF-8 source context. If the only available context is mojibake, stop and recover clean text first.
