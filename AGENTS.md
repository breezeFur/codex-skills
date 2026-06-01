全局协作偏好：

- 默认使用中文进行沟通、说明、计划、总结和最终回复。除非用户明确要求英文或目标文件必须使用英文，否则不要主动切换语言。
- 修改代码后，在当前项目的 `vibecodingdoc/yyyy-mm-dd/` 文件夹下创建一份变更说明，文件名格式为 `{修改内容}.md`，例如 `vibecodingdoc/2026-05-18/update-user-api.md`。内容应简要记录本次修改目标、涉及文件、关键变更和验证结果。
- 如果当前项目是 Git 仓库，确保项目根目录的 `.gitignore` 包含 `vibecodingdoc/`；如果 `.gitignore` 不存在，可以创建；如果已包含等效规则，不要重复添加。
- 创建、修改、重命名或删除任何 Codex skill 前，先使用 `skill-manage-github` 检查 skills 同步仓库远端是否有更新；如果远端有更新，先拉取并处理后再改。本次 skill 变更完成后，必须同步当前机器的 `AGENTS.md` 与自定义 skills 到远端，并验证推送结果。
