package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.Repository.UrlRepository
import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.mapper.toDto
import com.merhawi.urlshortener.model.Url
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UrlService(private val repo: UrlRepository, private val urlShortenService: UrlShortenService) {

    private val logger = LoggerFactory.getLogger(UrlService::class.java)

    /**
     * Creates a short URL  and saves it to the database.
     */
    @Transactional
    fun createShortUrl(request: ShortenRequest): UrlDto {
           val shortCode = urlShortenService. iDgenerate()
        val url = Url(
            originalUrl = request.originalUrl,
            shortCode = shortCode
        )
        return repo.save(url).toDto()
    }

        fun getOriginalUrl(shortCode: String): UrlDto? {
            print("getOriginalUrl :"+shortCode)
        return repo.findByShortCode(shortCode)?.toDto()
    }

       fun invalidateCache(shortCode: String) {
        logger.info("Cache invalidated for short code: $shortCode")
    }
}
