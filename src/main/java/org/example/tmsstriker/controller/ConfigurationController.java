package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ConfigurationDTO;
import org.example.tmsstriker.service.ConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/configurations")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService service;

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all configurations for a project",
            description = "Returns list of configurations associated with the specified project ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfigurationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    public ResponseEntity<List<ConfigurationDTO>> getByProject(
            @PathVariable @Schema(description = "UUID of the project to filter configurations", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID projectId) {
        List<ConfigurationDTO> list = service.getByProject(projectId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(summary = "Get all configurations",
            description = "Returns list of all configurations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfigurationDTO.class)))
    })
    public ResponseEntity<List<ConfigurationDTO>> getAll() {
        List<ConfigurationDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get configuration by ID",
            description = "Returns configuration by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfigurationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content)
    })
    public ResponseEntity<ConfigurationDTO> getById(
            @PathVariable @Schema(description = "UUID of the configuration", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        ConfigurationDTO config = service.getById(id);
        return ResponseEntity.ok(config);
    }

    @PostMapping
    @Operation(summary = "Create a new configuration",
            description = "Creates a new configuration under a project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfigurationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<ConfigurationDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Configuration payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfigurationDTO.class))
            )
            @RequestBody ConfigurationDTO dto) {
        ConfigurationDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing configuration",
            description = "Updates the fields of an existing configuration identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfigurationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<ConfigurationDTO> update(
            @PathVariable @Schema(description = "UUID of the configuration to update", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated configuration payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfigurationDTO.class))
            )
            @RequestBody ConfigurationDTO dto) {
        ConfigurationDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a configuration",
            description = "Removes the configuration identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "UUID of the configuration to delete", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

