package com.merhawi.urlshortener.mapper

import com.merhawi.urlshortener.dto.*
import com.merhawi.urlshortener.model.*

fun Url.toDto(): UrlDto = UrlDto(
    id = id,
    originalUrl = originalUrl,
    shortCode = shortCode,
    status = status,
    createdAt = createdAt,
    expirationTime = expirationTime
)


// Optional mapper for ShortenResponse
fun Url.toShortenResponse(): ShortenResponse = ShortenResponse(
    shortCode = shortCode,
    originalUrl = originalUrl
)
