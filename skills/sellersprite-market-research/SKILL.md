---
name: sellersprite-market-research
description: 使用已配置的 SellerSprite MCP（43个原生工具）进行亚马逊市场调研与 AI 商业分析，覆盖阶段一市场初筛（7表）、关卡 Top20 双标杆甄选、阶段二单品与 VOC 深挖（5表）、阶段三 12 章终局决策报告。用户询问选品、类目研究、竞品分析、需求趋势、评论 VOC、关键词或 SellerSprite 市场报告时使用。
metadata:
  short-description: 卖家精灵亚马逊市场调研与深度选品决策专家 (43原子工具版)
---

# SellerSprite 亚马逊市场调研与深度选品决策 (Market Research & Product Selection)

本 Skill 基于真实的 43 个 SellerSprite MCP 工具进行端到端市场调研与选品决策。

---

## 核心避坑与调用铁律 (Critical Rules)

1. **工具命名对齐（无 `account_visits`）**：
   - MCP 工具名为 `product_research`、`product_node`、`asin_detail`、`keepa_info`、`review`、`market_research_statistics`、`market_product_demand_trend` 等（MCP 客户端会自动带上前缀 `mcp__sellersprite-mcp__`）。
   - **严禁调用不存在的 `get_account_visits` 或 `sellersprite_get_*` 伪工具**。
2. **入参结构区分（`request` 包装 vs 标量平铺）**：
   - **列表与市场类工具**：参数必须包裹在 `request` 对象中，例如：
     `{ "request": { "marketplace": "US", "nodeIdPath": "2619533011:...", "month": "202607", "variation": "Y" } }`
   - **单 ASIN 类工具**（`asin_detail`, `keepa_info`, `asin_sales_trend`, `asin_prediction`）：参数为顶层平铺格式：
     `{ "marketplace": "US", "asin": "B0FDKQGRCK" }`
3. **变体反转语义 (`variation`)**：
   - 在商品筛选和市场统计中：`variation: "Y"` 代表**排除变体子体（只看独立 Listing）**，`"N"` 代表包含所有子变体。
   - **原则**：大盘统计与 Top100 榜单**必须传 `variation: "Y"`**，严禁不传导致一个父体下的几十个子变体刷屏。
4. **日期格式**：
   - 统一为 `yyyyMM`（如 `202607`）。
5. **字段精简 (`returnFields`)**：
   - 强烈建议传入 `returnFields` 减少 token 消耗，如：`"asin,title,brand,price,totalUnits,totalAmount,rating,ratings,bsrRank,availableDate"`。

---

## 完整三阶段工作流 (Workflow)

```text
阶段一：市场初筛 (7张证据表) -> 关卡：Top20 双标杆挑选 (走量王 vs 高客单款) -> 阶段二：标杆深挖 (5张证据表) -> 阶段三：12 章破坏性决策报告
```

### 1. 阶段一：市场初筛 (Market Screening)

在同一站点、类目节点和业务月份下，采集 7 张证据表：

| 证据表 | 准确 MCP 工具名 | 核心参数与注意事项 | 业务分析核心 |
| :--- | :--- | :--- | :--- |
| **1. US / 候选商品** | `product_research` | `request: { marketplace, nodeIdPath, variation: "Y", order: { field: "total_units", desc: true }, size: 20 }` | 建立 Top20 候选池、价格带、留评率 |
| **2. 行业销售趋势** | `market_research_statistics` | `request: { marketplace, nodeIdPath, month: "202607", topN: 10, newProduct: 6 }` | 近 4~12 个月单品均销、销售额、均价上移 |
| **3. 行业需求及趋势** | `market_product_demand_trend` | `request: { marketplace, nodeIdPath, month: "202607" }` | 搜索购买比、退货率对比大盘均值 |
| **4. 细分市场现状** | `market_research` | `request: { marketplace, nodeIdPath, topNum: 10 }` | 商品总数、品牌数、自营占比、A+普及率 |
| **5. 细分市场退货率** | `market_product_demand_trend` | 读取其中的 `refundRate` / `cateRefundRate` | 退货率差值定级（高出大盘 3% 为高危） |
| **6. 竞品品牌** | `market_brand_concentration` | `request: { marketplace, nodeIdPath, month: "202607", topN: 10 }` | CR3/CR10 品牌份额，垄断度判断 |
| **7. 商品集中度** | `market_product_concentration` | `request: { marketplace, nodeIdPath, month: "202607", topN: 10 }` | Top10 商品销量份额，长尾空间 |

