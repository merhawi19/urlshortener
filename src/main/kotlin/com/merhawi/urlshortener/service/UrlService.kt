package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.exception.UrlShorteningException
import com.merhawi.urlshortener.mapper.toDto
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.utils.InMemoryShortCodeGenerator
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataIntegrityViolationException
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
 try {


            //val shortCodeWithDB = ShortCodeGenerator.iDgenerate(repo)  <--
             val shortcodeWithInMemory = InMemoryShortCodeGenerator.generateUniqueCode(repo)
            //val shortCodeWithRedis = redisShortCodeGenerator.generateAndReserve(request.originalUrl)
            val url = Url(
                originalUrl = request.originalUrl,
                shortCode = shortcodeWithInMemory
            )
            return repo.save(url).toDto()
      } catch (e: DataIntegrityViolationException) {
        throw UrlShorteningException("Failed to save short URL. It might be a duplicate.", e)
    } catch (e: Exception) {
        throw UrlShorteningException("Unexpected error occurred while saving short URL.", e)
    }

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
