package com.merhawi.urlshortener.utils

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RedisShortCodeGeneratorTest {

    private lateinit var redisTemplate: StringRedisTemplate
    private lateinit var valueOps: ValueOperations<String, String>
    private lateinit var generator: RedisShortCodeGenerator

    @BeforeEach
    fun setup() {
        redisTemplate = mock()
        valueOps = mock()
        whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
        generator = RedisShortCodeGenerator(redisTemplate)
        generator.init()
    }

    @Test
    fun `should generate and reserve unique short code`() {
        whenever(valueOps.setIfAbsent(any(), any())).thenReturn(true)
        val code = generator.generateAndReserve("https://example.com/test")

        assertNotNull(code)

        verify(valueOps, atLeastOnce()).setIfAbsent(
            argThat { this.startsWith("shortcode:") },
            eq("RESERVED")
        )
    }


    @Test
    fun `should try multiple slices before success`() {
        // simulate first few reservations fail
        whenever(valueOps.setIfAbsent(any(), any()))
            .thenReturn(false, false, true)
        val code = generator.generateAndReserve("https://example.com/multi")
        assertNotNull(code)
    }

    @Test
    fun `should throw if all attempts exhausted`() {
        whenever(valueOps.setIfAbsent(any(), any())).thenReturn(false)
        assertFailsWith<IllegalStateException> {
            generator.generateAndReserve("https://example.com/fail")
        }
    }
}
