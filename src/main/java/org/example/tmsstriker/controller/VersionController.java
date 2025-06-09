package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.service.VersionService;
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
    @Operation(summary = "Create a new version")
    public VersionDTO create(@RequestBody VersionDTO dto) {
        return versionService.create(dto);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all versions for a project")
    public List<VersionDTO> getByProject(@PathVariable UUID projectId) {
        return versionService.getByProject(projectId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a version by ID")
    public void delete(@PathVariable UUID id) {
        versionService.delete(id);
    }
}
