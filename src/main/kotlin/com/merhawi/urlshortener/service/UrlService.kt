package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.exception.UrlShorteningException
import com.merhawi.urlshortener.mapper.toDto
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.service.shortener.ShortCodeService
import com.merhawi.urlshortener.utils.InMemoryShortCodeGenerator
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import com.merhawi.urlshortener.utils.ShortCodeGenerator
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UrlService(private val repo: UrlRepository,
                 private val redisShortCodeGenerator: RedisShortCodeGenerator,
                 private val shortCodeService: ShortCodeService
) {

    private val logger = LoggerFactory.getLogger(UrlService::class.java)

    /**
     * Creates a short URL  and saves it to the database.
     */
    @Transactional
    fun createShortUrl(request: ShortenRequest): UrlDto {
         try {

                    print("hello : short code generetion")
                    val shortCode = shortCodeService.generateUniqueShortCode(request.originalUrl)
                    val url = Url(
                        originalUrl = request.originalUrl,
                        shortCode = shortCode
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

    fun existsByShortCode(shortCode: String): Boolean =
        repo.findByShortCode(shortCode) != null
}
