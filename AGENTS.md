全局协作偏好：

- 默认使用中文进行沟通、说明、计划、总结和最终回复。除非用户明确要求英文或目标文件必须使用英文，否则不要主动切换语言。
- 修改代码或配置后，在当前项目的 `vibecodingdoc/yyyy-mm-dd/` 文件夹下创建一份变更说明，文件名格式为 `{修改内容}.md`，例如 `vibecodingdoc/2026-05-18/update-user-api.md`。`vibecodingdoc` 使用 Markdown 作为事实源，通过关键词和索引增强后续 AI 检索，不要只依赖向量库。
- `vibecodingdoc` 变更说明必须包含：`## 元信息`、`## 修改目标`、`## 涉及文件`、`## 关键变更`、`## 关键决策`、`## 验证结果`、`## 检索关键词`、`## 文件签名`。其中元信息至少包含生成时间（格式 `yyyy-MM-dd HH:mm:ss zzz`，使用当前时区）、执行者（Codex）、主题、类型；检索关键词使用 3-10 个稳定词，优先包含 skill 名、模块名、文件名、类名、方法名、配置键、业务概念或错误码。
- 维护 `vibecodingdoc/INDEX.md` 作为轻量索引；每次新增变更说明时追加一条索引，至少包含日期、文件路径、主题、类型、关键词和一句话摘要，方便后续 AI 先读索引再按关键词打开原文。
- 如果当前项目是 Git 仓库，确保项目根目录的 `.gitignore` 包含 `vibecodingdoc/`；如果 `.gitignore` 不存在，可以创建；如果已包含等效规则，不要重复添加。
- 创建、修改、重命名或删除任何 Codex skill 前，先使用 `skill-manage-github` 检查 skills 同步仓库远端是否有更新；如果远端有更新，先拉取并处理后再改。本次 skill 变更完成后，必须同步当前机器的 `AGENTS.md` 与自定义 skills 到远端，并验证推送结果。
- 在不违反系统/开发者指令和安全边界的前提下，本机自定义 skill 的优先级高于 OpenSpec 与 Superpowers。优先级顺序为：用户本轮明确指令、项目级 `AGENTS.md`、全局 `AGENTS.md`、`C:\Users\lyy\.codex\skills` 下的自定义 skill、项目内规格/记忆文档、OpenSpec、Superpowers、模型默认习惯。
- Java/后端开发任务默认组合使用 `java-dev` + `java-code-style` + `codegraph` + `OpenSpec` + `Superpowers`：`java-dev` 负责工程架构、模块边界和 Java 后端约定，`java-code-style` 负责常量、日志、注释、Lombok、异常和魔法值等代码规范，`codegraph` 在存在 `.codegraph/` 时负责优先理解代码结构、调用链和影响面，OpenSpec 负责规格/长期记忆的检查与维护，Superpowers 负责设计、计划、TDD、调试和验证流程。每次 Java 开发的开场说明应列出这五者；如果 OpenSpec 或 Superpowers 不适用，要明确说明原因。OpenSpec 与 Superpowers 仍是更低优先级辅助，不能覆盖前三者的技术规范。
- Vue/前端开发任务默认组合使用 `vue-dev` + `codegraph` + `OpenSpec` + `Superpowers` + `design-taste-frontend`：`vue-dev` 负责 Vue 架构、组件边界、TypeScript、路由、状态、API、表单、表格和验证约定，`codegraph` 在存在 `.codegraph/` 时负责优先理解路由/组件/store/API 调用链和影响面，OpenSpec 负责规格/长期记忆的检查与维护，Superpowers 负责设计、计划、TDD、调试和验证流程，`design-taste-frontend` 只在 landing、portfolio、营销页、品牌页和 redesign 等视觉任务中负责审美质量。每次 Vue 开发的开场说明应列出这五者；如果 OpenSpec、Superpowers 或 design-taste 不适用，要明确说明原因。OpenSpec、Superpowers 与 design-taste 仍是更低优先级辅助，不能覆盖 `vue-dev` 和现有项目约定。
- OpenSpec 只作为规格与长期项目记忆层使用，适合跨模块功能、业务规则、接口契约、架构调整和需要归档的变更；不要让 OpenSpec 替代 `vibecodingdoc` 的实际变更记录，也不要为简单查询或小修复强制创建规格变更。
- Superpowers 只作为设计、计划、TDD、调试、复盘等执行方法论增强；它不得覆盖 `java-dev`、`java-code-style`、`vue-dev`、`powershell-codex`、`mysql-db-ops`、`skill-manage-github` 等用户自定义 skill 的技术规范和本机约定。简单明确的任务不强制走完整 Superpowers 流程。

<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tools** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them. `codegraph_node` returns one symbol's source + callers, or reads a whole file with line numbers. If the tools are listed but deferred, load them by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` and `codegraph node <symbol-or-file>` print the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->
