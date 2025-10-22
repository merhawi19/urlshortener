package com.merhawi.urlshortener.service.shortener


import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.service.shortener.generator.BaseShortCodeGenerator
import com.merhawi.urlshortener.service.shortener.generator.ShortCodeGeneratorFactory
import com.merhawi.urlshortener.service.shortener.generator.SimpleShortCodeGenerator
import com.merhawi.urlshortener.utils.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ShortCodeService(
    private val urlRepository: UrlRepository,
    private val generatorFactory: ShortCodeGeneratorFactory
) {

    fun generateUniqueShortCode(originalUrl: String): String {
        val generator = generatorFactory.getGenerator()
        return when (generator) {
            is RedisShortCodeGenerator -> generator.generateUniqueCode(originalUrl,10)
            is InMemoryShortCodeGenerator -> generator.generateUniqueCode(urlRepository)
            is SimpleShortCodeGenerator -> generator.generateUniqueCode(urlRepository,10)
            else -> throw IllegalArgumentException("Unsupported generator type: ${generator::class.simpleName}")
        }
    }

}
