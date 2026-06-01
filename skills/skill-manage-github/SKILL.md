---
name: skill-manage-github
description: Create and maintain a GitHub-backed Codex configuration sync repository. Use when the user wants to share Codex custom skills or AGENTS.md across devices, create a local Git repository for Codex skills, copy selected skills into a repo, write README symlink instructions for Windows/Linux/macOS, add GitHub remotes, push the repo, update synced skills, handle GitHub CLI absence, or verify that skill sync repositories avoid secrets and Codex-managed cache folders. Also use whenever any Codex skill is created, updated, renamed, or deleted so remote updates are checked before edits and local skill changes are pushed afterward.
---

# Skill Manage GitHub

## Overview

This skill maintains a GitHub repository used to sync Codex user configuration across machines. It focuses on `AGENTS.md` and user-authored skills under `$CODEX_HOME/skills`, not general application source code.

## Scope

Use this skill for:

- Building a local repository that contains `AGENTS.md` and custom skills.
- Updating a skills sync repository after local skill changes.
- Writing README instructions for clone, push, pull, and symlink setup across Windows, Linux, and macOS.
- Adding or changing a GitHub remote and pushing `main`.
- Verifying secrets, ignored folders, line endings, and Git status before commit.

Do not include Codex-managed directories such as `.system`, `plugins/cache`, runtime caches, logs, or session history.

## Skill Update Rule

Whenever any Codex skill is created, updated, renamed, or deleted:

1. Before editing the local skill, inspect the sync repository and fetch the remote:

   ```powershell
   git -C <sync-repo> status --short --branch --ignored
   git -C <sync-repo> fetch origin
   git -C <sync-repo> rev-list --left-right --count origin/main...main
   ```

2. If the remote is ahead, pull or rebase before making local skill edits.
3. After the skill edit is complete, copy current `AGENTS.md` and all user-authored skills into the sync repo.
4. Run the secret and excluded-folder checks.
5. Commit, push, and verify `main` tracks `origin/main`.

## Workflow

1. Locate the relevant Codex home:

   - Prefer `$env:CODEX_HOME` if present.
   - On Windows, default to `$env:USERPROFILE\.codex`.
   - On Linux/macOS, default to `$HOME/.codex`.

2. Inspect current custom items before editing:

   ```powershell
   [Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $OutputEncoding = [System.Text.Encoding]::UTF8;
   Get-ChildItem -Force -LiteralPath "$env:USERPROFILE\.codex\skills" |
       Where-Object { $_.Name -notin @(".system") -and $_.Name -notlike ".*cache*" } |
       Select-Object Name,FullName
   ```

3. Create or update the repository structure:

   ```text
   repo/
   |-- AGENTS.md
   |-- README.md
   |-- .gitignore
   |-- .gitattributes
   `-- skills/
       `-- <custom-skill>/
   ```

4. Copy only user-authored skills and `AGENTS.md`.

   - Preserve skill directories as directories.
   - Preserve `agents/`, `references/`, `assets/`, and `scripts/` inside each skill.
   - Skip `.system`, `plugins/cache`, generated runtime folders, databases, credentials, and logs.

5. Add repository hygiene files:

   - `.gitignore` must contain `vibecodingdoc/`.
   - `.gitattributes` should normalize text line endings with LF for cross-device sync.
   - README must include clone, push, pull, and symlink instructions. See `references/readme-sync.md`.

6. Scan for secrets before commit:

   ```powershell
   rg -n -i "password|token|secret|api[_-]?key|apikey" .
   ```

   Treat hits in safety reminders or placeholders as acceptable only after reading them. Do not commit real tokens, passwords, cookies, private keys, database credentials, or machine-only config.

7. Initialize and commit:

   ```powershell
   git init
   git branch -M main
   git status --short --ignored
   git add .gitattributes .gitignore AGENTS.md README.md skills
   git commit -m "Initialize Codex skills sync repo"
   ```

8. Add a GitHub remote and push:

   - If `gh` is installed and authenticated, creating a private repository is acceptable when the user asks.
   - If `gh` is unavailable, ask the user for an existing remote URL or tell them to create an empty private repository, then run:

   ```powershell
   git remote add origin <remote-url>
   git push -u origin main
   ```

9. Verify after push:

   ```powershell
   git status --short --branch --ignored
   git log --oneline --decorate -2
   git remote -v
   ```

## Update Existing Sync Repo

When the repository already exists:

1. Run `git status --short --branch` first and preserve unrelated user changes.
2. Run `git fetch origin` and check `origin/main...main` before copying local changes.
3. If remote updates exist, pull or rebase before continuing.
4. Copy changed custom skills or `AGENTS.md` into the repo.
5. Run the secret scan.
6. Stage only intended paths.
7. Commit with a clear message such as `Update Codex skills`.
8. Push and verify branch tracking.

## PowerShell Notes

When operating on Windows, follow `powershell-codex`:

- Prefix commands that print Markdown, YAML, JSON, or Chinese text with UTF-8 output settings.
- Use `Get-Content -LiteralPath ... -Encoding UTF8`.
- Avoid Bash-only separators such as `&&` in PowerShell. Run separate commands instead.
- Use `-LiteralPath` for paths supplied by the user.

## References

- Read `references/readme-sync.md` when writing or updating README symlink instructions.
- Read `references/git-workflow.md` when initializing, committing, adding a remote, or pushing.
- Read `references/safety-checks.md` before committing or pushing.
