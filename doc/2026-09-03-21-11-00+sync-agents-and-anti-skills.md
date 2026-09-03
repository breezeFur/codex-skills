# sync-agents-and-anti-skills

## 变更概述

将全局协作偏好、Global AI Rules 与 CodeGraph 规范写入全局 `AGENTS.md` / `GEMINI.md`，并将 Codex 的全部自定义 skills 复制至 Antigravity (`anti`) 配置目录，实现双平台规则与技能环境对齐。

## 变更明细

1. **全局规则与配置更新**：
   - 更新 `C:\Users\lyy\.gemini\GEMINI.md`，注入 Global AI Rules（中文回复、/doc/ 变更说明、@Slf4j 追踪日志）、全局协作偏好（中文沟通、个人记录知识库、vibecodingdoc 规范、skill 同步与优先级、Java/Vue 技术栈约定、OpenSpec 边界）以及 CodeGraph 使用指南。
   - 创建 `C:\Users\lyy\.gemini\AGENTS.md` 与 `C:\Users\lyy\.gemini\config\AGENTS.md` 保持全局规则一致。
   - 更新 `C:\Users\lyy\.codex\AGENTS.md` 与 `D:\develop\project\codex-skills\AGENTS.md` 保持一致。

2. **Skill 同步至 Antigravity**：
   - 将 `D:\develop\project\codex-skills\skills` 下的 12 个自定义 skills 全部复制至 `C:\Users\lyy\.gemini\config\skills\`：
     - `codegraph`
     - `design-taste-frontend`
     - `java-code-style`
     - `java-dev`
     - `mysql-db-ops`
     - `openspec`
     - `pdf`
     - `playwright`
     - `powershell-codex`
     - `sellersprite-market-research`
     - `skill-manage-github`
     - `vue-dev`
   - 创建 `C:\Users\lyy\.gemini\config\skills.json` 显式注册全局 skills 目录。
   - 创建 `C:\Users\lyy\.gemini\skills` 与 `C:\Users\lyy\.gemini\antigravity\skills` 的目录联接（Junction）指向 `C:\Users\lyy\.gemini\config\skills`，确保各发现路径均可正确解析。

3. **变更说明与索引**：
   - 创建 `vibecodingdoc/2026-09-03/sync-anti-skills-and-agents.md`。
   - 追加记录至 `vibecodingdoc/INDEX.md`。

## 验证

- 远端同步检查：`git rev-list --left-right --count origin/main...main` 为 `0 0`。
- 敏感信息扫描：无泄露密钥与私密数据。
- 12 个 skill 均已成功复制到 Antigravity 全局 skills 目录并存在有效 `SKILL.md`。
- 全局 `GEMINI.md` 与 `AGENTS.md` 语法与格式验证通过。
