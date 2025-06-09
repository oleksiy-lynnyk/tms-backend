// src/main/java/org/example/tmsstriker/controller/ConfigurationController.java
package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.ConfigurationDTO;
import org.example.tmsstriker.service.ConfigurationService;
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
    @Operation(summary = "Get all configurations for a project")
    public List<ConfigurationDTO> getByProject(@PathVariable UUID projectId) {
        return service.getByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<ConfigurationDTO> create(@RequestBody ConfigurationDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
