package com.merhawi.urlshortener.Controller

import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.dto.UrlDto
import com.merhawi.urlshortener.service.UrlService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/url")
class UrlController(
    private val urlService: UrlService,
    @Value("\${app.base-url}") private val baseUrl: String
) {
    private val logger = LoggerFactory.getLogger(UrlController::class.java)

    /** Create new short URL */
    @PostMapping("/shorten")
    fun createShortUrl(@Valid @RequestBody request: ShortenRequest): ResponseEntity<String> {
        val url = urlService.createShortUrl(request )

        val shortUrl = "$baseUrl/${url.shortCode}"
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl)

    }

    /** Redirect endpoint (public) */
    @GetMapping("/{shortCode}")
    fun redirect(@PathVariable shortCode: String): ResponseEntity<Void> {
        val url = urlService.getOriginalUrl(shortCode)
            ?: return ResponseEntity.notFound().build()

        // Handle expired URLs
        if (url.expirationTime != null && url.expirationTime!!.isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(410).build() // Gone
        }

        return ResponseEntity.status(302)
            .location(URI.create(url.originalUrl))
            .build()
    }

    /** API to get info about a short URL (optional) */
    @GetMapping("/info/{shortCode}")
    fun getUrlInfo(@PathVariable shortCode: String): ResponseEntity<UrlDto> {
        val url = urlService.getOriginalUrl(shortCode)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(url)
    }
}
