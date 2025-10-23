package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.service.TestRunService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test-runs")
@RequiredArgsConstructor
public class TestRunController {

    private final TestRunService service;

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get Test Runs by Project",
            description = "Returns paginated list of Test Runs for given Project ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class)))
    })
    public ResponseEntity<Page<TestRunDTO>> getByProject(
            @PathVariable @Schema(description = "UUID of the project", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID projectId,
            Pageable pageable
    ) {
        Page<TestRunDTO> page = service.getByProject(projectId, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Operation(summary = "Create a new Test Run",
            description = "Creates a new Test Run.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Test Run created",  // ✅ ВИПРАВЛЕНО: 200 → 201
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<TestRunDTO> create(
            @RequestBody(description = "Test Run payload", required = true,
                    content = @Content(schema = @Schema(implementation = TestRunDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody TestRunDTO dto) {
        TestRunDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Test Run by ID",
            description = "Returns Test Run by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<TestRunDTO> getById(
            @PathVariable @Schema(description = "UUID of the Test Run", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        TestRunDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Test Run",
            description = "Updates fields of existing Test Run.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<TestRunDTO> update(
            @PathVariable UUID id,
            @RequestBody(description = "Test Run update payload", required = true,
                    content = @Content(schema = @Schema(implementation = TestRunDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody TestRunDTO dto) {
        TestRunDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Test Run",
            description = "Deletes Test Run by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Execute Test Run command",
            description = "Executes command on Test Run.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Executed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<TestRunDTO> execute(
            @PathVariable UUID id,
            @RequestBody(description = "Execution command payload", required = true,
                    content = @Content(schema = @Schema(implementation = ExecutionCommandDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody ExecutionCommandDTO command) {
        TestRunDTO result = service.executeCommand(id, command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete Test Run",
            description = "Marks Test Run as completed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<Void> complete(@PathVariable UUID id) {
        service.completeRun(id);  // ✅ ЗАЛИШЕНО: completeRun() існує в сервісі
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone Test Run",
            description = "Clones Test Run by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cloned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestRunDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<TestRunDTO> clone(@PathVariable UUID id) {
        TestRunDTO cloned = service.cloneRun(id);
        return ResponseEntity.ok(cloned);
    }
}