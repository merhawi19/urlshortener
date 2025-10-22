package com.merhawi.urlshortener.repository

import com.merhawi.urlshortener.model.Url
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UrlRepositoryTest @Autowired constructor(
    val repo: UrlRepository
) {

    @Test
    fun `should save and find by short code`() {
        val url = Url(originalUrl = "https://example.com", shortCode = "abc123")
        repo.save(url)

        val found = repo.findByShortCode("abc123")

        Assertions.assertThat(found).isNotNull()
        Assertions.assertThat(found?.originalUrl).isEqualTo("https://example.com")
    }

    @Test
    fun `should throw DataIntegrityViolationException on duplicate shortCode`() {
        val url1 = Url(originalUrl = "https://a.com", shortCode = "dup")
        val url2 = Url(originalUrl = "https://b.com", shortCode = "dup")

        repo.save(url1)

        assertThrows<DataIntegrityViolationException> {
            repo.saveAndFlush(url2)
        }
    }
}