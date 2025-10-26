package com.merhawi.urlshortener.service.shortener.generator

import java.security.SecureRandom

abstract class BaseShortCodeGenerator<T>(
    protected val alphabet: String,
    protected val defaultLength: Int
) {
    companion object {
        private val RANDOM = SecureRandom()
    }

    private val alphabetSize = alphabet.length

    protected open fun randomBase62(length: Int = defaultLength): String {
        val bytes = ByteArray(length)
        RANDOM.nextBytes(bytes)
        val chars = CharArray(length)
        for (i in bytes.indices) {
            chars[i] = alphabet[(bytes[i].toInt() and 0xFF) % alphabetSize]
        }
        return String(chars)
    }

    abstract fun generateUniqueCode(source: T, maxAttempts: Int = 100): String
}
