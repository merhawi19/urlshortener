package com.merhawi.urlshortener.service.shortener.generator


import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.repository.UrlRepository
import org.springframework.stereotype.Component

/**
 * Database-backed generator.
 * Ensures global uniqueness by checking repository for existing short codes.
 */
@Component("simpleGenerator")
class SimpleShortCodeGenerator (    props: ShortCodeProperties
): BaseShortCodeGenerator<UrlRepository>( props.alphabet,
    props.defaultLength) {

    //private val defaultLength = props.defaultLength
    private val step = props.lengthIncrementStep


    override fun generateUniqueCode(source: UrlRepository, maxAttempts: Int): String {
        repeat(maxAttempts) { attempt ->
            val length = defaultLength + (attempt / step)
            val code = randomBase62(length)
            if (source.findByShortCode(code) == null) {
                return code
            }
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}
