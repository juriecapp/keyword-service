package com.example.keywords.controller;

import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.dto.MaskRequestDTO;
import com.example.keywords.services.KeywordService;
import com.example.keywords.services.StringMaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KeywordController.class)
class KeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KeywordService keywordService;

    @MockBean
    private StringMaskService stringMaskService;

    @Test
    void createKeyword_ShouldReturnCreated() throws Exception {
        KeywordDTO input = new KeywordDTO();
        input.setWord("SELECT");

        KeywordDTO output = new KeywordDTO();
        output.setId(1L);
        output.setWord("SELECT");

        when(keywordService.createKeyword(any(KeywordDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.word").value("SELECT"));
    }

    @Test
    void maskSensitiveWords_ShouldReturnMaskedText() throws Exception {
        MaskRequestDTO request = new MaskRequestDTO();
        request.setInput("SELECT * FROM users");

        when(stringMaskService.maskSensitiveWords(anyString()))
                .thenReturn("****** * **** users");

        mockMvc.perform(post("/api/keywords/mask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("****** * **** users"));
    }
}
