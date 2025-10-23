package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.EnvironmentDTO;
import org.example.tmsstriker.service.EnvironmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/environments")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService service;

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all environments for a project",
            description = "Returns list of environments associated with the specified project ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnvironmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    public ResponseEntity<List<EnvironmentDTO>> getByProject(
            @PathVariable @Schema(description = "UUID of the project to filter environments", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID projectId) {
        List<EnvironmentDTO> list = service.getByProject(projectId);
        return ResponseEntity.ok(list);
    }
    // Додати ці методи в EnvironmentController:

    @GetMapping
    @Operation(summary = "Get all environments",
            description = "Returns list of all environments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnvironmentDTO.class)))
    })
    public ResponseEntity<List<EnvironmentDTO>> getAll() {
        List<EnvironmentDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get environment by ID",
            description = "Returns environment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnvironmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Environment not found", content = @Content)
    })
    public ResponseEntity<EnvironmentDTO> getById(
            @PathVariable @Schema(description = "UUID of the environment", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        EnvironmentDTO environment = service.getById(id);
        return ResponseEntity.ok(environment);
    }

    @PostMapping
    @Operation(summary = "Create a new environment",
            description = "Creates a new environment under a project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Environment created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnvironmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<EnvironmentDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Environment payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EnvironmentDTO.class))
            )
            @RequestBody EnvironmentDTO dto) {
        EnvironmentDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing environment",
            description = "Updates the fields of an existing environment identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Environment updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EnvironmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Environment not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<EnvironmentDTO> update(
            @PathVariable @Schema(description = "UUID of the environment to update", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated environment payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EnvironmentDTO.class))
            )
            @RequestBody EnvironmentDTO dto) {
        EnvironmentDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an environment",
            description = "Removes the environment identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Environment deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Environment not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "UUID of the environment to delete", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
