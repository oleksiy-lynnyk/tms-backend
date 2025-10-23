package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.BulkTestCaseRequestDTO;
import org.example.tmsstriker.dto.ImportResultDto;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.service.TestCaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get test case by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found", content = @Content(schema = @Schema(implementation = TestCaseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<TestCaseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/suite/{suiteId}")
    @Operation(summary = "Get test cases by suite")
    public ResponseEntity<Page<TestCaseDTO>> getBySuite(
            @PathVariable UUID suiteId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getCases(suiteId, search, pageable));
    }

    @PostMapping
    @Operation(summary = "Create test case")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = TestCaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<TestCaseDTO> create(
            @RequestBody(description = "Test case payload", required = true,
                    content = @Content(schema = @Schema(implementation = TestCaseDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody TestCaseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTestCase(dto));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update test case")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = TestCaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<TestCaseDTO> update(
            @PathVariable UUID id,
            @RequestBody(description = "Test case payload", required = true,
                    content = @Content(schema = @Schema(implementation = TestCaseDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody TestCaseDTO dto) {
        return ResponseEntity.ok(service.updateTestCase(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete test case")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk update test cases")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bulk operation done"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<Void> bulkUpdate(
            @RequestBody(description = "Bulk update payload", required = true,
                    content = @Content(schema = @Schema(implementation = BulkTestCaseRequestDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody BulkTestCaseRequestDTO dto) {
        service.bulkUpdate(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import")
    @Operation(summary = "Import test cases from CSV")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imported", content = @Content(schema = @Schema(implementation = ImportResultDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<ImportResultDto> importCsv(
            @RequestParam UUID suiteId,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        ImportResultDto result = service.importFromCsv(suiteId, file.getInputStream());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/search")
    @Operation(summary = "Advanced search for test cases")
    public ResponseEntity<Page<TestCaseDTO>> searchTestCases(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID suiteId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String automationStatus,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String component,
            @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        // TODO: Реалізувати в сервісі
        return ResponseEntity.ok(Page.empty());
    }

    // 🔍 НОВИЙ ENDPOINT: Get filter values
    @GetMapping("/filters")
    @Operation(summary = "Get available filter values")
    public ResponseEntity<Map<String, List<String>>> getFilterValues(
            @RequestParam(required = false) UUID projectId
    ) {
        // TODO: Реалізувати в сервісі
        Map<String, List<String>> filters = Map.of(
                "priorities", List.of("High", "Medium", "Low"),
                "statuses", List.of("Active", "Draft", "Deprecated"),
                "owners", List.of("john.doe", "jane.smith")
        );
        return ResponseEntity.ok(filters);
    }
}
