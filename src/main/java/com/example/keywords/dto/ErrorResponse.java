package com.example.keywords.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type/category", example = "BAD_REQUEST")
    private String error;

    @Schema(description = "Detailed error message", example = "Validation failed for input parameters")
    private String message;

    @Schema(description = "Path where the error occurred", example = "/api/keywords")
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp when error occurred", example = "2024-01-15 10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Detailed validation errors")
    private Map<String, String> fieldErrors;

    @Schema(description = "List of sub-errors")
    private List<String> subErrors;

    @Schema(description = "Debug message (only in development)")
    private String debugMessage;

    @Schema(description = "Error code for client reference", example = "KEYWORD_001")
    private String errorCode;

    @Schema(description = "Suggested actions to resolve the error")
    private List<String> suggestedActions;

    // Factory methods for common error types
    public static ErrorResponse createValidationError(String message, String path,
                                                      Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
    }

    public static ErrorResponse createNotFoundError(String message, String path) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse createInternalError(String message, String path,
                                                    String debugMessage) {
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .debugMessage(debugMessage)
                .build();
    }
}
