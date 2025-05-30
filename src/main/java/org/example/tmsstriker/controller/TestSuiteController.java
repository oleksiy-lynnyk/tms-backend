package org.example.tmsstriker.controller;

import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.service.TestSuiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/testsuites")
@CrossOrigin(origins = "http://localhost:3000")
public class TestSuiteController {

    private final TestSuiteService suiteService; // <-- одне поле

    public TestSuiteController(TestSuiteService service) {
        this.suiteService = service;
    }

    @GetMapping("/tree")
    public ResponseEntity<List<TestSuiteDTO>> getSuitesTree(@RequestParam UUID projectId) {
        System.out.println("==== getSuitesTree projectId: " + projectId);
        return ResponseEntity.ok(suiteService.getSuitesTree(projectId));
    }

    /** Повернути дерево: тільки кореневі + всі діти */
    @GetMapping
    public ResponseEntity<List<TestSuiteDTO>> getAllSuitesTree() {
        List<TestSuiteDTO> tree = suiteService.getAllSuitesAsTree();
        return ResponseEntity.ok(tree);
    }

    /** Повернути плоский список усіх сьютів */
    @GetMapping("/flat")
    public ResponseEntity<List<TestSuiteDTO>> getAllSuitesFlat() {
        List<TestSuiteDTO> list = suiteService.getAllSuites();
        return ResponseEntity.ok(list);
    }

    /** Повернути один сьют за UUID */
    @GetMapping("/{id}")
    public ResponseEntity<TestSuiteDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(suiteService.getById(id));
    }

    /** Створити новий сьют; може містити parentId */
    @PostMapping
    public ResponseEntity<TestSuiteDTO> createSuite(@RequestBody TestSuiteDTO dto) {
        TestSuiteDTO created = suiteService.createSuite(dto);
        return ResponseEntity.ok(created);
    }

    /** Оновити існуючий сьют */
    @PutMapping("/{id}")
    public ResponseEntity<TestSuiteDTO> updateSuite(
            @PathVariable UUID id,
            @RequestBody TestSuiteDTO dto
    ) {
        TestSuiteDTO updated = suiteService.updateSuite(id, dto);
        return ResponseEntity.ok(updated);
    }

    /** Видалити сьют (cascade children) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuite(@PathVariable UUID id) {
        suiteService.deleteSuite(id);
        return ResponseEntity.noContent().build();
    }
}
