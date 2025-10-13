package com.merhawi.urlshortener

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc
) {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should redirect from short code to original URL`() {
        // 1. Create short URL
        val request = mapOf("originalUrl" to "https://example.com")

        val responseJson = mockMvc.perform(
            post("/api/url/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .response
            .contentAsString

        // Extract short code from response
        val response: Map<String, String> = objectMapper.readValue(responseJson)
        val shortCode = response["shortCode"]!!.substringAfterLast("/")

        mockMvc.perform(get("/api/url/{shortCode}", shortCode))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", "https://example.com"))
    }

    @Test
    fun `should return 404 for non-existent short code`() {
        mockMvc.perform(get("/api/url/nonexistent"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 404 for non-existent short code info`() {
        mockMvc.perform(get("/api/url/info/nonexistent"))
            .andExpect(status().isNotFound)
    }
}