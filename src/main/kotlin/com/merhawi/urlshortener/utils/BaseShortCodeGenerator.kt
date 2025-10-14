package com.merhawi.urlshortener.utils


import com.merhawi.urlshortener.repository.UrlRepository
import java.security.SecureRandom

abstract class BaseShortCodeGenerator {

    companion object {
        private const val BASE_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DEFAULT_LENGTH = 7
        private const val LENGTH_INCREMENT_STEP = 20

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

    abstract fun generateUniqueCode(repo: UrlRepository, maxAttempts: Int = 100): String
}