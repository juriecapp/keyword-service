package com.example.keywords;

import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.repository.KeywordRepository;
import com.example.keywords.services.KeywordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class KeywordServiceIntegrationTest {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private KeywordRepository keywordRepository;

    @Test
    void testFullCRUDCycle() {
        // Create
        KeywordDTO dto = new KeywordDTO();
        dto.setWord("TESTKEYWORD");
        KeywordDTO created = keywordService.createKeyword(dto);

        assertNotNull(created.getId());
        assertEquals("TESTKEYWORD", created.getWord());

        // Read
        KeywordDTO found = keywordService.getKeywordById(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getWord(), found.getWord());

        // Update
        dto.setWord("UPDATEDWORD");
        KeywordDTO updated = keywordService.updateKeyword(created.getId(), dto);
        assertEquals("UPDATEDWORD", updated.getWord());

        // Delete
        keywordService.deleteKeyword(created.getId());
        assertFalse(keywordRepository.existsById(created.getId()));
    }
}
