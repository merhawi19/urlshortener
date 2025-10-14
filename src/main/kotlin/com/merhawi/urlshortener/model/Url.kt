package com.merhawi.urlshortener.model

import com.merhawi.urlshortener.model.enums.UrlState
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "urls",
    indexes = [Index(name = "idx_short_code", columnList = "shortCode")]
)
class Url(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long ?= null

    @Column(nullable = false, length = 2048)
    lateinit var originalUrl: String

    @Column(nullable = false, unique = true, length = 32)
    lateinit var shortCode: String

    val createdAt: Instant = Instant.now()

    @Enumerated(EnumType.STRING)
    var status: UrlState = UrlState.ACTIVE

    @Column(nullable = true)
    var expirationTime: Instant? = null



    constructor(originalUrl: String, shortCode: String) : this() {
        this.originalUrl = originalUrl
        this.shortCode = shortCode
    }
    constructor(id: Long, originalUrl: String, shortCode: String) : this() {
        this.id = id          // ✅ fixed — now sets the class property
        this.originalUrl = originalUrl
        this.shortCode = shortCode
    }


    constructor(
        originalUrl: String,
        shortCode: String,
        expirationTime: Instant? = null
    ) : this() {
        this.originalUrl = originalUrl
        this.shortCode = shortCode
        this.expirationTime = expirationTime
    }

    constructor(
        originalUrl: String,
        expirationTime: Instant? = null
    ) : this() {
        this.originalUrl = originalUrl
        this.expirationTime = expirationTime
    }

}
