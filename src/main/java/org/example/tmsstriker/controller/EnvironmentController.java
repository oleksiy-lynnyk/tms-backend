package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.EnvironmentDTO;
import org.example.tmsstriker.service.EnvironmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/environments")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService service;

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all environments for a project")
    public List<EnvironmentDTO> getByProject(@PathVariable UUID projectId) {
        return service.getByProject(projectId);
    }

    @PostMapping
    public ResponseEntity<EnvironmentDTO> create(@RequestBody EnvironmentDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


