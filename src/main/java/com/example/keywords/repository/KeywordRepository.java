package com.example.keywords.repository;

import com.example.keywords.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Keyword findByWord(String word);

    boolean existsByWord(String word);

    @Query("SELECT k.word FROM Keyword k")
    List<String> findAllWords();
}
