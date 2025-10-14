package com.merhawi.urlshortener.service


import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.exception.UrlShorteningException
import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.dao.DataIntegrityViolationException


class UrlServiceTest {

    private lateinit var repo: UrlRepository
    private lateinit var redisShortCodeGenerator: RedisShortCodeGenerator
    private lateinit var service: UrlService

    @BeforeEach
    fun setup() {
        repo = mock(UrlRepository::class.java)
        redisShortCodeGenerator = mock(RedisShortCodeGenerator::class.java)
        service = UrlService(repo, redisShortCodeGenerator)
    }

    @Test
    fun `should create short url successfully`() {
        val request = ShortenRequest(originalUrl = "https://example.com")
        val savedUrl = Url(id = 1L, originalUrl = request.originalUrl, shortCode = "abc123")

        `when`(repo.save(any(Url::class.java))).thenReturn(savedUrl)

        val result: UrlDto = service.createShortUrl(request)

        assertEquals("https://example.com", result.originalUrl)
        assertEquals("abc123", result.shortCode)
        verify(repo, times(1)).save(any(Url::class.java))
    }

    @Test
    fun `should throw UrlShorteningException on DataIntegrityViolation`() {
        val request = ShortenRequest(originalUrl = "https://duplicate.com")

        `when`(repo.save(any(Url::class.java)))
            .thenThrow(DataIntegrityViolationException("duplicate"))

        assertThrows(UrlShorteningException::class.java) {
            service.createShortUrl(request)
        }
    }

    @Test
    fun `should return UrlDto when shortCode exists`() {
        val url = Url(id = 1L, originalUrl = "https://example.com", shortCode = "abc123")
        `when`(repo.findByShortCode("abc123")).thenReturn(url)

        val result = service.getOriginalUrl("abc123")

        assertNotNull(result)
        assertEquals("https://example.com", result?.originalUrl)
    }

    @Test
    fun `should return null when shortCode not found`() {
        `when`(repo.findByShortCode("notfound")).thenReturn(null)
        val result = service.getOriginalUrl("notfound")
        assertNull(result)
    }
}
