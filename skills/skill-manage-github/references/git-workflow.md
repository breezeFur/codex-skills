# Git Workflow

Use this workflow for Codex skills sync repositories.

## Initialize

```powershell
git init
git branch -M main
git status --short --ignored
git add .gitattributes .gitignore AGENTS.md README.md skills
git commit -m "Initialize Codex skills sync repo"
```

If PowerShell rejects a Bash-style command separator, rerun the commands separately.

## GitHub CLI

Check whether GitHub CLI is available:

```powershell
gh --version
```

If it is installed and the user wants Codex to create a remote:

```powershell
gh auth status
gh repo create <repo-name> --private --source . --remote origin --push
```

If `gh` is unavailable or unauthenticated, ask the user for an existing GitHub remote URL after they create an empty private repository.

## Existing Remote

```powershell
git remote -v
git remote add origin <remote-url>
git push -u origin main
```

If `origin` already exists and the user wants to replace it:

```powershell
git remote set-url origin <remote-url>
```

## Verify

```powershell
git status --short --branch --ignored
git log --oneline --decorate -2
git remote -v
```

Expected result after a clean push:

- Branch tracks `origin/main`.
- No unexpected modified or untracked files except intentionally ignored local docs such as `vibecodingdoc/`.
- Latest commit message matches the performed change.
