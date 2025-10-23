package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/versions")
@Tag(name = "version-controller", description = "API for managing project versions")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @PostMapping
    @Operation(summary = "Create a new version",
            description = "Creates a new version (milestone/release) under the specified project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Version created",  // ✅ ВИПРАВЛЕНО: 200 → 201
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VersionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<VersionDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload for creating a version",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VersionDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody VersionDTO dto
    ) {
        VersionDTO created = versionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing version",
            description = "Updates fields of an existing version identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Version not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<VersionDTO> update(
            @PathVariable @Schema(description = "UUID of the version to update",
                    example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload for updating a version",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VersionDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody VersionDTO dto
    ) {
        VersionDTO updated = versionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all versions for a project",
            description = "Returns list of versions associated with the specified project ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    public ResponseEntity<List<VersionDTO>> getByProject(
            @PathVariable @Schema(description = "UUID of the project to filter versions",
                    example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID projectId
    ) {
        List<VersionDTO> list = versionService.getByProject(projectId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get version by ID",  // ✅ ДОДАНО: Swagger документацію
            description = "Returns version by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Version not found", content = @Content)
    })
    public ResponseEntity<VersionDTO> getById(
            @PathVariable @Schema(description = "UUID of the version to retrieve",
                    example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        VersionDTO version = versionService.findById(id);
        return ResponseEntity.ok(version);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a version",
            description = "Deletes the version identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Version deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Version not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "UUID of the version to delete",
                    example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id
    ) {
        versionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}