package com.merhawi.urlshortener.service.shortener.generator

import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.service.shortener.ReservationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*


class HmacShortCodeGeneratorTest {

    private lateinit var reservationService: ReservationService
    private lateinit var props: ShortCodeProperties
    private lateinit var generator: HmacShortCodeGenerator

    @BeforeEach
    fun setup() {
        reservationService = mock(ReservationService::class.java)
        props = ShortCodeProperties().apply {
            alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            defaultLength = 7
            lengthIncrementStep = 20
            bitLength = 48
            macAlgorithm = "HmacSHA256"
        }
        generator = HmacShortCodeGenerator(reservationService, props)
    }

    @Test
    fun `should generate unique code when reservation succeeds`() {
        `when`(reservationService.tryReserve(anyString())).thenReturn(true)

        val code = generator.generateUniqueCode("https://example.com", 5)

        assertNotNull(code)
        verify(reservationService, atLeastOnce()).tryReserve(anyString())
    }

    @Test
    fun `should throw exception after exceeding max attempts`() {
        `when`(reservationService.tryReserve(anyString())).thenReturn(false)

        assertThrows(IllegalStateException::class.java) {
            generator.generateUniqueCode("https://example.com", 2)
        }
    }
    @Test
    fun `should generate deterministic short code for same URL`() {
        `when`(reservationService.tryReserve(anyString())).thenReturn(true)

        val url = "https://example.org/test"
        val code1 = generator.generateUniqueCode(url, 5)
        val code2 = generator.generateUniqueCode(url, 5)

        assertEquals(code1, code2, "Same input should produce the same code")
    }

    @Test
    fun `should generate different short codes for different URLs`() {
        `when`(reservationService.tryReserve(anyString())).thenReturn(true)

        val code1 = generator.generateUniqueCode("https://example.org/a", 5)
        val code2 = generator.generateUniqueCode("https://example.org/b", 5)

        assertNotEquals(code1, code2, "Different URLs should produce different codes")
    }
    @Test
    fun `should throw after exhausting all retry attempts`() {
        `when`(reservationService.tryReserve(anyString())).thenReturn(false)

        val ex = assertThrows(IllegalStateException::class.java) {
            generator.generateUniqueCode("https://fail.me", 2)
        }

        assertTrue(ex.message!!.contains("Failed to generate"))
    }
}
