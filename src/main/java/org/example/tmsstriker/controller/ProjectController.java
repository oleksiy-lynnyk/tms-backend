package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.dto.ErrorResponse;
import org.example.tmsstriker.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @GetMapping
    @Operation(summary = "Get all projects", description = "Returns list of all projects.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class)))
    })
    public ResponseEntity<List<ProjectDTO>> getAll() {
        List<ProjectDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Returns project by its UUID or 404 if not found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    public ResponseEntity<ProjectDTO> getById(@PathVariable UUID id) {
        ProjectDTO dto = service.findById(id)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Creates a new project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<ProjectDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Project payload", required = true,
                    content = @Content(schema = @Schema(implementation = ProjectDTO.class))
            )
            @RequestBody ProjectDTO dto) {
        ProjectDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project", description = "Updates fields of an existing project identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<ProjectDTO> update(
            @PathVariable @Schema(description = "UUID of the project to update", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated project payload", required = true,
                    content = @Content(schema = @Schema(implementation = ProjectDTO.class))
            )
            @RequestBody ProjectDTO dto) {
        ProjectDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project", description = "Removes the project identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "UUID of the project to delete", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
