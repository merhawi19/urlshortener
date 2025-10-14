package com.merhawi.urlshortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.merhawi.urlshortener.dto.ShortenRequest
import com.merhawi.urlshortener.model.Url
import com.merhawi.urlshortener.repository.UrlRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UrlControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val repo: UrlRepository
) {

    @BeforeEach
    fun cleanup() {
        repo.deleteAll() // extra safeguard
    }

    @Test
    fun `POST shorten should create short URL`() {
        val request = ShortenRequest(originalUrl = "https://example.org")

        mockMvc.perform(
            post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.originalUrl").value("https://example.org"))
            .andExpect(jsonPath("$.shortCode").exists())
    }

    @Test
    fun `GET info should return URL details`() {
        val code = "info${Random.nextInt(1000,9999)}"
        val url = repo.save(Url(originalUrl = "https://info-test.com", shortCode = code))

        mockMvc.perform(get("/api/info/${url.shortCode}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.originalUrl").value("https://info-test.com"))
    }

    @Test
    fun `GET redirect should return 302 and redirect to original URL`() {
        val code = "redir${Random.nextInt(1000,9999)}"
        val url = repo.save(Url(originalUrl = "https://redirect.com", shortCode = code))

        val result = mockMvc.perform(get("/api/${url.shortCode}"))
            .andExpect(status().isFound)
            .andReturn()

        assert(result.response.getHeader("Location") == "https://redirect.com")
    }

    @Test
    fun `GET info should return 404 if not found`() {
        mockMvc.perform(get("/api/info/notfound"))
            .andExpect(status().isNotFound)
    }
}
