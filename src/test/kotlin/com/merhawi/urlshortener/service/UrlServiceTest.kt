package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.exception.UrlShorteningException
import com.merhawi.urlshortener.mapper.toDto
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.service.shortener.ShortCodeService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.dao.DataIntegrityViolationException

class UrlServiceTest {

    private lateinit var repo: UrlRepository
    private lateinit var shortCodeService: ShortCodeService
    private lateinit var service: UrlService

    @BeforeEach
    fun setup() {
        repo = mock(UrlRepository::class.java)
        shortCodeService = mock(ShortCodeService::class.java)
        service = UrlService(repo, shortCodeService)
    }

    @Test
    fun `should create short url successfully`() {
        val request = ShortenRequest("https://test.com")
        val shortCode = "abc123"
        val savedUrl = Url(id = 1L, originalUrl = request.originalUrl, shortCode = shortCode)

        `when`(shortCodeService.generateUniqueShortCode(request.originalUrl)).thenReturn(shortCode)
        `when`(repo.save(any(Url::class.java))).thenReturn(savedUrl)

        val result = service.createShortUrl(request)

        assertEquals(shortCode, result.shortCode)
        assertEquals(request.originalUrl, result.originalUrl)
        verify(repo).save(any(Url::class.java))
    }

    @Test
    fun `should throw UrlShorteningException on DataIntegrityViolationException`() {
        val request = ShortenRequest("https://duplicate.com")

        `when`(shortCodeService.generateUniqueShortCode(anyString())).thenReturn("dup123")
        `when`(repo.save(any(Url::class.java))).thenThrow(DataIntegrityViolationException("duplicate"))

        assertThrows(UrlShorteningException::class.java) {
            service.createShortUrl(request)
        }
    }

    @Test
    fun `should return null when short code not found`() {
        `when`(repo.findByShortCode("xyz")).thenReturn(null)
        val result = service.getOriginalUrl("xyz")
        assertNull(result)
    }

    @Test
    fun `should return UrlDto when short code exists`() {
        val url = Url(id = 1L, originalUrl = "https://test.com", shortCode = "abc")
        `when`(repo.findByShortCode("abc")).thenReturn(url)

        val result = service.getOriginalUrl("abc")

        assertNotNull(result)
        assertEquals("https://test.com", result!!.originalUrl)
    }
}
