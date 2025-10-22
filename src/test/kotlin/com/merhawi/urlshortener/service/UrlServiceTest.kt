package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.exception.UrlShorteningException
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.dao.DataIntegrityViolationException

class UrlServiceTest {

    private lateinit var repo: UrlRepository
    private lateinit var redisShortCodeGenerator: RedisShortCodeGenerator
    private lateinit var service: UrlService

    @BeforeEach
    fun setup() {
        repo = Mockito.mock(UrlRepository::class.java)
        redisShortCodeGenerator = Mockito.mock(RedisShortCodeGenerator::class.java)
        service = UrlService(repo, redisShortCodeGenerator)
    }

    @Test
    fun `should create short url successfully`() {
        val request = ShortenRequest(originalUrl = "https://example.com")
        val savedUrl = Url(id = 1L, originalUrl = request.originalUrl, shortCode = "abc123")

        Mockito.`when`(repo.save(ArgumentMatchers.any(Url::class.java))).thenReturn(savedUrl)

        val result: UrlDto = service.createShortUrl(request)

        Assertions.assertEquals("https://example.com", result.originalUrl)
        Assertions.assertEquals("abc123", result.shortCode)
        Mockito.verify(repo, Mockito.times(1)).save(ArgumentMatchers.any(Url::class.java))
    }

    @Test
    fun `should throw UrlShorteningException on DataIntegrityViolation`() {
        val request = ShortenRequest(originalUrl = "https://duplicate.com")

        Mockito.`when`(repo.save(ArgumentMatchers.any(Url::class.java)))
            .thenThrow(DataIntegrityViolationException("duplicate"))

        Assertions.assertThrows(UrlShorteningException::class.java) {
            service.createShortUrl(request)
        }
    }

    @Test
    fun `should return UrlDto when shortCode exists`() {
        val url = Url(id = 1L, originalUrl = "https://example.com", shortCode = "abc123")
        Mockito.`when`(repo.findByShortCode("abc123")).thenReturn(url)

        val result = service.getOriginalUrl("abc123")

        Assertions.assertNotNull(result)
        Assertions.assertEquals("https://example.com", result?.originalUrl)
    }

    @Test
    fun `should return null when shortCode not found`() {
        Mockito.`when`(repo.findByShortCode("notfound")).thenReturn(null)
        val result = service.getOriginalUrl("notfound")
        Assertions.assertNull(result)
    }
}