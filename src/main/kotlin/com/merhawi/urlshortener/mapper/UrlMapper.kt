package com.merhawi.urlshortener.mapper

import com.merhawi.urlshortener.dto.*
import com.merhawi.urlshortener.model.*

fun Url.toDto(): UrlDto = UrlDto(
    id = id,
    originalUrl = originalUrl,
    shortCode = shortCode,
    status = status,
    category = category?.toDto(),
    createdAt = createdAt,
    expirationTime = expirationTime
)

fun Category.toDto(): CategoryDto = CategoryDto(
    id = id,
    name = name,
    baseUrl = baseUrl,
    description = description
)

fun CreateCategoryRequest.toEntity(): Category = Category(
    name = name,
    baseUrl = baseUrl,
    description = description
)

fun UpdateCategoryRequest.toEntity(existing: Category): Category = existing.apply {
    name = this@toEntity.name
    baseUrl = this@toEntity.baseUrl
    description = this@toEntity.description
}

// Optional mapper for ShortenResponse
fun Url.toShortenResponse(): ShortenResponse = ShortenResponse(
    shortCode = shortCode,
    originalUrl = originalUrl
)
