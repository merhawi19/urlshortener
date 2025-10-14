package com.merhawi.urlshortener.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import java.time.Instant

data class ShortenRequest(
    @field:NotBlank(message = "Original URL must not be blank")
    @field:URL(message = "Original URL must be a valid URL")
    @field:URL(message = "Original URL must be a valid URL")
    val originalUrl: String,
    val expirationTime: Instant? = null
)
