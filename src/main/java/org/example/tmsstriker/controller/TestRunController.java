package org.example.tmsstriker.controller;

import org.example.tmsstriker.dto.ExecutionCommandDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.service.TestRunService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/testruns")
@CrossOrigin(origins = "http://localhost:3000")
public class TestRunController {

    private final TestRunService service;

    public TestRunController(TestRunService service) {
        this.service = service;
    }

    /** Отримати сторінку запусків по проєкту */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TestRunDTO>> getRunsByProject(
            @PathVariable UUID projectId,
            Pageable pageable
    ) {
        Page<TestRunDTO> page = service.getRunsByProject(projectId, pageable);
        return ResponseEntity.ok(page);
    }

    /** Отримати один запуск за UUID */
    @GetMapping("/{id}")
    public ResponseEntity<TestRunDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /** Створити новий запуск */
    @PostMapping
    public ResponseEntity<TestRunDTO> create(@RequestBody TestRunDTO dto) {
        return ResponseEntity.ok(service.createRun(dto));
    }

    /** Оновити існуючий запуск */
    @PutMapping("/{id}")
    public ResponseEntity<TestRunDTO> update(
            @PathVariable UUID id,
            @RequestBody TestRunDTO dto
    ) {
        return ResponseEntity.ok(service.updateRun(id, dto));
    }

    /** Видалити запуск */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteRun(id);
        return ResponseEntity.noContent().build();
    }

    /** Виконати команду над запуском */
    @PostMapping("/{id}/execute")
    public ResponseEntity<TestRunDTO> execute(
            @PathVariable UUID id,
            @RequestBody ExecutionCommandDTO cmd
    ) {
        return ResponseEntity.ok(service.executeCommand(id, cmd));
    }

    /** Позначити запуск як завершений */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID id) {
        service.completeRun(id);
        return ResponseEntity.noContent().build();
    }

    /** Клонувати запуск */
    @PostMapping("/{id}/clone")
    public ResponseEntity<TestRunDTO> clone(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cloneRun(id));
    }
}
