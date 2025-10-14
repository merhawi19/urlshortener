package com.merhawi.urlshortener.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        return ResponseEntity.badRequest().body(
            mapOf(
                "status" to 400,
                "error" to "Validation Failed",
                "details" to errors
            )
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            mapOf(
                "status" to 404,
                "error" to "Not Found",
                "message" to (ex.message ?: "Resource not found")
            )
        )

    @ExceptionHandler(Exception::class)
    fun handleGenericError(ex: Exception): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            mapOf(
                "status" to 500,
                "error" to "Internal Server Error",
                "message" to ex.localizedMessage
            )
        )
    @ExceptionHandler(UrlShorteningException::class)
    fun handleUrlShorteningError(ex: UrlShorteningException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            mapOf(
                "status" to 409,
                "error" to "Short URL Creation Failed",
                "message" to (ex.message ?: "Unknown error occurred while creating short URL.")
            )
        )

}
