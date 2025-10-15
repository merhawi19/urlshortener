package com.merhawi.urlshortener.utils

import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class InMemoryShortCodeGeneratorTest {

    private lateinit var repo: UrlRepository

    @BeforeEach
    fun setup() {
        repo = mock(UrlRepository::class.java)
    }

    @Test
    fun `should generate a unique short code when not found in repo`() {
        `when`(repo.findByShortCode(anyString())).thenReturn(null)
        val code = InMemoryShortCodeGenerator.generateUniqueCode(repo, 10)
        assertNotNull(code)
        assertEquals(7, code.length)
        verify(repo, atLeastOnce()).findByShortCode(anyString())
    }

    @Test
    fun `should increase code length after multiple failed attempts`() {
        var attemptCount = 0
        `when`(repo.findByShortCode(anyString())).thenAnswer {
            attemptCount++
            if (attemptCount < 25) mock(Url::class.java) else null
        }
        val code = InMemoryShortCodeGenerator.generateUniqueCode(repo, 30)
        assertTrue(code.length >= 8)
    }

    @Test
    fun `should throw exception after max attempts exceeded`() {
        `when`(repo.findByShortCode(anyString())).thenReturn(mock(Url::class.java))
        val exception = assertThrows<IllegalStateException> {
            InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        }
        assertEquals("Failed to generate unique short code after 5 attempts", exception.message)
    }

    @Test
    fun `should cache used short codes locally`() {
        `when`(repo.findByShortCode(anyString())).thenReturn(null)
        val first = InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        val second = InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        assertNotEquals(first, second)
    }
}
