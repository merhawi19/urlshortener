package com.merhawi.urlshortener.controller

import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.ShortenResponse
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.service.UrlService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api")
@Validated
class UrlController(
    private val urlService: UrlService,
    @Value("\${app.base-url}") private val baseUrl: String
) {
    private val logger = LoggerFactory.getLogger(UrlController::class.java)

    /** Create new short URL */
    @PostMapping("/shorten")
    fun createShortUrl(@Valid @RequestBody request: ShortenRequest): ResponseEntity<ShortenResponse> {
        val url = urlService.createShortUrl(request )

        val shortUrl = "$baseUrl/${url.shortCode}"
        val response = ShortenResponse(
            shortCode = shortUrl,
            originalUrl = url.originalUrl
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)

    }

    /** Redirect endpoint (public) */

    @GetMapping("/{shortCode}")
    fun redirectToOriginalUrl(@PathVariable shortCode: String): ResponseEntity<Void> {
        val url = urlService.getOriginalUrl(shortCode)
        return if (url != null) {
            ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.originalUrl))
                .build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /** API to get info about a short URL (optional) */
    @GetMapping("/info/{shortCode}")
    fun getUrlInfo(@PathVariable shortCode: String): ResponseEntity<UrlDto> {
        val url = urlService.getOriginalUrl(shortCode)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(url)
    }
}
