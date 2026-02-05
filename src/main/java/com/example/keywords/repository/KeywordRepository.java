package com.example.keywords.repository;

import com.example.keywords.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // Natural ID query - will use second level cache automatically
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "keywordByWord")
    })
    Optional<Keyword> findByWord(String word);

    // Exists query - can be cached
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    boolean existsByWord(String word);

    // Query with projection - cache results
    @Query("SELECT k.word FROM Keyword k")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "allKeywordWords")
    })
    List<String> findAllWords();

    // Additional cached queries
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Keyword> findAll();

    // Find keywords containing text - cacheable
    @Query("SELECT k FROM Keyword k WHERE LOWER(k.word) LIKE LOWER(CONCAT('%', :text, '%'))")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Keyword> findByWordContaining(String text);
}