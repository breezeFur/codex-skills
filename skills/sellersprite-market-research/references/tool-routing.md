# 43 个真实 SellerSprite MCP 工具路由与参数规范 (Tool Routing)

> 依据真实 MCP 工具描述导出，客户端前缀通常为 `mcp__sellersprite-mcp__<tool>` 或直接 `<tool>`。

---

## 一、 真实工具名称映射清单 (全 43 个)

### 1. ABA 关键词研究 (3 个)
- `aba_research_monthly`：按月发现热门/异动/增长/潜力关键词（`request` 包装，`date: yyyyMM`）。
- `aba_research_weekly`：按周发现关键词（`request` 包装，`date` 格式为 `yyyyMMdd` 且必须为**当周周六**）。
- `aba_research_trend`：单个关键词 ABA 排名与搜索量趋势（平铺参数：`marketplace`, `keyword`, `timeGranularity`）。

### 2. ASIN 深度分析 (6 个，平铺参数)
- `asin_detail`：查询单个 ASIN 完整详情（类目、价格、评分、留评数、LQS、变体）。
- `asin_detail_with_coupon_trend`：商品详情 + 优惠券历史成交价趋势。
- `asin_coupon_trend`：优惠券历史明细（原价、优惠类型、最终成交价）。
- `asin_competitor`：竞品 ASIN 反查。
- `asin_prediction`：近 14 个月历史销量与日/月销量预测。
- `asin_sales_trend`：ASIN 月度历史销售趋势（父子体销量、历史均价）。

### 3. Keepa 数据 (1 个，平铺参数)
- `keepa_info`：**优先使用**。完整 Keepa 历史趋势（BSR、售价、BuyBox 价格、卖家数、评论数、评分时间序列，支持 `startTimestamp`/`endTimestamp` 与 `dailyLatest: true`）。

### 4. 商品 / 类目 / 竞品检索 (5 个)
- `product_node`：类目节点搜索与定位（`request` 包装，根据关键词或 nodeId 获取 `nodeIdPath`）。
- `product_research`：爆品/候选高级筛选（`request` 包装，支持价格、销量、BSR、毛利、变体过滤）。
- `competitor_lookup`：按 ASIN 列表批量查询竞品数据（`request` 包装，`asins` 数组上限 40 个）。
- `bsr_prediction`：大类 BSR 预测日销量与近 30 天销量（平铺参数：`marketplace`, `bsr`, `categoryId`）。
- `review`：买家评价列表采集（平铺参数：`marketplace`, `asin`, `starList: [1, 2, 3]` 查差评，`starList: [4, 5]` 查好评，`typeList` 筛选类型）。

### 5. 市场（类目节点）分析 (12 个，全部 request 包装)
- `market_research`：全类目细分市场筛选（商品数、品牌数、自营占比，注意：头部数量字段为 **`topNum`**）。
- `market_research_statistics`：类目结构与机会分析（头部字段为 **`topN`**，`newProduct` 新品定义月数）。
- `market_brand_concentration`：品牌集中度分析（CR3/CR10 品牌份额）。
- `market_product_concentration`：商品集中度分析（头部 Listing 销量/销售额份额，支持 `asins` 过滤）。
- `market_seller_concentration`：卖家集中度分析。
- `market_seller_type_concentration`：卖家发货类型分布（Amazon 自营 / FBA / FBM）。
- `market_seller_country_distribution`：卖家所属地分布（中国/美国等卖家占比）。
- `market_price_distribution`：价格区间分布与销量/评分分布。
- `market_listing_date_distribution`：距今上架时间分布与新品接受度。
- `market_listing_trend_distribution`：绝对上架时间分布与产品生命周期。
- `market_ebc_distribution`：A+ 页面与视频配置普及率。
- `market_product_demand_trend`：类目需求趋势（退货率 `refundRate`、同类目退货率 `cateRefundRate`、搜索购买比）。

### 6. 关键词工具 (5 个)
- `keyword_miner`：高级关键词决策分析（`request` 包装，供需比、SPR、PPC 竞价、点击集中度）。
- `keyword_research`：关键词选品与市场分析（`request` 包装，搜索量、购买量、增长趋势）。
- `keyword_research_trends`：单个关键词趋势数据（平铺参数：`marketplace`, `keyword`, `month`）。
- `keyword_conversion`：关键词转化率分析（`request` 包装，`timeType: "WEEK" / "90D"`）。
- `keyword_order`：基于 ASIN 的出单词反查（`request` 包装，`asins`, `reverseType: "W"/"M"`, `date: yyyyMMdd(周六) / yyyyMM`）。

### 7. 流量分析 (6 个)
- `traffic_extend`：批量拓词（`request` 包装，`asinList` 上限 20 个）。
- `traffic_keyword`：指定 ASIN 流量词反查（`request` 包装，`asin`, `month`, `badges`, `trafficKeywordTypes`）。
- `traffic_keyword_stat`：ASIN 流量关键词结构概览统计（平铺参数：`marketplace`, `asin`, `month`）。
- `traffic_listing`：站内关联竞品列表（`request` 包装，`asinList`, `relations`）。
- `traffic_listing_stat`：ASIN 流量来源结构分析（免费/付费+关联类型，平铺参数：`marketplace`, `asin`, `month`）。
- `traffic_source`：流量关键词来源结构分析（`request` 包装，`q: "ASIN或关键词"`, `month`）。

### 8. 商标查询 (4 个)
- `trademark_country_list`：支持商标的国家列表（无参数）。
- `trademark_list`：商标列表查询（`request` 包装，`text`, `niceClass`, `status`）。
- `trademark_detail`：商标详情（平铺参数：`office`, `brandId`）。
- `trademark_stats`：商标统计数据（`request` 包装，`office`, `text`）。

### 9. 站外趋势 (1 个)
- `google_trend`：Google Trends 站外搜索热度（`request` 包装，`marketplace`, `keyword`, `googleProp`, `monthly`）。

---

## 二、 核心参数避坑规则 (Critical Caveats)

1. **变体反转语义 (`variation`)**：
   - `variation: "Y"` = **排除变体（只查独立主 Listing）**。
   - `variation: "N"` = 包含变体子体。
   - **大盘统计与候选筛选必须传 `variation: "Y"`**。
2. **入参结构区分**：
   - 列表/市场类工具（`product_research`, `market_*`, `keyword_miner`, `traffic_keyword` 等）：入参必须包裹在 `{ "request": { ... } }` 中。
   - 单 ASIN / 详情工具（`asin_detail`, `keepa_info`, `asin_sales_trend`, `review`, `traffic_keyword_stat` 等）：入参为顶层平铺 `{ "marketplace": "US", "asin": "..." }`。
3. **月份与日期格式**：
   - 月度工具：`yyyyMM`（如 `202607`）。
   - 周度工具（`aba_research_weekly`、`keyword_order` 周模式）：`yyyyMMdd` 且必须为 **当周周六**。
4. **头部数量字段名称差异**：
   - `market_research`（全类目）使用 **`topNum`**；
   - 其余节点级市场工具使用 **`topN`**。
5. **评论查询参数格式 (`review`)**：
   - 必须使用整型数组 `starList: [1, 2, 3]`（差评）或 `starList: [4, 5]`（好评），严禁使用逗号拼接字符串。