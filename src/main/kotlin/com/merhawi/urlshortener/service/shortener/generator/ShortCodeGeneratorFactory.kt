package com.merhawi.urlshortener.service.shortener.generator


import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.utils.RedisShortCodeGenerator
import com.merhawi.urlshortener.utils.ShortCodeGenerator
import org.springframework.stereotype.Component

@Component
class ShortCodeGeneratorFactory(
    private val inMemoryGenerator: InMemoryShortCodeGenerator,
    private val redisGenerator: RedisShortCodeGenerator,
    private val simpleGenerator: SimpleShortCodeGenerator,
    private val props: ShortCodeProperties
) {
    fun getGenerator(): Any {
        return when (props.generator.lowercase()) {
            "redis" -> redisGenerator
            "in-memory" -> inMemoryGenerator
            "simple" -> simpleGenerator
            else -> throw IllegalArgumentException("Unknown generator mode: ${props.generator}")
        }
    }
}