package com.merhawi.urlshortener.utils

import com.merhawi.urlshortener.repository.UrlRepository
/**
 * Generates random short codes ensuring uniqueness using the database repository.
 */
object ShortCodeGenerator : BaseShortCodeGeneratorOld<UrlRepository>() {

    private const val DEFAULT_LENGTH = 7
    private const val LENGTH_INCREMENT_STEP = 20

    override fun generateUniqueCode(source: UrlRepository, maxAttempts: Int): String {
        repeat(maxAttempts) { attempt ->
            val codeLength = DEFAULT_LENGTH + (attempt / LENGTH_INCREMENT_STEP)
            val code = generate(codeLength)
            if (source.findByShortCode(code) == null) return code
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}