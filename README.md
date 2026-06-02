# Codex Skills Sync

这个仓库用于在多台设备之间同步 Codex 的自定义配置：

- `AGENTS.md`：全局协作偏好
- `skills/`：自定义 skill

当前已同步的 skill：

- `codegraph`
- `java-code-style`
- `java-dev`
- `mysql-db-ops`
- `powershell-codex`
- `skill-manage-github`

不要把 Codex 自动管理的目录放进来，例如 `.system`、`plugins/cache`。

## 初始化远端仓库

本机已经是一个 Git 仓库。当前远端仓库：

```text
https://github.com/breezeFur/codex-skills.git
```

你可以执行：

```bash
git remote add origin https://github.com/breezeFur/codex-skills.git
git branch -M main
git push -u origin main
```

另一台设备拉取：

```bash
git clone https://github.com/breezeFur/codex-skills.git ~/codex-skills
```

Windows 也可以克隆到例如：

```powershell
git clone https://github.com/breezeFur/codex-skills.git D:\codex-skills
```

## Windows 软链接

PowerShell 建议以管理员身份运行，或者开启 Windows 开发者模式。

```powershell
$Repo = "D:\codex-skills"
$CodexHome = Join-Path $env:USERPROFILE ".codex"
$Skills = @("codegraph", "java-code-style", "java-dev", "mysql-db-ops", "powershell-codex", "skill-manage-github")

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

如果目录软链接权限受限，也可以把 skill 目录改成 Junction：

```powershell
New-Item -ItemType Junction -Path "$env:USERPROFILE\.codex\skills\java-dev" -Target "D:\codex-skills\skills\java-dev"
```

注意：Junction 只能链接目录，`AGENTS.md` 仍需要文件软链接或手动复制。

## Linux 软链接

```bash
REPO="$HOME/codex-skills"
CODEX_HOME="${CODEX_HOME:-$HOME/.codex}"
SKILLS="codegraph java-code-style java-dev mysql-db-ops powershell-codex skill-manage-github"

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

## macOS 软链接

```bash
REPO="$HOME/codex-skills"
CODEX_HOME="${CODEX_HOME:-$HOME/.codex}"
SKILLS="codegraph java-code-style java-dev mysql-db-ops powershell-codex skill-manage-github"

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

## 日常同步

修改 skill 或 `AGENTS.md` 后：

```bash
git status
git add AGENTS.md skills
git commit -m "Update codex skills"
git push
```

其他设备更新：

```bash
git pull --rebase
```

如果已经按上面的命令创建了软链接，`git pull` 后 Codex 使用的文件会自动更新。必要时重启 Codex 或重新打开会话。

## 安全提醒

- 不要提交数据库密码、API Key、Token、Cookie 等敏感信息。
- 数据库连接信息、密钥和本机路径优先放在环境变量或本机私有配置里。
- 提交前可以扫描一次：

```bash
rg -n -i "password|token|secret|api[_-]?key|apikey|密钥|密码|令牌"
```
