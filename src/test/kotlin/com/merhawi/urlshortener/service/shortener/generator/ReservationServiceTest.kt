package com.merhawi.urlshortener.service.shortener.generator

import com.merhawi.urlshortener.service.shortener.ReservationService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class ReservationServiceTest {

    private val redisTemplate = Mockito.mock(StringRedisTemplate::class.java)
    private val valueOps = Mockito.mock(ValueOperations::class.java) as ValueOperations<String, String>
    private val reservationTtl = 10L
    private val prefix ="shortcode"

    private val service = ReservationService(redisTemplate, prefix, reservationTtl)

    @Test
    fun `should reserve new code if not exists`() {
        Mockito.`when`(redisTemplate.opsForValue()).thenReturn(valueOps)
        Mockito.`when`(valueOps.setIfAbsent("shortcode:test", "RESERVED")).thenReturn(true)

        val result = service.tryReserve("test")

        Assertions.assertTrue(result)
        Mockito.verify(valueOps).setIfAbsent("shortcode:test", "RESERVED")
        Mockito.verify(redisTemplate).expire("shortcode:test", Duration.ofMillis(reservationTtl))
    }

    @Test
    fun `should not reserve if already exists`() {
        Mockito.`when`(redisTemplate.opsForValue()).thenReturn(valueOps)
        Mockito.`when`(valueOps.setIfAbsent("shortcode:test", "RESERVED")).thenReturn(false)

        val result = service.tryReserve("test")

        Assertions.assertFalse(result)
        Mockito.verify(redisTemplate, Mockito.never())
            .expire(ArgumentMatchers.anyString(), ArgumentMatchers.any(Duration::class.java))
    }
}