package com.merhawi.urlshortener.utils

import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ShortCodeGeneratorTest {

    private lateinit var repo: UrlRepository

    @BeforeEach
    fun setup() {
        repo = mock<UrlRepository>()
    }
    @Test
    fun `should generate mostly unique short codes`() {
        val codes = (1..1000).map { ShortCodeGenerator.generateUniqueCode(repo,100) }
        val uniqueCount = codes.toSet().size
        Assertions.assertTrue(uniqueCount > 990, "Expected >990 unique codes but got $uniqueCount")
    }

    @Test
    fun `should generate alphanumeric codes of default length`() {
        val code = ShortCodeGenerator.generateUniqueCode(repo,100)
        Assertions.assertEquals(7, code.length, "Default short code length should be 7")
        Assertions.assertTrue(code.all { it.isLetterOrDigit() }, "Code must be alphanumeric")
    }

    @Test
    fun `should generate unique code not existing in repository`() {

        whenever(repo.findByShortCode(any())).thenReturn(null)

        val code = ShortCodeGenerator.generateUniqueCode(repo,100)

        Assertions.assertNotNull(code)
        Assertions.assertEquals(7, code.length)
    }

    @Test
    fun `should retry and eventually generate unique code`() {
        val repo = mock<UrlRepository>()

        // Return Url for first few attempts, then null to simulate success
        whenever(repo.findByShortCode(any()))
            .thenReturn(Url("https://example.com", "dup1"))
            .thenReturn(Url("https://example.com", "dup2"))
            .thenReturn(Url("https://example.com", "dup3"))
            .thenReturn(null)

        val code = ShortCodeGenerator.generateUniqueCode(repo,100)

        Assertions.assertNotNull(code)
        Assertions.assertEquals(7, code.length)
        verify(repo, atLeast(4)).findByShortCode(any())
    }

    @Test
    fun `should throw after exceeding max attempts`() {
        val repo = mock<UrlRepository>()

        // Always return a non-null Url (duplicate)
        whenever(repo.findByShortCode(any())).thenReturn(Url("https://example.com", "dup"))

        val exception = assertThrows<IllegalStateException> {
            ShortCodeGenerator.generateUniqueCode(repo, 5)
            //ShortCodeGenerator.idGenerate(repo, maxAttempts = 5)
        }

        Assertions.assertEquals(
            "Failed to generate unique short code after 5 attempts",
            exception.message
        )

        verify(repo, times(5)).findByShortCode(any())
    }
}