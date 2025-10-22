package com.merhawi.urlshortener.service.shortener.generator

import com.github.benmanes.caffeine.cache.Caffeine
import com.merhawi.urlshortener.config.ShortCodeProperties
import org.springframework.stereotype.Component
import java.time.Duration

@Component("inMemoryGenerator")
class InMemoryShortCodeGenerator(
    props: ShortCodeProperties
) : BaseShortCodeGenerator<Unit>(
    props.alphabet,
    props.defaultLength
) {
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(props.cacheTtlMinutes))
        .maximumSize(props.cacheMaxSize)
        .build<String, Boolean>()

    private val step = props.lengthIncrementStep

    override fun generateUniqueCode(source: Unit, maxAttempts: Int): String {
        repeat(maxAttempts) { attempt ->
            val len = defaultLength + (attempt / step)
            val code = randomBase62(len)
            if (cache.getIfPresent(code) == null) {
                cache.put(code, true)
                return code
            }
        }
        throw IllegalStateException("Failed to generate unique short code after $maxAttempts attempts")
    }
}
