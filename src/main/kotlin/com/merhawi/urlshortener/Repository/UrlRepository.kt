package com.merhawi.urlshortener.Repository

import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.model.enums.UrlState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : JpaRepository<Url, Long> {
    fun findByShortCode(shortCode: String): Url?
    fun findByShortCodeAndStatus(shortCode: String,urlState: UrlState): Url?
    fun findByStatus(urlState: UrlState): List<Url>
}