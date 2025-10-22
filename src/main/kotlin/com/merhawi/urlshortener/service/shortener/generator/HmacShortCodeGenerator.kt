package com.merhawi.urlshortener.service.shortener.generator

import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.service.shortener.ReservationService
import com.merhawi.urlshortener.utils.Base62
import org.springframework.stereotype.Component
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

/**
 * Deterministic, HMAC-based short code generator.
 * Fully configurable via ShortCodeProperties.
 */
@Component
class HmacShortCodeGenerator(
    private val reservationService: ReservationService,
    private val props: ShortCodeProperties

) : BaseShortCodeGenerator<String>(props.alphabet, props.bitLength) {

    //private val macAlgorithm = "HmacSHA256"
    private val macAlgorithm = props.macAlgorithm
    private val bitLength get() = props.bitLength

    // Uses environment key if provided, else generates random key for local
    private val secretKey: ByteArray = System.getenv("SHORTCODE_HMAC_KEY")?.toByteArray()
        ?: SecureRandom().run { ByteArray(32).also { nextBytes(it) } }

    private val macPrototype: Mac = Mac.getInstance(macAlgorithm).apply {
        init(SecretKeySpec(secretKey, macAlgorithm))
    }

    override fun generateUniqueCode(source: String, maxAttempts: Int): String {
        val digest = hmacSha256(source)
        val bytesNeeded = (bitLength + 7) / 8
        val maxWindowStart = digest.size - bytesNeeded

        // Deterministic slice-based generation
        for (start in 0..maxWindowStart) {
            val slice = digest.copyOfRange(start, start + bytesNeeded)
            val code = Base62.encode(slice, bitLength, props.alphabet)
            if (reservationService.tryReserve(code))  return code
        }

        // Fallback if slice windows collide
        for (counter in 1..maxAttempts) {
            val digest2 = hmacSha256("$source::$counter")
            val slice = digest2.copyOfRange(0, bytesNeeded)
            val code = Base62.encode(slice, bitLength, props.alphabet)
            if (reservationService.tryReserve(code))  return code
        }

        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }

    private fun hmacSha256(data: String): ByteArray {
        val mac = macPrototype.clone() as Mac // thread-safe clone
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
    }
}
