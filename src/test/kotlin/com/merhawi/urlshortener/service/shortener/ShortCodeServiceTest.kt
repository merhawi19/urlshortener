package com.merhawi.urlshortener.service.shortener

import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.service.shortener.generator.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class ShortCodeServiceTest {

    private lateinit var urlRepository: UrlRepository
    private lateinit var props: ShortCodeProperties
    private lateinit var generatorFactory: ShortCodeGeneratorFactory

    @BeforeEach
    fun setup() {
        urlRepository = mock()
        props = ShortCodeProperties().apply {
            alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            defaultLength = 7
            lengthIncrementStep = 20
            bitLength = 48
            macAlgorithm = "HmacSHA256"
            maxAttempts = 5
        }
        generatorFactory = mock()
    }

    @Test
    fun `should use HmacShortCodeGenerator when returned from factory`() {
        val hmacGenerator: HmacShortCodeGenerator = mock()
        whenever(generatorFactory.getGenerator()).thenReturn(hmacGenerator)
        whenever(hmacGenerator.generateUniqueCode(any(), any())).thenReturn("HMAC123")

        val service = ShortCodeService(urlRepository, props, generatorFactory)
        val code = service.generateUniqueShortCode("https://test.com")

        assertEquals("HMAC123", code)
        verify(hmacGenerator).generateUniqueCode(eq("https://test.com"), eq(props.maxAttempts))
    }

    @Test
    fun `should use InMemoryShortCodeGenerator when returned from factory`() {
        val inMemoryGenerator: InMemoryShortCodeGenerator = mock()
        whenever(generatorFactory.getGenerator()).thenReturn(inMemoryGenerator)
        whenever(inMemoryGenerator.generateUniqueCode(any(), any())).thenReturn("LOCAL123")

        val service = ShortCodeService(urlRepository, props, generatorFactory)
        val code = service.generateUniqueShortCode("https://local.com")

        assertEquals("LOCAL123", code)
        verify(inMemoryGenerator).generateUniqueCode(any(), eq(props.maxAttempts))
    }

    @Test
    fun `should use SimpleShortCodeGenerator when returned from factory`() {
        val simpleGenerator: SimpleShortCodeGenerator = mock()
        whenever(generatorFactory.getGenerator()).thenReturn(simpleGenerator)
        whenever(simpleGenerator.generateUniqueCode(any(), any())).thenReturn("DB123")

        val service = ShortCodeService(urlRepository, props, generatorFactory)
        val code = service.generateUniqueShortCode("https://db.com")

        assertEquals("DB123", code)
        verify(simpleGenerator).generateUniqueCode(eq(urlRepository), eq(props.maxAttempts))
    }

    @Test
    fun `should throw for unsupported generator type`() {
        val unsupported: BaseShortCodeGenerator<Any> = mock()
        whenever(generatorFactory.getGenerator()).thenReturn(unsupported)

        val service = ShortCodeService(urlRepository, props, generatorFactory)

        assertThrows<IllegalArgumentException> {
            service.generateUniqueShortCode("https://unsupported.com")
        }
    }
}
