package com.merhawi.urlshortener.utils

import com.github.benmanes.caffeine.cache.Caffeine
import com.merhawi.urlshortener.repository.UrlRepository
import java.time.Duration

/**
 * In-memory cache short code generator.
 * Prevents local collisions and checks repository for uniqueness.
 */
object InMemoryShortCodeGenerator : BaseShortCodeGeneratorOld<UrlRepository>() {


    private const val CACHE_TTL_MINUTES = 60L
    private const val CACHE_MAX_SIZE = 20_000
    private const val DEFAULT_LENGTH = 7
    private const val LENGTH_INCREMENT_STEP = 20

    private val localCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(CACHE_TTL_MINUTES))
        .maximumSize(CACHE_MAX_SIZE.toLong())
        .build<String, Boolean>()

    override fun generateUniqueCode(source: UrlRepository, maxAttempts: Int): String {
        repeat(maxAttempts) { attempt ->
            val codeLength = DEFAULT_LENGTH + (attempt / LENGTH_INCREMENT_STEP)
            val code = generate(codeLength)
            if (localCache.getIfPresent(code) != null) return@repeat
            if (source.findByShortCode(code) == null) {
                localCache.put(code, true)
                return code
            } else {
                localCache.put(code, true)
            }
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}