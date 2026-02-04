package com.example.keywords.services;

import com.example.keywords.exception.BusinessRuleException;
import com.example.keywords.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class StringMaskServiceImpl implements StringMaskService{
        private final KeywordService keywordService;
        private static final int MAX_INPUT_LENGTH = 10000;

        public String maskSensitiveWords(String input) {
            // Validate input
            validateInput(input);

            try {
                List<String> keywords = keywordService.getAllKeywordWords();

                if (keywords.isEmpty()) {
                    log.warn("No keywords found in database for masking");
                    return input;
                }

                // Sort keywords by length (longest first) to avoid partial matches
                keywords.sort((a, b) -> Integer.compare(b.length(), a.length()));

                String masked = input;
                log.info("masked {}", masked);

                for (String keyword : keywords) {
                    // Create a regex pattern that matches the whole word (case-insensitive)
                    String regex = "\\b" + Pattern.quote(keyword) + "\\b";
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(masked);

                    // Replace with asterisks of same length
                    StringBuffer sb = new StringBuffer();
                    while (matcher.find()) {
                        String matchedWord = matcher.group();
                        log.info(" matchedWord {}", matchedWord);
                        String replacement = "*".repeat(matchedWord.length());
                        log.info("replacement {}",replacement);
                        matcher.appendReplacement(sb, replacement);
                    }
                    matcher.appendTail(sb);
                    masked = sb.toString();
                }

                log.debug("Successfully masked input. Original length: {}, Masked length: {}",
                        input.length(), masked.length());

                return masked;

            } catch (Exception e) {
                log.error("Error during text masking: {}", e.getMessage(), e);
                throw new BusinessRuleException("Failed to mask sensitive words: " + e.getMessage(), e);
            }
        }

        private void validateInput(String input) {
            if (input == null) {
                throw new ValidationException("Input cannot be null");
            }

            if (input.length() > MAX_INPUT_LENGTH) {
                throw new ValidationException(
                        String.format("Input exceeds maximum length of %d characters", MAX_INPUT_LENGTH));
            }

            // Additional validation if needed
            if (input.trim().isEmpty()) {
                log.info("Empty input provided for masking");
            }
        }
    }