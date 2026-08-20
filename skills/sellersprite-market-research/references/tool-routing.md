# SellerSprite MCP 工具路由

本参考基于 2026-08-20 导出的 SellerSprite MCP schema。工具数量、名称和参数可能随 MCP 更新；使用前必须用 ToolSearch 发现实时工具并读取实际 schema。本文件中的工具 ID 用于路由和校验，不覆盖实时 schema。

## 发现与调用原则

1. 搜索 SellerSprite MCP，确认当前服务和工具可用。
2. 用功能描述匹配工具，再读取候选工具的实时 input schema。
3. 只调用回答当前问题所需的最小集合；完整报告才扩展到对应证据表。
4. 记录实际工具 ID 和关键参数，不要把不存在的旧别名当成可调用工具。

当前导出工具常见前缀为 `mcp__sellersprite-mcp__`。如果运行时 ID 不同，以运行时返回为准。

## 当前工具分组

### ABA 关键词研究

- `mcp__sellersprite-mcp__aba_research_monthly`
- `mcp__sellersprite-mcp__aba_research_weekly`
- `mcp__sellersprite-mcp__aba_research_trend`

### ASIN 深度分析

- `mcp__sellersprite-mcp__asin_detail`
- `mcp__sellersprite-mcp__asin_detail_with_coupon_trend`
- `mcp__sellersprite-mcp__asin_coupon_trend`
- `mcp__sellersprite-mcp__asin_competitor`
- `mcp__sellersprite-mcp__asin_prediction`
- `mcp__sellersprite-mcp__asin_sales_trend`

### Keepa

- `mcp__sellersprite-mcp__keepa_info`

### 商品、类目和竞品

- `mcp__sellersprite-mcp__product_node`
- `mcp__sellersprite-mcp__product_research`
- `mcp__sellersprite-mcp__competitor_lookup`
- `mcp__sellersprite-mcp__bsr_prediction`
- `mcp__sellersprite-mcp__review`

### 市场和类目结构

- `mcp__sellersprite-mcp__market_research`
- `mcp__sellersprite-mcp__market_research_statistics`
- `mcp__sellersprite-mcp__market_brand_concentration`
- `mcp__sellersprite-mcp__market_product_concentration`
- `mcp__sellersprite-mcp__market_seller_concentration`
- `mcp__sellersprite-mcp__market_seller_type_concentration`
- `mcp__sellersprite-mcp__market_seller_country_distribution`
- `mcp__sellersprite-mcp__market_price_distribution`
- `mcp__sellersprite-mcp__market_listing_date_distribution`
- `mcp__sellersprite-mcp__market_listing_trend_distribution`
- `mcp__sellersprite-mcp__market_ebc_distribution`
- `mcp__sellersprite-mcp__market_product_demand_trend`

### 关键词

- `mcp__sellersprite-mcp__keyword_miner`
- `mcp__sellersprite-mcp__keyword_research`
- `mcp__sellersprite-mcp__keyword_research_trends`
- `mcp__sellersprite-mcp__keyword_conversion`
- `mcp__sellersprite-mcp__keyword_order`

### 流量

- `mcp__sellersprite-mcp__traffic_extend`
- `mcp__sellersprite-mcp__traffic_keyword`
- `mcp__sellersprite-mcp__traffic_keyword_stat`
- `mcp__sellersprite-mcp__traffic_listing`
- `mcp__sellersprite-mcp__traffic_listing_stat`
- `mcp__sellersprite-mcp__traffic_source`

### 商标

- `mcp__sellersprite-mcp__trademark_country_list`
- `mcp__sellersprite-mcp__trademark_list`
- `mcp__sellersprite-mcp__trademark_detail`
- `mcp__sellersprite-mcp__trademark_stats`

### 站外趋势

- `mcp__sellersprite-mcp__google_trend`

当前导出共 43 个工具。不要在 Skill 中写死“45 个工具”，也不要调用不存在的配额工具；若需要额度信息，先确认 MCP 实际提供了相应工具，否则只说明不可见。

## 按问题路由

| 目标 | 首选工具 | 追加工具 |
|---|---|---|
| 找类目节点 | `product_node` | `product_research` |
| 市场规模和月度趋势 | `market_research_statistics`、`market_research` | 分布、集中度、需求趋势 |
| 候选商品 | `product_research`、`competitor_lookup` | `bsr_prediction` |
| 单 ASIN 快速判断 | `asin_detail`、`keepa_info` | `asin_sales_trend`、少量 `review` |
| 单 ASIN 完整深挖 | `asin_detail`、`asin_sales_trend`、`keepa_info` | `asin_prediction`、`asin_competitor`、优惠趋势 |
| VOC | `review` | 按星级/时间/类型分层抽样后再聚合 |
| 关键词机会 | `keyword_miner`、`keyword_research` | trends、conversion、ABA、Google Trends |
| ASIN 流量健康 | `traffic_keyword_stat`、`traffic_listing_stat` | `traffic_source`、`traffic_keyword`、`traffic_extend` |
| 关键词反查 | `keyword_order`、`traffic_keyword` | `traffic_extend` |
| 关联竞品 | `traffic_listing`、`asin_competitor` | `competitor_lookup` |
| 品牌合规 | `trademark_country_list`、`trademark_list` | `trademark_detail`、`trademark_stats` |

## 参数铁律

- 复杂列表/市场查询通常使用 `{ "request": { ... } }`；ASIN 单查和统计工具常为平铺参数，必须按实时 schema 传递。
- 通用字段通常包括 `marketplace`、`month`、`nodeIdPath`、`returnFields`、分页和排序，但不是每个工具都相同。
- `variation` 当前语义为 `Y=排除变体`、`N=包含变体`；未明确时使用 `Y`。
- 市场工具通常使用 `topN`；`market_research` 的头部数量字段可能是 `topNum`。
- `aba_research_weekly` 的 `date` 使用 `yyyyMMdd` 且必须是当周周六；`keyword_order` 的周模式同样需要周六，月模式使用 `yyyyMM`。
- `keepa_info` 和 `review` 的时间筛选若 schema 使用时间戳，按毫秒处理；展示时同时保留可读日期。
- `competitor_lookup` 的 `asins` 最多 40 个；`traffic_extend` 的 `asinList` 最多 20 个，超过上限拆批。
- `returnFields` 用于压缩响应，但必须保留支撑当前结论的字段。
- 所有列表工具根据 `total`、页数、`hasNextPage` 或实际结果判断是否继续分页；不得只看第一页就宣称全量。

## 证据记录格式

每次关键调用至少记录：

```text
tool: 实际 MCP 工具 ID
scope: marketplace / nodeIdPath / asin / keyword
business_month: 报告中的业务月份
request_month: 实际传给 MCP 的月份
filters: 关键筛选、variation、topN/topNum、分页
fields: returnFields 或实际使用字段
sample: 数量、是否 TopN、是否抽样
quality: complete / partial / empty / conflict
```

密钥、请求头和完整敏感配置只写脱敏占位符。
