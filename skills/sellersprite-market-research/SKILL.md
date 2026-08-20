---
name: sellersprite-market-research
description: 使用已配置的 SellerSprite MCP 进行亚马逊市场调研与 AI 分析，覆盖市场初筛、候选商品选择、ASIN 深挖、关键词与评论证据、市场进入决策和报告输出。用户询问选品、类目研究、竞品、需求趋势、评论、关键词或 SellerSprite 市场报告时使用。
metadata:
  short-description: SellerSprite 市场调研与 AI 分析
---

# SellerSprite 市场调研

这个 Skill 把项目现有的市场调研工作流编排成 Agent 可执行的流程。SellerSprite 数据只能通过已配置的 MCP 服务 `sellersprite` 获取；不要绕过 MCP 直接请求 SellerSprite HTTP 接口，也不要在回答中暴露密钥、请求头或完整敏感配置。

## 适用边界

- 适用于亚马逊市场规模、类目机会、竞品结构、商品筛选、ASIN 经营分析、评论/VOC、关键词/流量和市场进入建议。
- 默认生成基于当前会话证据的分析结果或 Markdown 报告，不自动修改 Java/Vue 代码、数据库或项目配置。
- 当前 MCP 主要提供 45 个底层 SellerSprite API 工具。它不会自动创建项目数据库中的 `market_research_job`、SSE 事件或 Excel 产物；只有在用户另行提供项目 REST/工作流工具并明确要求时，才操作这些系统。

## 开始前检查

1. 确认当前环境存在 MCP 服务 `sellersprite`，并能发现以 `sellersprite_` 开头的工具。若 MCP 不可用、工具列表为空或名称不匹配，直接说明阻塞原因，不要伪造调研结果。
2. 使用工具前先读取该工具的实际 input schema。请求 DTO 的必填字段、枚举值、分页字段和日期格式以 MCP schema 为准，不要凭记忆拼接请求。
3. 从用户请求中提取并规范化：
   - `marketplace`：例如 `US`、`JP`、`UK`、`DE`、`FR`、`IT`、`ES`、`CA`、`IN`；
   - `nodeIdPath`：类目节点路径；缺失时可先调用 `sellersprite_list_product_nodes` 辅助定位；
   - `month`：业务月份，统一记录为 `YYYY-MM`，调用工具时按 schema 要求转换；
   - `keyword`：目标关键词或产品词；
   - `seedAsins`：用户指定的 ASIN；
   - `analysisGoal`：例如市场进入、竞争强度、差异化、退货风险或广告获客难度。
4. 只询问会改变采集范围的缺失信息。若用户只要求快速判断，可采用明确标注的合理假设；若要完整报告，至少应有站点、类目/关键词、月份和分析目标。

## 工作流

根据用户要求选择模式：

- `screening`：只做阶段一市场初筛，输出市场规模、趋势、竞争结构和候选 ASIN。
- `deep-dive`：用户已给出 ASIN，或已明确选择阶段一候选后，做评论、VOC、关键词和 ASIN 趋势深挖。
- `full`：先执行阶段一；得到候选后暂停，请用户选择一个或多个 ASIN，再继续阶段二和最终决策。不得未经授权替用户选品。
- `final`：仅当阶段一和阶段二证据已经存在于当前会话或用户提供的可靠数据中时使用；最终分析优先复用已有证据，不重复进行全量采集。

完整工作流的逻辑顺序是：

`screening → product selection gate → deep-dive → final decision`

### 阶段一：市场初筛

先调用 `sellersprite_get_account_visits` 检查配额，然后在同一站点、类目路径、月份和分析目标下采集：

- 商品候选：`sellersprite_research_products`；必要时先用 `sellersprite_list_product_nodes` 找类目路径，或用 `sellersprite_lookup_competitors` 补充竞品入口。
- 行业销售趋势：对 `sellersprite_get_market_statistics` 按目标月份逐月调用，保持相同类目、`topN` 和新品定义，再做可复算的月度汇总。
- 行业需求及趋势：`sellersprite_get_market_demand_trend`；有关键词时补充 `sellersprite_get_keyword_research_trends`。
- 细分市场现状：`sellersprite_research_markets` 和 `sellersprite_get_market_statistics`。
- 市场结构：按需要调用 `sellersprite_get_market_shelf_time_distribution`、`sellersprite_get_market_shelf_trend_distribution`、`sellersprite_get_market_price_distribution`、`sellersprite_get_market_ratings_distribution`、`sellersprite_get_market_rating_distribution`、`sellersprite_get_market_ebc_distribution`。
- 竞争结构：调用 `sellersprite_get_market_goods_concentration`、`sellersprite_get_market_brand_concentration`、`sellersprite_get_market_seller_concentration`、`sellersprite_get_market_seller_location_distribution` 和 `sellersprite_get_market_seller_type_distribution`。

