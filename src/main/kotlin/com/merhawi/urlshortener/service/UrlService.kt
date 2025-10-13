package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.Repository.UrlRepository
import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.mapper.toDto
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.utils.InMemoryShortCodeGenerator
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UrlService(private val repo: UrlRepository,
                 private val redisShortCodeGenerator: RedisShortCodeGenerator) {

    private val logger = LoggerFactory.getLogger(UrlService::class.java)

    /**
     * Creates a short URL  and saves it to the database.
     */
    @Transactional
    fun createShortUrl(request: ShortenRequest): UrlDto {
        val shortCodeWithDB = ShortCodeGenerator. iDgenerate(repo)
        val shortcodeWithInMemory = InMemoryShortCodeGenerator.generateUniqueCode(repo)
        val shortCodeWithRedis = redisShortCodeGenerator.generateAndReserve(request.originalUrl)
        val url = Url(
            originalUrl = request.originalUrl,
            shortCode = shortCodeWithRedis
        )
        return repo.save(url).toDto()
    }

    @Cacheable(value = ["shortUrlCache"], key = "#shortCode", unless = "#result == null")
    fun getOriginalUrlEntity(shortCode: String): Url? =
        repo.findByShortCode(shortCode)

    fun getOriginalUrl(shortCode: String): UrlDto? =
        getOriginalUrlEntity(shortCode)?.let { it.toDto() }

       fun invalidateCache(shortCode: String) {
        logger.info("Cache invalidated for short code: $shortCode")
    }
}
