package com.merhawi.urlshortener.utils

import com.github.benmanes.caffeine.cache.Caffeine
import com.merhawi.urlshortener.repository.UrlRepository
import java.time.Duration

object InMemoryShortCodeGenerator : BaseShortCodeGenerator() {

    private const val CACHE_TTL_MINUTES = 60L
    private const val CACHE_MAX_SIZE = 20_000

    private val localCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(CACHE_TTL_MINUTES))
        .maximumSize(CACHE_MAX_SIZE.toLong())
        .build<String, Boolean>()

    override fun generateUniqueCode(repo: UrlRepository, maxAttempts: Int): String {
        repeat(maxAttempts) { attempt ->
            val codeLength = 7 + (attempt / 20)
            val code = generate(codeLength)
            if (localCache.getIfPresent(code) != null) return@repeat

            if (repo.findByShortCode(code) == null) {
                localCache.put(code, true)
                return code
            } else {
                localCache.put(code, true)
            }
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}