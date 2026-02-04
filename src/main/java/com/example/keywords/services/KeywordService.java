package com.example.keywords.services;


import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.model.Keyword;
import com.example.keywords.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


public interface KeywordService {

    public KeywordDTO createKeyword(KeywordDTO keywordDTO);

    public KeywordDTO getKeywordById(Long id);

    public List<KeywordDTO> getAllKeywords();

    public KeywordDTO updateKeyword(Long id, KeywordDTO keywordDTO);

    public void deleteKeyword(Long id);

    public List<String> getAllKeywordWords();
}