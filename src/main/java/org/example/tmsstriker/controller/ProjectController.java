package org.example.tmsstriker.controller;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    // --- Пагінація + пошук ---
    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getPaged(
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable
    ) {
        Page<ProjectDTO> page = service.getPagedProjects(search, pageable);
        return ResponseEntity.ok(page);
    }

    // --- За ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable UUID id,
            @RequestBody ProjectDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- [Optional] Якщо треба всіх (НЕ для великих списків) ---
    // @GetMapping("/all")
    // public ResponseEntity<List<ProjectDTO>> getAll() {
    //     return ResponseEntity.ok(service.findAll());
    // }
}

