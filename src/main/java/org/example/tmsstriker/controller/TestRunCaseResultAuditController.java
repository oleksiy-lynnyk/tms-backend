package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestRunCaseResultAuditDTO;
import org.example.tmsstriker.service.TestRunCaseResultAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-runs/{runId}/cases/{caseId}/audit")
@RequiredArgsConstructor
@Tag(name = "test-run-case-result-audit-controller", description = "API for managing audit trail of test run case results")
public class TestRunCaseResultAuditController {

    private final TestRunCaseResultAuditService service;

    @GetMapping
    @Operation(summary = "Get audit history for test run case result",
            description = "Returns the audit trail for a specific test run case result identified by runId and caseId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TestRunCaseResultAuditDTO.class))),
            @ApiResponse(responseCode = "404", description = "Test run or case not found", content = @Content)
    })
    public ResponseEntity<List<TestRunCaseResultAuditDTO>> getAudit(
            @PathVariable UUID runId,
            @PathVariable UUID caseId
    ) {
        List<TestRunCaseResultAuditDTO> auditTrail = service.getAuditTrail(runId, caseId);
        return ResponseEntity.ok(auditTrail);
    }
}
