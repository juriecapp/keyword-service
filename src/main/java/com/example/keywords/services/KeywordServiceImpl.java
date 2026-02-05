package com.example.keywords.services;


import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.exception.DuplicateResourceException;
import com.example.keywords.exception.ResourceNotFoundException;
import com.example.keywords.model.Keyword;
import com.example.keywords.repository.KeywordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.hibernate.stat.Statistics;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordServiceImpl implements KeywordService {

        private final KeywordRepository keywordRepository;

        @PersistenceContext
        private EntityManager entityManager;

        private final EntityManagerFactory entityManagerFactory;

        @CacheEvict(value = {"keywordById", "allKeywords", "allKeywordWords"}, allEntries = true)
        public KeywordDTO createKeyword(KeywordDTO keywordDTO) {
            String word = keywordDTO.getWord().toUpperCase();

            // Check for duplicates
            if (keywordRepository.existsByWord(word)) {
                throw new DuplicateResourceException("Keyword", "word", word);
            }

            Keyword keyword = new Keyword();
            keyword.setWord(word);
            Keyword saved = keywordRepository.save(keyword);
            return convertToDTO(saved);
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "keywordById", key = "#id", unless = "#result == null")
        public KeywordDTO getKeywordById(Long id) {
            Keyword keyword = keywordRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Keyword", "id", id));
            return convertToDTO(keyword);
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "allKeywords")
        public List<KeywordDTO> getAllKeywords() {
            return keywordRepository.findAll()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        @Transactional
        @CacheEvict(value = {"keywordById", "allKeywords", "allKeywordWords"},
                key = "#id", allEntries = false)
        public KeywordDTO updateKeyword(Long id, KeywordDTO keywordDTO) {
            Keyword keyword = keywordRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Keyword", "id", id));

            String newWord = keywordDTO.getWord().toUpperCase();

            // Check if updating to a duplicate word (different from current)
            if (!keyword.getWord().equals(newWord) && keywordRepository.existsByWord(newWord)) {
                throw new DuplicateResourceException("Keyword", "word", newWord);
            }

            keyword.setWord(newWord);
            Keyword updated = keywordRepository.save(keyword);
            return convertToDTO(updated);
        }

        @Transactional
        @CacheEvict(value = {"keywordById", "allKeywords", "allKeywordWords"},
                key = "#id", allEntries = true)
        public void deleteKeyword(Long id) {
            if (!keywordRepository.existsById(id)) {
                throw new ResourceNotFoundException("Keyword", "id", id);
            }
            keywordRepository.deleteById(id);
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "allKeywordWords")
        public List<String> getAllKeywordWords() {
                return keywordRepository.findAllWords();
        }

        private KeywordDTO convertToDTO(Keyword keyword) {
            KeywordDTO dto = new KeywordDTO();
            dto.setId(keyword.getId());
            dto.setWord(keyword.getWord());
            return dto;
        }
    }