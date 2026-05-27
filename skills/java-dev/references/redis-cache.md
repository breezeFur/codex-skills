# Redis Cache

Use Redis for performance or coordination only when the task benefits from caching, rate limiting, token/session storage, or distributed state.

## Client Choice

- Use the project's existing Redis abstraction first.
- Use `StringRedisTemplate` for string or JSON payloads when no abstraction exists.
- Use `RedisTemplate<String, T>` only when typed serialization is already configured.

## Key Naming

Use stable, namespaced keys:

```text
<service-name>:<module>:<business>:<id>
<service-name>:<module>:list:<hash-or-version>
```

Prefer constants for key prefixes and TTL values.

## TTL Strategy

- Always set TTL for cache entries unless the data is intentionally persistent.
- Choose short TTLs for fast-changing data.
- Choose longer TTLs for reference data.
- Add jitter for high-traffic keys when cache stampede is a risk.

## Cache Patterns

- Cache-aside is the default: read cache, read database on miss, then write cache.
- Delete or refresh cache after writes.
- Use null markers or short TTL empty values to reduce cache penetration when appropriate.
- Treat Redis as best effort unless the feature is explicitly Redis-backed state.

## Serialization

- Store typed JSON for structured values.
- Do not store raw `Map` as a long-term business object.
- Use ObjectMapper or the existing serialization utility.
- On deserialization failure, delete the bad cache value and fall back to the source of truth.

## Error Handling

Cache failures should not break read APIs unless Redis is the source of truth for the feature. Log or swallow according to project style, then fall back to database or service computation.

