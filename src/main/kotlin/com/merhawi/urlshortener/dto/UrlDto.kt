package com.merhawi.urlshortener.dto

import com.merhawi.urlshortener.model.enums.UrlState
import java.time.Instant

data class UrlDto(
    val id: Long?,
    val originalUrl: String,
    val shortCode: String,
    val status: UrlState,
    val createdAt: Instant?,
    val expirationTime: Instant?
)