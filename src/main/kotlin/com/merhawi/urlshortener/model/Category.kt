package com.merhawi.urlshortener.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "categories")
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    @field:NotBlank(message = "Category name must not be blank")
    var name: String,

    @Column(nullable = true)
    var baseUrl: String? = null,

    var description: String? = null,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    val urls: MutableList<Url> = mutableListOf()
)