package com.example.keywords.controller;

import com.example.keywords.KeywordApplication;
import com.example.keywords.dto.ErrorResponse;
import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.dto.MaskRequestDTO;
import com.example.keywords.model.Keyword;
import com.example.keywords.repository.KeywordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = KeywordApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KeywordRepository keywordRepository;

    private Keyword existingKeyword;

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();

        existingKeyword = new Keyword();
        existingKeyword.setWord("EXISTING");
        existingKeyword = keywordRepository.save(existingKeyword);
    }

    @Test
    void testResourceNotFoundException() throws Exception {
        // Try to get non-existent keyword
        mockMvc.perform(get("/api/keywords/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.errorCode").value("KEYWORD_001"))
                .andExpect(jsonPath("$.suggestedActions", hasSize(greaterThan(0))));
    }

    @Test
    void testDuplicateResourceException() throws Exception {
        // Try to create duplicate keyword
        KeywordDTO duplicate = new KeywordDTO();
        duplicate.setWord("EXISTING");

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("already exists")))
                .andExpect(jsonPath("$.errorCode").value("KEYWORD_002"));
    }

    @Test
    void testValidationException_InvalidKeywordFormat() throws Exception {
        // Try to create keyword with invalid characters
        KeywordDTO invalid = new KeywordDTO();
        invalid.setWord("SELECT 123"); // Numbers not allowed

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.word",
                        containsString("letters, underscores, and asterisks")));
    }

    @Test
    void testValidationException_EmptyKeyword() throws Exception {
        // Try to create empty keyword
        KeywordDTO empty = new KeywordDTO();
        empty.setWord("");

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.word").exists());
    }

    @Test
    void testValidationException_KeywordTooLong() throws Exception {
        // Try to create very long keyword
        KeywordDTO tooLong = new KeywordDTO();
        tooLong.setWord("A".repeat(256)); // Max is 255

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooLong)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.word",
                        containsString("between 1 and 255 characters")));
    }

    @Test
    void testValidationException_EmptyMaskInput() throws Exception {
        // Try to mask empty input
        MaskRequestDTO empty = new MaskRequestDTO();
        empty.setInput("");

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.input").exists());
    }

    @Test
    void testValidationException_MaskInputTooLong() throws Exception {
        // Try to mask very long input
        MaskRequestDTO tooLong = new MaskRequestDTO();
        tooLong.setInput("A".repeat(10001)); // Max is 10000

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooLong)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.input",
                        containsString("cannot exceed 10000 characters")));
    }

    @Test
    void testHttpMessageNotReadableException() throws Exception {
        // Send invalid JSON
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON")));
    }

    @Test
    void testMethodArgumentTypeMismatchException() throws Exception {
        // Try to use string instead of long for ID
        mockMvc.perform(get("/api/keywords/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("should be of type")));
    }

    @Test
    void testUpdateNonExistentKeyword() throws Exception {
        KeywordDTO update = new KeywordDTO();
        update.setWord("UPDATED");

        mockMvc.perform(put("/api/keywords/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentKeyword() throws Exception {
        mockMvc.perform(delete("/api/keywords/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBusinessRuleException() throws Exception {
        // This would test if the StringMaskService throws a BusinessRuleException
        // You could mock the service to throw this exception
        MaskRequestDTO request = new MaskRequestDTO();
        request.setInput("SELECT * FROM users");

        // Normal case should work
        MvcResult result = mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void testConstraintViolationException() throws Exception {
        // Test path variable validation (if you add @Min, etc.)
        mockMvc.perform(get("/api/keywords/0")) // ID should be positive
                .andExpect(status().is(404)); // Currently no validation on path variable
    }

    @Test
    void testGeneralExceptionHandling() throws Exception {
        // This test would require mocking a service to throw a generic exception
        // For integration, we can test the fallback handler with a problematic scenario

        MaskRequestDTO request = new MaskRequestDTO();
        request.setInput(null); // This should be caught by validation first

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Should be validation error, not generic
    }
}