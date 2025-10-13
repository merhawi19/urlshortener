package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.Repository.UrlRepository
import java.security.SecureRandom


object ShortCodeGenerator {

    private const val BASE_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val RANDOM = SecureRandom()

    private const val DEFAULT_LENGTH = 7
    private const val MAX_ATTEMPTS = 100
    private const val LENGTH_INCREMENT_STEP = 20

    private val alphabetSize = BASE_ALPHABET.length

    fun generate(length: Int = DEFAULT_LENGTH): String {
        val bytes = ByteArray(length)
        RANDOM.nextBytes(bytes)
        val result = CharArray(length)
        for (i in bytes.indices) {
            // Mask to positive range & map to alphabet
            result[i] = BASE_ALPHABET[(bytes[i].toInt() and 0xFF) % alphabetSize]
        }
        return String(result)
    }

    fun iDgenerate(repo: UrlRepository, maxAttempts: Int = MAX_ATTEMPTS): String {
        repeat(maxAttempts) { attempt ->
            val codeLength = DEFAULT_LENGTH + (attempt / LENGTH_INCREMENT_STEP)
            val code = generate(codeLength)
            if (repo.findByShortCode(code) == null) {
                return code
            }
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}