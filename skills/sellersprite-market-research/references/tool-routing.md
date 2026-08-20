# SellerSprite MCP 工具路由

MCP 服务名：`sellersprite`。工具名称以当前项目 `SellerSpriteMcpToolNames` 为准；使用前仍须读取实时 schema。

## 工具分组

### 账户、产品和 ASIN

- `sellersprite_get_account_visits`
- `sellersprite_list_product_nodes`
- `sellersprite_research_products`
- `sellersprite_lookup_competitors`
- `sellersprite_get_asin_detail`
- `sellersprite_get_asin_sales_trend`
- `sellersprite_get_keepa_trend`
- `sellersprite_get_asin_coupon_trend`
- `sellersprite_get_asin_with_coupon_trend`
- `sellersprite_predict_asin_sales`
- `sellersprite_predict_bsr_sales`

### 关键词、ABA 和流量

- `sellersprite_research_keywords`
- `sellersprite_get_keyword_research_trends`
- `sellersprite_mine_keywords`
- `sellersprite_extend_traffic_keywords`
- `sellersprite_research_aba_weekly`
- `sellersprite_research_aba_monthly`
- `sellersprite_get_aba_keyword_trends`
- `sellersprite_get_google_trends`
- `sellersprite_reverse_order_keywords`
- `sellersprite_reverse_traffic_keywords`
- `sellersprite_list_related_traffic`
- `sellersprite_get_traffic_keyword_stats`
- `sellersprite_get_traffic_listing_stats`
- `sellersprite_get_traffic_sources`

### 市场、竞争和内容结构

- `sellersprite_research_markets`
- `sellersprite_get_market_statistics`
- `sellersprite_get_market_demand_trend`
- `sellersprite_get_market_shelf_time_distribution`
- `sellersprite_get_market_shelf_trend_distribution`
- `sellersprite_get_market_price_distribution`
- `sellersprite_get_market_ratings_distribution`
- `sellersprite_get_market_rating_distribution`
- `sellersprite_get_market_ebc_distribution`
- `sellersprite_get_market_goods_concentration`
- `sellersprite_get_market_brand_concentration`
- `sellersprite_get_market_seller_concentration`
- `sellersprite_get_market_seller_location_distribution`
- `sellersprite_get_market_seller_type_distribution`

### 评论、商标和 OCR

- `sellersprite_list_reviews`
- `sellersprite_get_trademark_range`
- `sellersprite_get_trademark_detail`
- `sellersprite_list_trademarks`
- `sellersprite_get_trademark_stats`
- `sellersprite_recognize_image_text`

## 推荐调用顺序

```text
quota
  -> product/category context
  -> market screening
  -> candidate ASIN gate
  -> selected-ASIN deep dive
  -> evidence synthesis
  -> final decision
```

同一请求中的所有市场工具应尽量保持 `marketplace`、`nodeIdPath`、月份、`topN` 和新品定义一致。多 ASIN 工具调用要显式记录 ASIN 列表，避免把不同样本混在一起。

## 参数注意事项

- MCP 工具传递的是 JSON；OCR 使用远程 URL 或 Base64，商标图片查询使用 Base64，不传浏览器 `MultipartFile`。
- 日期可能在不同工具 schema 中要求 `YYYY-MM` 或 `YYYYMM`；以当前工具 schema 为准，并在证据日志中同时记录业务月份和实际传参。
- 复杂请求通常是 `{ "request": { ... } }`；简单工具可能直接接收 `marketplace`、`asin` 等标量参数。不要把一种形状套用到所有工具。
- 分页请求必须根据响应的 `hasNextPage`、`pages`、`total` 和实际 item 数判断是否继续；不能只请求第一页后宣称完成。
