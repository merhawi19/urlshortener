package com.merhawi.urlshortener.config


import com.merhawi.urlshortener.utils.ShortCodeGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
/*
@Configuration
class GeneratorConfig {

    @Bean
    fun activeGenerator(
        @Value("\${shortener.generator}") generatorType: String,
        @Qualifier("inMemoryGenerator") inMemoryGenerator: ShortCodeGenerator,
        @Qualifier("redisGenerator") redisGenerator: ShortCodeGenerator
    ): ShortCodeGenerator {
        return when (generatorType.lowercase()) {
            "inmemory" -> inMemoryGenerator
            "redis" -> redisGenerator
            else -> inMemoryGenerator // fallback
        }
    }
}*/