阶段一结束时，输出初筛结论与 **Top20 候选矩阵**，停下来进入【关卡】。

---

### 2. 关卡：Top20 双标杆挑选 (Selection Gate)

挑选 **2~3 个最具反差的标杆 ASIN** 进入阶段二：
1. **标杆 A (走量王 / 基础款代表)**：如 $20~$28，BSR #1~3，销量最大，验证主流配置与价格底线。
2. **标杆 B (高客单款 / 智能升级代表)**：如 $50~$70，月销过万，验证用户对升级功能的付费天花板。
3. **标杆 C (差异化黑马，可选)**：上架 6 个月内放量的新品。

---

### 3. 阶段二：标杆深挖与 VOC (Deep Dive)

针对选中的 ASIN 采集 5 张证据表：

| 证据表 | 准确 MCP 工具名 | 核心入参规范 | 业务分析核心要点 |
| :--- | :--- | :--- | :--- |
| **8. 评价列表** | `review` | 平铺参数：`{ marketplace, asin, starList: [1, 2, 3], size: 20 }`（查差评）；`starList: [4, 5]`（查好评） | 买家第一手原声：故障部位、材质抱怨、包装吐槽 |
| **9. VOC 痛点画像** | 基于 `review` 聚类 | 统计差评类型、发生频次、严重度 | 提炼 Defect-to-Roadmap 产品改良路线图 |
| **10. Keywords** | `traffic_keyword` + `keyword_miner` | `traffic_keyword` (`request: { marketplace, asin, month }`) + `keyword_miner` (`request: { marketplace, keyword }`) | 流量词自然排名 vs 广告排名、PPC 建议竞价、进店主流量词 |
| **11. ASIN销售趋势**| `asin_sales_trend` / `asin_prediction` | 平铺参数：`{ marketplace, asin }` | 月度销量走势、放量节点、断货与稳定性 |
| **12. ASIN运营趋势**| `keepa_info` | 平铺参数：`{ marketplace, asin, dailyLatest: true }` | 历史价格走势、BSR 波动、促销打折依赖度 |

---

### 4. 阶段三：12 章破坏性决策报告输出 (Final Analysis)

严格按照 `references/report-template.md` 规范生成：
1. **第一章 · 核心决策结论 (执行摘要先行)**：明确给出【强烈推荐进入 / 谨慎观察 / 坚决放弃】+ 五维评分雷达 + 核心破局点。
2. **第二章 · 调研范围与数据质量自检**（诚实列出数据缺口与进入前人工补验清单）。
3. **第三章 · 行业销售大盘与趋势**（含 Mermaid 折线图）。
4. **第四章 · 需求端与关键词证据**（大词搜索量、年同比增速、购买率）。
5. **第五章 · 细分市场现状与价格带分布**（价格带柱状图）。
6. **第六章 · 竞争格局与品牌集中度**（CR3/CR10、自营占比）。
7. **第七章 · 对标深挖 A：走量王爆品解剖**（销量、流量、Keepa 价格历史、优缺点）。
8. **第八章 · 对标深挖 B：高客单标杆解剖**（放量逻辑、致命差评聚类）。
9. **第九章 · 跨标杆 VOC 汇总与未满足诉求**（需求层级、对手差评即我方路线图）。
10. **第十章 · 进入策略建议**（目标定价、产品形态、Slogan、主攻与回避词）。
11. **第十一章 · 风险清单与应对方案**（退货率、广告通胀、供应链与售后）。
12. **第十二章 · 终局决策矩阵与进入前必验清单**。