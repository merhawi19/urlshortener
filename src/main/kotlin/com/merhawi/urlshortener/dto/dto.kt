package com.merhawi.urlshortener.dto

import com.merhawi.urlshortener.model.enums.UrlState
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import java.time.Instant

// ========== Category DTOs ==========

data class CategoryDto(
    val id: Long?,
    val name: String,
    val baseUrl: String?,
    val description: String?
)

data class CreateCategoryRequest(
    val name: String,
    val baseUrl: String? = null,
    val description: String? = null
)

data class UpdateCategoryRequest(
    val id: Long,
    val name: String,
    val baseUrl: String? = null,
    val description: String? = null
)

// ========== URL DTOs ==========

data class UrlDto(
    val id: Long?,
    val originalUrl: String,
    val shortCode: String,
    val status: UrlState,
    val category: CategoryDto?,
    val createdAt: Instant?,
    val expirationTime: Instant?
)

data class ShortenRequest(
    @field:NotBlank(message = "Original URL must not be blank")
    @field:URL(message = "Original URL must be a valid URL")
    @field:URL(message = "Original URL must be a valid URL")
    val originalUrl: String,
    val categoryId: Long? = null,
    val expirationTime: Instant? = null
)

data class ShortenResponse(

    val shortCode: String,
    val originalUrl: String
)

