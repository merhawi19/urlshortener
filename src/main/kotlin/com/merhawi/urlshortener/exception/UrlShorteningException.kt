package com.merhawi.urlshortener.exception

class UrlShorteningException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
