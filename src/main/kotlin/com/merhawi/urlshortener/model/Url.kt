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
    val id: Long ?= null

    @Column(nullable = false, length = 2048)
    lateinit var originalUrl: String

    @Column(nullable = false, unique = true, length = 32)
    lateinit var shortCode: String

    val createdAt: Instant = Instant.now()

    @Enumerated(EnumType.STRING)
    var status: UrlState = UrlState.ACTIVE

    @Column(nullable = true)
    var expirationTime: Instant? = null

    @Column(nullable = true)
    var updatedBy: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null

    constructor(originalUrl: String, shortCode: String) : this() {
        this.originalUrl = originalUrl
        this.shortCode = shortCode
    }

    constructor(originalUrl: String, shortCode: String, category: Category? = null) : this() {
        this.originalUrl = originalUrl
        this.shortCode = shortCode
        this.category = category
    }

    constructor(
        originalUrl: String,
        shortCode: String,
        category: Category,
        expirationTime: Instant? = null
    ) : this() {
        this.originalUrl = originalUrl
        this.shortCode = shortCode
        this.category = category
        this.expirationTime = expirationTime
    }

    constructor(
        originalUrl: String,
        category: Category,
        expirationTime: Instant? = null
    ) : this() {
        this.originalUrl = originalUrl
        this.category = category
        this.expirationTime = expirationTime
    }

}
