package com.example.keywords.service;

import com.example.keywords.services.KeywordService;
import com.example.keywords.services.StringMaskService;
import com.example.keywords.services.StringMaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StringMaskServiceTest {

    @Mock
    private KeywordService keywordService;

    @InjectMocks
    private StringMaskServiceImpl stringMaskService;

    @BeforeEach
    void setUp() {
        List<String> keywords = Arrays.asList("SELECT", "FROM", "WHERE", "INSERT");
        when(keywordService.getAllKeywordWords()).thenReturn(keywords);
    }

    @Test
    void maskSensitiveWords_ShouldMaskKeywords() {
        String input = "SELECT * FROM users WHERE id = 1";
        String expected = "****** * **** users ***** id = 1";

        String result = stringMaskService.maskSensitiveWords(input);

        assertEquals(expected, result);
    }

    @Test
    void maskSensitiveWords_ShouldHandleCaseInsensitive() {
        String input = "select * from users where id = 1";
        String expected = "****** * **** users ***** id = 1";

        String result = stringMaskService.maskSensitiveWords(input);

        assertEquals(expected, result);
    }

    @Test
    void maskSensitiveWords_ShouldHandleNoKeywords() {
        String input = "This is a normal sentence without keywords";

        String result = stringMaskService.maskSensitiveWords(input);

        assertEquals(input, result);
    }
}
