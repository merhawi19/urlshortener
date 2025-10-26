package com.merhawi.urlshortener.service.shortener.generator

import com.merhawi.urlshortener.config.ShortCodeProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy

class InMemoryShortCodeGeneratorTest {

    private lateinit var props: ShortCodeProperties
    private lateinit var generator: InMemoryShortCodeGenerator

    @BeforeEach
    fun setup() {
        props = ShortCodeProperties().apply {
            alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            defaultLength = 7
            lengthIncrementStep = 20
            cacheTtlMinutes = 60
            cacheMaxSize = 1000
            maxAttempts = 5
        }
        generator = InMemoryShortCodeGenerator(props)
    }

    @Test
    fun `should generate code of correct length`() {
        val code = generator.generateUniqueCode(Unit, props.maxAttempts)
        assertEquals(props.defaultLength, code.length)
        assertTrue(code.all { it in props.alphabet })
    }
    @Test
    fun `should cache generated codes and avoid duplicates`() {
        val first = generator.generateUniqueCode(Unit, props.maxAttempts)
        val second = generator.generateUniqueCode(Unit, props.maxAttempts)
        assertNotEquals(first, second)
    }

    @Test
    fun `should retry on collision until maxAttempts reached`() {
        val generator = object : InMemoryShortCodeGenerator(props) {
            override fun randomBase62(length: Int): String = "DUPLICATE"
        }

        generator.generateUniqueCode(Unit, props.maxAttempts)

        assertThrows(IllegalStateException::class.java) {
            generator.generateUniqueCode(Unit, props.maxAttempts)
        }
    }




}
