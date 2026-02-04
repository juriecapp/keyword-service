package com.example.keywords;

import com.example.keywords.KeywordApplication;
import com.example.keywords.model.Keyword;
import com.example.keywords.repository.KeywordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(
        classes = KeywordApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class KeywordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KeywordRepository keywordRepository;

    private final List<String> testKeywords = Arrays.asList(
            "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE",
            "CREATE", "TABLE", "DROP", "ALTER", "GRANT", "REVOKE",
            "BEGIN", "COMMIT", "ROLLBACK", "TRANSACTION"
    );

    @SneakyThrows
    @BeforeEach
    void setUp() {
        // Clear existing data
        keywordRepository.deleteAll();

        log.info("Count Records - {}", keywordRepository.count());
        // Insert test keywords into H2 database
        for (String keyword : testKeywords) {
            Keyword entity = new Keyword();
            entity.setWord(keyword);
            keywordRepository.save(entity);
        }

        // Verify data was inserted
        assertEquals(testKeywords.size(), keywordRepository.count());
    }

    @Test
    void testMaskSensitiveWords_ShouldMaskAllKeywords() throws Exception {
        // Given
        String input = "SELECT * FROM users WHERE id = 1 AND INSERT INTO logs VALUES ('test')";
        String expectedMasked = "****** * **** users ***** id = 1 AND ****** INTO logs VALUES ('test')";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testMaskSensitiveWords_CaseInsensitive() throws Exception {
        // Given
        String input = "select * from users where id = 1";
        String expectedMasked = "****** * **** users ***** id = 1";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testMaskSensitiveWords_MixedCase() throws Exception {
        // Given
        String input = "Select * FrOm users Where id = 1";
        String expectedMasked = "****** * **** users ***** id = 1";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testMaskSensitiveWords_PartialWordsNotMasked() throws Exception {
        // Given
        String input = "This is a selection from the dropdown";
        // "selection" contains "select" but shouldn't be masked as it's not exact word
        // "from" should be masked

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a selection **** the dropdown"));
    }

    @Test
    void testMaskSensitiveWords_WithSQLTransaction() throws Exception {
        // Given
        String input = "BEGIN TRANSACTION; SELECT * FROM users; COMMIT;";
        String expectedMasked = "***** ***********; ****** * **** users; ******;";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testMaskSensitiveWords_NoKeywordsInInput() throws Exception {
        // Given
        String input = "This is a normal sentence without any SQL keywords";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(input));
    }

    @Test
    void testMaskSensitiveWords_EmptyInput() throws Exception {
        // Given
        String requestBody = "{\"input\": \"\"}";

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(400));
    }

    @Test
    void testMaskSensitiveWords_WithSpecialCharacters() throws Exception {
        // Given
        String input = "SELECT * FROM users WHERE email LIKE 'test@example.com'";
        String expectedMasked = "****** * **** users ***** email LIKE 'test@example.com'";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testFullCRUDWithMaskingIntegration() throws Exception {
        // Step 1: Create a new keyword via API
        String newKeywordJson = "{\"word\": \"EXECUTE\"}";

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newKeywordJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word").value("EXECUTE"))
                .andExpect(jsonPath("$.id").exists());

        // Step 2: Test that the new keyword gets masked
        String input = "EXECUTE stored_procedure";
        String requestBody = String.format("{\"input\": \"%s\"}", input);

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("******* stored_procedure"));
    }

    @Test
    void testGetAllKeywords() throws Exception {
        mockMvc.perform(get("/api/keywords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(testKeywords.size())))
                .andExpect(jsonPath("$[*].word", everyItem(is(in(testKeywords)))));
    }

    @Test
    void testCreateGetUpdateDeleteCycle() throws Exception {
        // Create
        String createJson = "{\"word\": \"NEWKEYWORD\"}";

        String response = mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word").value("NEWKEYWORD"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response
        Long createdId = objectMapper.readTree(response).get("id").asLong();

        // Get
        mockMvc.perform(get("/api/keywords/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.word").value("NEWKEYWORD"));

        // Update
        String updateJson = "{\"word\": \"UPDATEDKEYWORD\"}";

        mockMvc.perform(put("/api/keywords/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("UPDATEDKEYWORD"));

        // Verify update by getting again
        mockMvc.perform(get("/api/keywords/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("UPDATEDKEYWORD"));

        // Test that updated keyword gets masked
        String input = "This contains UPDATEDKEYWORD and SELECT";
        String maskRequestBody = String.format("{\"input\": \"%s\"}", input);

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(maskRequestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("This contains ************** and ******"));

        // Delete
        mockMvc.perform(delete("/api/keywords/{id}", createdId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/keywords/{id}", createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMaskWithMultipleSpacesAndNewlines() throws Exception {
        // Given
        String input = "SELECT  *  \nFROM  users\nWHERE  id = 1";
        String expectedMasked = "******  *  \n****  users\n*****  id = 1";

        String requestBody = String.format("{\"input\": \"%s\"}", input.replace("\n", "\\n"));

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testMaskWithPunctuation() throws Exception {
        // Given
        String input = "SELECT*FROM users; DROP TABLE temp; -- This is a comment";
        String expectedMasked = "*********** users; **** ***** temp; -- This is a comment";
        // Note: "SELECT*FROM" doesn't have space, so "SELECT" won't be masked due to word boundary

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        // When & Then
        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMasked));
    }

    @Test
    void testPerformanceWithManyKeywords() throws Exception {
        // Add many more keywords to test performance
        for (int i = 0; i < 50; i++) {
            Keyword keyword = new Keyword();
            keyword.setWord("KEYWORD_" + i);
            keywordRepository.save(keyword);
        }

        // Test with a complex query
        String input = "SELECT column1, column2 FROM table1 WHERE condition = true "
                + "AND column3 IN (SELECT id FROM table2) ORDER BY column1";

        String requestBody = String.format("{\"input\": \"%s\"}", input);

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();

        // Performance assertion (should complete within 500ms)
        long duration = endTime - startTime;
        System.out.println("Masking operation took: " + duration + "ms");
        assertTrue(duration < 500, "Masking should complete within 500ms");
    }
}
