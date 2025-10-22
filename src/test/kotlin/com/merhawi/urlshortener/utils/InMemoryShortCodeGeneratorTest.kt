package com.merhawi.urlshortener.utils

import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class InMemoryShortCodeGeneratorTest {

    private lateinit var repo: UrlRepository

    @BeforeEach
    fun setup() {
        repo = Mockito.mock(UrlRepository::class.java)
    }

    @Test
    fun `should generate a unique short code when not found in repo`() {
        Mockito.`when`(repo.findByShortCode(ArgumentMatchers.anyString())).thenReturn(null)
        val code = InMemoryShortCodeGenerator.generateUniqueCode(repo, 10)
        Assertions.assertNotNull(code)
        Assertions.assertEquals(7, code.length)
        Mockito.verify(repo, Mockito.atLeastOnce()).findByShortCode(ArgumentMatchers.anyString())
    }

    @Test
    fun `should increase code length after multiple failed attempts`() {
        var attemptCount = 0
        Mockito.`when`(repo.findByShortCode(ArgumentMatchers.anyString())).thenAnswer {
            attemptCount++
            if (attemptCount < 25) Mockito.mock(Url::class.java) else null
        }
        val code = InMemoryShortCodeGenerator.generateUniqueCode(repo, 30)
        Assertions.assertTrue(code.length >= 8)
    }

    @Test
    fun `should throw exception after max attempts exceeded`() {
        Mockito.`when`(repo.findByShortCode(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(Url::class.java))
        val exception = assertThrows<IllegalStateException> {
            InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        }
        Assertions.assertEquals("Failed to generate unique short code after 5 attempts", exception.message)
    }

    @Test
    fun `should cache used short codes locally`() {
        Mockito.`when`(repo.findByShortCode(ArgumentMatchers.anyString())).thenReturn(null)
        val first = InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        val second = InMemoryShortCodeGenerator.generateUniqueCode(repo, 5)
        Assertions.assertNotEquals(first, second)
    }
}