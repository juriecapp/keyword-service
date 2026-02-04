package com.example.keywords.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request for masking sensitive words")
public class MaskRequestDTO {

    @NotBlank(message = "Input text is required")
    @Size(max = 10000, message = "Input text cannot exceed 10000 characters")
    @Schema(description = "Input text to be masked",
            example = "SELECT * FROM users WHERE id = 1",
            required = true)
    private String input;
}