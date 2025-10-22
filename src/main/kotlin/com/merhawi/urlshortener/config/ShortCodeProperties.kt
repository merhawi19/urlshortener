package com.merhawi.urlshortener.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "shortcode")
class ShortCodeProperties {
    var defaultLength: Int = 7
    var maxAttempts: Int = 100
    var lengthIncrementStep: Int = 20
    var redisReservationTtlMs: Long = 10_000
    var bitLength: Int = 48
    var cacheTtlMinutes: Long = 60
    var cacheMaxSize: Long = 20_000
    var alphabet: String = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var macAlgorithm: String = "HmacSHA256"
    var generator: String = "redis"

}
