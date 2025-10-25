package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestRunCaseResultDTO;
import org.example.tmsstriker.service.TestRunCaseResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-runs/{runId}/cases")
@RequiredArgsConstructor
@Tag(name = "test-run-case-result-controller", description = "API for managing test run case results")
public class TestRunCaseResultController {

    private final TestRunCaseResultService service;

    @PostMapping("/{caseId}/result")
    @Operation(summary = "Set or update test run case result", description = "Creates or updates the result for a specific test case in a test run.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Result saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TestRunCaseResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<TestRunCaseResultDTO> setResult(
            @PathVariable UUID runId,
            @PathVariable UUID caseId,
            @RequestBody TestRunCaseResultDTO dto
    ) {
        return ResponseEntity.ok(service.createOrUpdateResult(runId, caseId, dto));
    }

    @GetMapping
    @Operation(summary = "Get all case results for test run", description = "Returns all test case results for the specified test run.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TestRunCaseResultDTO.class)))
    })
    public ResponseEntity<List<TestRunCaseResultDTO>> getAllResults(@PathVariable UUID runId) {
        return ResponseEntity.ok(service.getResultsForRun(runId));
    }

    @GetMapping("/{caseId}/result")
    @Operation(summary = "Get specific test run case result", description = "Returns the result for a specific test case within a test run.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TestRunCaseResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "Result not found", content = @Content)
    })
    public ResponseEntity<TestRunCaseResultDTO> getResult(
            @PathVariable UUID runId,
            @PathVariable UUID caseId
    ) {
        return ResponseEntity.ok(service.getResult(runId, caseId));
    }
}

