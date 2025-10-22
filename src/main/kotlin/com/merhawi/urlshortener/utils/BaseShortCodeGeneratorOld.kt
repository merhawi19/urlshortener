package com.merhawi.urlshortener.utils

import java.security.SecureRandom

/**
 * Abstract base for all short code generators.
 * Provides a common alphabet and random code generator.
 */
abstract class BaseShortCodeGeneratorOld<T>() {

    companion object {
        const val BASE_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DEFAULT_LENGTH = 7
        private val RANDOM = SecureRandom()
        private val ALPHABET_SIZE = BASE_ALPHABET.length

        fun generate(length: Int = DEFAULT_LENGTH): String {
            val bytes = ByteArray(length)
            RANDOM.nextBytes(bytes)
            val result = CharArray(length)
            for (i in bytes.indices) {
                result[i] = BASE_ALPHABET[(bytes[i].toInt() and 0xFF) % ALPHABET_SIZE]
            }
            return String(result)
        }
    }

    abstract fun generateUniqueCode(source: T, maxAttempts: Int = 100): String
}