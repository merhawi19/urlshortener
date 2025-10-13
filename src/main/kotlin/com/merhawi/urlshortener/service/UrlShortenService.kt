package com.merhawi.urlshortener.service

import com.merhawi.urlshortener.Repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlShortenService (private val repo: UrlRepository) {
    private val alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    // Generates a random Base62-like short code
    fun generate(length: Int = 7): String {
        val sb = StringBuilder()
        val rnd = kotlin.random.Random.Default
        repeat(length) {
            sb.append(alphabet[rnd.nextInt(alphabet.length)])
        }
        return sb.toString()
    }

    fun iDgenerate(): String {
                while (true) {
            val code = generate()
            if (repo.findByShortCode(code) == null) {
                               return code
                }

            }
        }
}