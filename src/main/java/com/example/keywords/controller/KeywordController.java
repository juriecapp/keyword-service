package com.example.keywords.controller;

import com.example.keywords.dto.ErrorResponse;
import com.example.keywords.dto.KeywordDTO;
import com.example.keywords.dto.MaskRequestDTO;
import com.example.keywords.services.KeywordService;
import com.example.keywords.services.StringMaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
@Tag(name = "Keyword Management", description = "APIs for managing and masking keywords")
public class KeywordController {

    private final KeywordService keywordService;
    private final StringMaskService stringMaskService;

    @PostMapping
    @Operation(summary = "Create a new keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Keyword created successfully",
                    content = @Content(schema = @Schema(implementation = KeywordDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Keyword already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<KeywordDTO> createKeyword(
            @Parameter(description = "Keyword to create", required = true)
            @Valid @RequestBody KeywordDTO keywordDTO) {

        log.info("Creating new keyword: {}", keywordDTO.getWord());
        KeywordDTO created = keywordService.createKeyword(keywordDTO);
        log.info("Keyword created successfully with ID: {}", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get keyword by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Keyword found",
                    content = @Content(schema = @Schema(implementation = KeywordDTO.class))),
            @ApiResponse(responseCode = "404", description = "Keyword not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<KeywordDTO> getKeywordById(
            @Parameter(description = "Keyword ID", required = true, example = "1")
            @PathVariable Long id) {

        log.debug("Fetching keyword with ID: {}", id);
        KeywordDTO keyword = keywordService.getKeywordById(id);
        log.debug("Keyword found: {}", keyword.getWord());

        return ResponseEntity.ok(keyword);
    }

    @GetMapping
    @Operation(summary = "Get all keywords")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Keywords retrieved successfully",
                    content = @Content(schema = @Schema(implementation = KeywordDTO.class)))
    })
    public ResponseEntity<List<KeywordDTO>> getAllKeywords() {
        log.debug("Fetching all keywords");
        List<KeywordDTO> keywords = keywordService.getAllKeywords();
        log.debug("Found {} keywords", keywords.size());

        return ResponseEntity.ok(keywords);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Keyword updated successfully",
                    content = @Content(schema = @Schema(implementation = KeywordDTO.class))),
            @ApiResponse(responseCode = "404", description = "Keyword not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Keyword already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<KeywordDTO> updateKeyword(
            @Parameter(description = "Keyword ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated keyword data", required = true)
            @Valid @RequestBody KeywordDTO keywordDTO) {

        log.info("Updating keyword with ID: {}", id);
        KeywordDTO updated = keywordService.updateKeyword(id, keywordDTO);
        log.info("Keyword updated successfully");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Keyword deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Keyword not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteKeyword(
            @Parameter(description = "Keyword ID", required = true)
            @PathVariable Long id) {

        log.info("Deleting keyword with ID: {}", id);
        keywordService.deleteKeyword(id);
        log.info("Keyword deleted successfully");

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mask")
    @Operation(summary = "Mask sensitive words in input text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text masked successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Masking failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> maskSensitiveWords(
            @Parameter(description = "Text to be masked", required = true)
            @Valid @RequestBody MaskRequestDTO request) {

        log.info("Masking sensitive words in input text (length: {})",
                request.getInput().length());

        String masked = stringMaskService.maskSensitiveWords(request.getInput());

        log.info("Text masked successfully. Masked length: {}", masked.length());

        return ResponseEntity.ok(masked);
    }
}
