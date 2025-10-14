package com.merhawi.urlshortener.utils

import jakarta.annotation.PostConstruct
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.time.Duration
import java.math.BigInteger

/**
 * HMAC + Base62 short code generator with Redis reservation fallback.
 *
 * - Deterministic per original URL (same input → same code)
 * - Very low collision probability (96 bits )
 * - Uses Redis for temporary reservation to avoid concurrent duplicates
 */
@Component
class RedisShortCodeGenerator(
    private val redisTemplate: StringRedisTemplate
) {
    // === Configuration ===
    private val bitLength = 48 // ≈17 base62 chars
    private val reservationTtlMs = 10_000L // Redis reservation TTL
    private val base62Alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val base = base62Alphabet.length

    // Secret key for HMAC
    private val secretKey: ByteArray = System.getenv("SHORTCODE_HMAC_KEY")?.toByteArray()
        ?: SecureRandom().run {
            ByteArray(32).also { nextBytes(it) }
        }

    private val macAlgorithm = "HmacSHA256"
    private lateinit var macPrototype: Mac

    @PostConstruct
    fun init() {
        val keySpec = SecretKeySpec(secretKey, macAlgorithm)
        macPrototype = Mac.getInstance(macAlgorithm)
        macPrototype.init(keySpec)
    }

    /**
     * Generate and temporarily reserve a short code for the given URL.
     * Ensures no collision by reserving in Redis before returning.
     */
    fun generateAndReserve(originalUrl: String): String {
        val digest = hmacSha256(originalUrl)
        val bytesNeeded = (bitLength + 7) / 8
        val maxWindowStart = digest.size - bytesNeeded

        // Try deterministic slices of digest
        for (start in 0..maxWindowStart) {
            val slice = digest.copyOfRange(start, start + bytesNeeded)
            val code = base62FromBytes(slice, bitLength)
            if (tryReserve(code)) return code
        }

        // Fallback: counter-based HMAC
        for (counter in 1..1000) {
            val digest2 = hmacSha256("$originalUrl::$counter")
            val slice = digest2.copyOfRange(0, bytesNeeded)
            val code = base62FromBytes(slice, bitLength)
            if (tryReserve(code)) return code
        }

        throw IllegalStateException("Failed to generate unique short code after multiple attempts")
    }

    private fun tryReserve(code: String): Boolean {
        val key = "shortcode:$code"
        val success = redisTemplate.opsForValue().setIfAbsent(key, "RESERVED")
        if (success == true) {
            redisTemplate.expire(key, Duration.ofMillis(reservationTtlMs))
            return true
        }
        return false
    }

    private fun hmacSha256(data: String): ByteArray {
        val mac = macPrototype.clone() as Mac // thread-safe clone
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    private fun base62FromBytes(bytes: ByteArray, bits: Int): String {
        val totalBytes = (bits + 7) / 8
        val buffer = ByteBuffer.allocate(totalBytes + 1)
        buffer.put(0)
        buffer.put(bytes.copyOf(totalBytes))
        val mag = buffer.array()

        var value = BigInteger(mag)
        val shift = (totalBytes * 8) - bits
        if (shift > 0) value = value.shiftRight(shift)

        val sb = StringBuilder()
        if (value == BigInteger.ZERO) sb.append(base62Alphabet[0])
        while (value > BigInteger.ZERO) {
            val (div, rem) = value.divideAndRemainder(BigInteger.valueOf(base.toLong()))
            sb.append(base62Alphabet[rem.toInt()])
            value = div
        }
        return sb.reverse().toString()
    }
}

