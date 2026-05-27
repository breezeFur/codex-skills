# Safety Checks

Run these checks before committing or pushing a Codex configuration sync repository.

## Include Only User Config

Allowed by default:

- `AGENTS.md`
- `skills/<custom-skill>/SKILL.md`
- `skills/<custom-skill>/agents/`
- `skills/<custom-skill>/references/`
- `skills/<custom-skill>/assets/`
- `skills/<custom-skill>/scripts/`
- README and Git hygiene files

Exclude by default:

- `.system`
- `plugins/cache`
- sessions and rollout JSONL
- logs
- runtime caches
- local databases
- credentials and machine-only config
- generated build output

## Secret Scan

```powershell
rg -n -i "password|token|secret|api[_-]?key|apikey|密钥|密码|令牌" .
```

Read every hit. Safety reminders, examples, or placeholders are acceptable. Real secrets are not.

## Line Endings

For cross-device sync, add `.gitattributes`:

```gitattributes
* text=auto eol=lf
*.bat text eol=crlf
*.cmd text eol=crlf
*.ps1 text eol=crlf
```

If line-ending warnings appear on Windows, explain that they are warnings, then normalize deliberately:

```powershell
git add --renormalize .
```

## Change Log

If the active workspace has the user's rule requiring change notes, create `vibecodingdoc/yyyy-mm-dd/<change>.md` locally and keep `vibecodingdoc/` in `.gitignore`.
