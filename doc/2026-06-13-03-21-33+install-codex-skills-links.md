# install-codex-skills-links

## 变更概述

将 `D:\develop\project\codex-skills` 仓库接入当前机器的 Codex 配置目录。

## 变更明细

- 备份原 `C:\Users\lyy\.codex\AGENTS.md` 到 `C:\Users\lyy\.codex\AGENTS.md.bak.20260613031944`。
- 创建 `C:\Users\lyy\.codex\AGENTS.md` 到 `D:\develop\project\codex-skills\AGENTS.md` 的文件符号链接。
- 创建以下 skill 目录符号链接：
  - `C:\Users\lyy\.codex\skills\codegraph`
  - `C:\Users\lyy\.codex\skills\java-code-style`
  - `C:\Users\lyy\.codex\skills\java-dev`
  - `C:\Users\lyy\.codex\skills\mysql-db-ops`
  - `C:\Users\lyy\.codex\skills\powershell-codex`
  - `C:\Users\lyy\.codex\skills\skill-manage-github`
- 新增 `vibecodingdoc/2026-06-13/install-codex-skills-links.md` 记录本次配置接入。
- 新增 `vibecodingdoc/INDEX.md` 作为本地检索索引。

## 验证

- 远端同步检查：`git rev-list --left-right --count origin/main...main` 输出 `0 0`。
- `AGENTS.md` 链接验证通过：`LinkType` 为 `SymbolicLink`，目标为仓库内 `AGENTS.md`。
- 6 个 skill 链接验证通过，均可通过 `C:\Users\lyy\.codex\skills\<skill>\SKILL.md` 读取。

## 备注

`vibecodingdoc/` 已由仓库 `.gitignore` 忽略；`doc/` 目录用于满足全局变更日志规则。
