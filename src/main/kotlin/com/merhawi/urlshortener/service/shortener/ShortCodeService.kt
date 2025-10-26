package com.merhawi.urlshortener.service.shortener


import com.merhawi.urlshortener.config.ShortCodeProperties
import com.merhawi.urlshortener.repository.UrlRepository
import com.merhawi.urlshortener.service.shortener.generator.BaseShortCodeGenerator
import com.merhawi.urlshortener.service.shortener.generator.ShortCodeGeneratorFactory
import com.merhawi.urlshortener.service.shortener.generator.SimpleShortCodeGenerator
import com.merhawi.urlshortener.service.shortener.generator.InMemoryShortCodeGenerator
import com.merhawi.urlshortener.service.shortener.generator.HmacShortCodeGenerator

import com.merhawi.urlshortener.utils.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ShortCodeService(
    private val urlRepository: UrlRepository,
    private val props: ShortCodeProperties,
    private val generatorFactory: ShortCodeGeneratorFactory
) {

    fun generateUniqueShortCode(originalUrl: String): String {
        val generator = generatorFactory.getGenerator()
        return when (generator) {
            is HmacShortCodeGenerator -> generator.generateUniqueCode(originalUrl,props.maxAttempts)
            is InMemoryShortCodeGenerator -> generator.generateUniqueCode(Unit,props.maxAttempts)
            is SimpleShortCodeGenerator -> generator.generateUniqueCode(urlRepository,props.maxAttempts)
            else -> throw IllegalArgumentException("Unsupported generator type: ${generator::class.simpleName}")
        }
    }

}
