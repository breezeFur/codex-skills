# README Sync Content

A Codex skills sync README should be practical and copy-paste friendly.

## Include

- What the repository syncs:
  - `AGENTS.md`
  - `skills/`
- What the repository must not sync:
  - `.system`
  - `plugins/cache`
  - sessions, logs, runtimes, credentials, local databases, generated caches
- Current remote URL, if known and not sensitive.
- Initial push commands.
- Clone commands for another device.
- Windows, Linux, and macOS symlink instructions.
- Daily update commands:
  - `git status`
  - `git add AGENTS.md skills`
  - `git commit -m "Update Codex skills"`
  - `git push`
  - `git pull --rebase`
- Safety reminder and secret scan command.

## Windows Symlink Pattern

Use symbolic links when permitted. Directory junctions are acceptable for skill directories if symbolic links are blocked.

```powershell
$Repo = "D:\codex-skills"
$CodexHome = Join-Path $env:USERPROFILE ".codex"
$Skills = @("skill-one", "skill-two")

New-Item -ItemType Directory -Force -Path (Join-Path $CodexHome "skills") | Out-Null

$AgentsPath = Join-Path $CodexHome "AGENTS.md"
if (Test-Path -LiteralPath $AgentsPath) {
    Move-Item -LiteralPath $AgentsPath -Destination "$AgentsPath.bak" -Force
}
New-Item -ItemType SymbolicLink -Path $AgentsPath -Target (Join-Path $Repo "AGENTS.md")

foreach ($Skill in $Skills) {
    $Link = Join-Path (Join-Path $CodexHome "skills") $Skill
    $Target = Join-Path (Join-Path $Repo "skills") $Skill

    if (Test-Path -LiteralPath $Link) {
        Move-Item -LiteralPath $Link -Destination "$Link.bak" -Force
    }

    New-Item -ItemType SymbolicLink -Path $Link -Target $Target
}
```

Junction fallback for a skill directory:

```powershell
New-Item -ItemType Junction -Path "$env:USERPROFILE\.codex\skills\java-dev" -Target "D:\codex-skills\skills\java-dev"
```

Junctions only work for directories. `AGENTS.md` still needs a file symlink or manual copy.

## Linux/macOS Symlink Pattern

```bash
REPO="$HOME/codex-skills"
CODEX_HOME="${CODEX_HOME:-$HOME/.codex}"
SKILLS="skill-one skill-two"

mkdir -p "$CODEX_HOME/skills"

if [ -e "$CODEX_HOME/AGENTS.md" ] || [ -L "$CODEX_HOME/AGENTS.md" ]; then
  mv "$CODEX_HOME/AGENTS.md" "$CODEX_HOME/AGENTS.md.bak"
fi
ln -s "$REPO/AGENTS.md" "$CODEX_HOME/AGENTS.md"

for skill in $SKILLS; do
  link="$CODEX_HOME/skills/$skill"
  target="$REPO/skills/$skill"

  if [ -e "$link" ] || [ -L "$link" ]; then
    mv "$link" "$link.bak"
  fi

  ln -s "$target" "$link"
done
```
