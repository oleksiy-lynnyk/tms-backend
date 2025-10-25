package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.service.TestSuiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-suites")
@RequiredArgsConstructor
@Tag(name = "test-suite-controller", description = "API for managing test suits")
public class TestSuiteController {

    private final TestSuiteService suiteService;

    @GetMapping("/tree")
    @Operation(summary = "Get suites tree for a project", description = "Returns hierarchical tree of test suites for given projectId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSuiteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid projectId", content = @Content)
    })
    public ResponseEntity<List<TestSuiteDTO>> getSuitesTree(@RequestParam UUID projectId) {
        if (projectId == null) {
            throw new ApiException("Project ID is required", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(suiteService.getSuitesTree(projectId));
    }

    @GetMapping
    @Operation(summary = "Get all suites as tree", description = "Returns full hierarchical tree of all test suites across projects.")
    public ResponseEntity<List<TestSuiteDTO>> getAllSuitesTree() {
        return ResponseEntity.ok(suiteService.getAllSuitesAsTree());
    }

    @GetMapping("/flat")
    @Operation(summary = "Get flat list of all suites", description = "Returns flat list of all test suites.")
    public ResponseEntity<List<TestSuiteDTO>> getAllSuitesFlat() {
        return ResponseEntity.ok(suiteService.getAllSuites());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test suite by ID", description = "Returns single test suite by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSuiteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suite not found", content = @Content)
    })
    public ResponseEntity<TestSuiteDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(suiteService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new test suite", description = "Creates a new test suite. Requires valid projectId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Suite created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSuiteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Suite name already exists", content = @Content)
    })
    public ResponseEntity<TestSuiteDTO> createSuite(@RequestBody TestSuiteDTO dto) {
        if (dto.getProjectId() == null) {
            throw new ApiException("Project ID is required to create a Test Suite", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(suiteService.createSuite(dto));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update existing test suite", description = "Updates fields of existing suite by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suite updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSuiteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suite not found", content = @Content)
    })
    public ResponseEntity<TestSuiteDTO> updateSuite(@PathVariable UUID id, @RequestBody TestSuiteDTO dto) {
        return ResponseEntity.ok(suiteService.updateSuite(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete suite by ID", description = "Deletes the suite and all children.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suite deleted"),
            @ApiResponse(responseCode = "404", description = "Suite not found")
    })
    public ResponseEntity<Void> deleteSuite(@PathVariable UUID id) {
        suiteService.deleteSuite(id);
        return ResponseEntity.noContent().build();
    }
}
