package com.example.keywords.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Keyword Data Transfer Object")
public class KeywordDTO {

    @Schema(description = "Keyword ID", example = "1")
    private Long id;

    @NotBlank(message = "Keyword word is required")
    @Size(min = 1, max = 255, message = "Keyword must be between 1 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z_*]+$", message = "Keyword can only contain letters, underscores, and asterisks")
    @Schema(description = "Keyword word", example = "SELECT", required = true)
    private String word;
}
