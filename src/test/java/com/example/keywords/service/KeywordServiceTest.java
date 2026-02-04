package com.example.keywords.service;

import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.model.Keyword;
import com.example.keywords.repository.KeywordRepository;
import com.example.keywords.services.KeywordService;
import com.example.keywords.services.KeywordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @Mock
    private KeywordRepository keywordRepository;

    @InjectMocks
    private KeywordServiceImpl keywordService;

    private Keyword keyword;
    private KeywordDTO keywordDTO;

    @BeforeEach
    void setUp() {
        keyword = new Keyword();
        keyword.setId(1L);
        keyword.setWord("SELECT");

        keywordDTO = new KeywordDTO();
        keywordDTO.setWord("SELECT");
    }

    @Test
    void createKeyword_ShouldReturnKeywordDTO() {
        when(keywordRepository.save(any(Keyword.class))).thenReturn(keyword);

        KeywordDTO result = keywordService.createKeyword(keywordDTO);

        assertNotNull(result);
        assertEquals("SELECT", result.getWord());
        verify(keywordRepository, times(1)).save(any(Keyword.class));
    }

    @Test
    void getKeywordById_ShouldReturnKeywordDTO() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.of(keyword));

        KeywordDTO result = keywordService.getKeywordById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SELECT", result.getWord());
    }

    @Test
    void getAllKeywords_ShouldReturnList() {
        List<Keyword> keywords = Arrays.asList(keyword);
        when(keywordRepository.findAll()).thenReturn(keywords);

        List<KeywordDTO> result = keywordService.getAllKeywords();

        assertEquals(1, result.size());
        assertEquals("SELECT", result.get(0).getWord());
    }
}