阶段一分析必须围绕七张证据表组织：`US`、`行业销售趋势`、`行业需求及趋势`、`细分市场现状`、`细分市场退货率`、`竞品品牌`、`商品集中度`。后四张表可由多个 MCP 响应确定性整理而成；不要把 MCP 原始响应直接当成最终证据表。

阶段一结束时：

- 给出默认候选 Top 20 或实际返回的候选列表，并说明排序依据、样本范围和缺失字段。
- 可以给候选打“值得深挖 / 需谨慎 / 暂不建议”标签，但每个标签必须引用可见证据。
- 如果用户要求 `full`，停在选择关卡，要求用户确认 ASIN；不要默默进入阶段二。

### 阶段二：ASIN 深挖

针对用户选中的 ASIN，逐个调用并记录筛选边界：

- 商品和经营趋势：`sellersprite_get_asin_detail`、`sellersprite_get_asin_sales_trend`、`sellersprite_get_keepa_trend`。
- 评论与 VOC：`sellersprite_list_reviews`。保留星级、评论类型、页码、样本量等筛选条件；如是定向抽样，只能分析该样本，不能外推全体评论。
- 关键词与获客信号：`sellersprite_research_keywords`、`sellersprite_mine_keywords`，必要时使用 `sellersprite_extend_traffic_keywords`、`sellersprite_reverse_traffic_keywords` 或 `sellersprite_reverse_order_keywords`。
- 需要外部趋势对照时才调用 `sellersprite_get_google_trends`、`sellersprite_research_aba_monthly`、`sellersprite_research_aba_weekly` 或 `sellersprite_get_aba_keyword_trends`，并标记它们与 Amazon 站内数据的来源差异。

阶段二分析必须围绕五张证据表组织：`评价`、`VOC`、`Keywords`、`ASIN销售趋势`、`ASIN运营趋势`。VOC、趋势统计和关键词聚合应保持可复算；不要因为模型读到了若干评论就声称代表总体消费者。

### 阶段三：最终决策

只有在阶段一和阶段二证据齐备后，输出最终市场进入决策。按以下十二章顺序组织：

1. `US`
2. `行业销售趋势`
3. `行业需求及趋势`
4. `细分市场现状`
5. `细分市场退货率`
6. `竞品品牌`
7. `商品集中度`
8. `评价`
9. `VOC`
10. `Keywords`
11. `ASIN销售趋势`
12. `ASIN运营趋势`

每章至少包含：证据范围、核心判断、主要风险、对决策的影响和置信度。最后给出：

- 是否进入市场：建议进入、条件进入或暂不进入；
- 推荐的目标商品/差异化方向；
- 需要补采或人工验证的关键问题；
- 支撑结论的工具调用和数据时间范围。

## 证据与分析规则

- 将内容分为“原始事实”“确定性推导”“AI 判断”“待核查假设”四类。比例、差值、环比、累计占比和波动率只有在输入字段足够时才计算，并写出计算口径。
- 每条重要结论都要能回溯到工具名、请求条件、时间范围和响应字段。工具失败、空响应、字段缺失和配额不足必须进入数据质量说明。
- 不编造销量、销售额、排名、评分、退货率、用户痛点、广告预算或 ROI。没有证据时写“未提供/无法判断”。
- PPC 建议竞价、最低价和最高价只能作为竞争或投放难度信号，不能推导实际广告预算、ACOS 或 ROI。
- 选中的 ASIN 是样本商品，不能外推为全市场；定向星级或评论类型样本不能外推总体差评率、平均星级或满意度。
- 不重复调用已经成功取得且参数相同的数据；需要重试时只修正输入或在明确瞬态错误后有限重试，并记录重试原因。
- 不为了“完整”盲目调用 45 个工具。优先调用能回答用户问题的最小集合；完整报告才扩展到对应阶段的证据集合，并先检查配额。
- 若当前环境另有 `generateResearchReportChart` 工具，可以用它生成确定性图表；没有该工具时不要自行伪造 Mermaid 图表或图表数值。

## 输出模板

普通回答采用以下紧凑结构：

1. 调研范围与假设；
2. 数据来源与数据质量；
3. 关键事实与确定性推导；
4. AI 分析：机会、竞争、风险和建议；
5. 候选 ASIN 或下一步选择；
6. 未覆盖问题与需要补采的数据。

完整报告还要按阶段和证据表输出，避免只给一个没有来源的“推荐/不推荐”结论。必要时把原始数据压缩为表格，但保留字段含义、单位、时间范围和样本边界。

## 参考资料

- 需要了解项目当前三阶段 Graph、证据表和最终报告契约时，读取 [references/project-workflow.md](references/project-workflow.md)。
- 需要快速确认工具分组和调用顺序时，读取 [references/tool-routing.md](references/tool-routing.md)。
