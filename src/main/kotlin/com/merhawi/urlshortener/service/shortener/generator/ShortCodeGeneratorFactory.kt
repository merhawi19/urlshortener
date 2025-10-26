package com.merhawi.urlshortener.service.shortener.generator


import com.merhawi.urlshortener.config.ShortCodeProperties
import org.springframework.stereotype.Component

@Component
class ShortCodeGeneratorFactory(
    private val inMemoryGenerator: InMemoryShortCodeGenerator,
    private val redisGenerator: HmacShortCodeGenerator,
    private val simpleGenerator: SimpleShortCodeGenerator,
    private val props: ShortCodeProperties
) {
    fun getGenerator(): Any {
        return when (props.generator.lowercase()) {
            "redis" -> redisGenerator
            "inmemory" -> inMemoryGenerator
            "simple" -> simpleGenerator
            else -> throw IllegalArgumentException("Unknown generator mode: ${props.generator}")
        }
    }
}