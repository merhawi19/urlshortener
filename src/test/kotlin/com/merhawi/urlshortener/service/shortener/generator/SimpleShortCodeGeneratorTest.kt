package com.merhawi.urlshortener.service.shortener.generator

import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import com.merhawi.urlshortener.model.Url

class SimpleShortCodeGeneratorTest {

    private lateinit var props: ShortCodeProperties
    private lateinit var urlRepository: UrlRepository
    private lateinit var generator: SimpleShortCodeGenerator

    @BeforeEach
    fun setup() {
        props = ShortCodeProperties().apply {
            alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            defaultLength = 7
            lengthIncrementStep = 20
            maxAttempts = 5
        }
        urlRepository = mock()
        generator = SimpleShortCodeGenerator(props)
    }

    @Test
    fun `should generate code of default length`() {
        whenever(urlRepository.findByShortCode(any())).thenReturn(null)

        val code = generator.generateUniqueCode(urlRepository, props.maxAttempts)

        assertEquals(props.defaultLength, code.length)
        assertTrue(code.all { it in props.alphabet })
    }

    @Test
    fun `should increase code length after repeated collisions`() {
        var count = 0
        whenever(urlRepository.findByShortCode(any())).thenAnswer {
            count++
            if (count <= 20) mock<Url>() else null
        }

        val code = generator.generateUniqueCode(urlRepository, props.maxAttempts + 25)
        assertTrue(code.length >= props.defaultLength)
    }

    @Test
    fun `should throw after exceeding max attempts`() {
        whenever(urlRepository.findByShortCode(any())).thenReturn(mock())

        assertThrows<IllegalStateException> {
            generator.generateUniqueCode(urlRepository, 3)
        }
    }
}
