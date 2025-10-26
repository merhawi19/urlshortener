package com.merhawi.urlshortener.service.shortener

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * Handles temporary Redis-based short code reservations.
 * Prevents cross-instance collisions during code generation.
 */
@Service
class ReservationService(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${shortener.reservation.prefix:shortcode}") private val prefix: String,
    @Value("\${shortener.reservation.ttl-ms:10000}") private val ttlMs: Long
) {

    /**
     * Attempts to reserve the given short code key in Redis.
     *
     * @return true if reservation succeeded (code is free),
     *         false if already reserved or used.
     */
    fun tryReserve(code: String): Boolean {
        val key = "$prefix:$code"
        val success = redisTemplate.opsForValue().setIfAbsent(key, "RESERVED")
        if (success == true) {
            redisTemplate.expire(key, Duration.ofMillis(ttlMs))
            return try {
                true
            } catch (e: Exception) {
                redisTemplate.delete(key)
                false
            }
        }
        return false
    }

    /**
     *  can be called to release a reservation
     */
    fun release(code: String) {
        redisTemplate.delete("$prefix:$code")
    }

    /**
     * Helper for tests or cache clearing.
     */
    fun isReserved(code: String): Boolean =
        redisTemplate.hasKey("$prefix:$code") == true
}
