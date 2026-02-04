package com.example.keywords.dto;

import lombok.Getter;

@Getter
public enum ApiErrorCode {
    // Keyword related errors
    KEYWORD_NOT_FOUND("KEYWORD_001", "Keyword not found"),
    KEYWORD_ALREADY_EXISTS("KEYWORD_002", "Keyword already exists"),
    KEYWORD_VALIDATION_FAILED("KEYWORD_003", "Keyword validation failed"),

    // Input validation errors
    INPUT_VALIDATION_FAILED("INPUT_001", "Input validation failed"),
    INVALID_INPUT_FORMAT("INPUT_002", "Invalid input format"),

    // Database errors
    DATABASE_CONSTRAINT_VIOLATION("DB_001", "Database constraint violation"),
    DATABASE_CONNECTION_FAILED("DB_002", "Database connection failed"),

    // Service errors
    SERVICE_UNAVAILABLE("SVC_001", "Service temporarily unavailable"),
    SERVICE_ERROR("SVC_002", "Internal service error"),

    // Masking service errors
    MASKING_FAILED("MASK_001", "Text masking failed"),
    INVALID_MASKING_INPUT("MASK_002", "Invalid input for masking");

    private final String code;
    private final String description;

    ApiErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
