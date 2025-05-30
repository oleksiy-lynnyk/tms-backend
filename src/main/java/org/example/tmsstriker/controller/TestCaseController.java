// TestCaseController.java
package org.example.tmsstriker.controller;

import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.dto.BulkTestCaseRequestDTO;
import org.example.tmsstriker.dto.ImportResultDto;
import org.example.tmsstriker.service.TestCaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "http://localhost:3000")
public class TestCaseController {
    private final TestCaseService service;

    public TestCaseController(TestCaseService service) {
        this.service = service;
    }

    @GetMapping("/suite/{suiteId}")
    public ResponseEntity<Page<TestCaseDTO>> getBySuite(
            @PathVariable UUID suiteId,
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable
    ) {
        Page<TestCaseDTO> page = service.getCases(suiteId, search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<TestCaseDTO> create(@RequestBody TestCaseDTO dto) {
        return ResponseEntity.ok(service.createTestCase(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCaseDTO> update(
            @PathVariable UUID id,
            @RequestBody TestCaseDTO dto
    ) {
        return ResponseEntity.ok(service.updateTestCase(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> bulk(@RequestBody BulkTestCaseRequestDTO req) {
        service.bulkUpdate(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<ImportResultDto> importCsv(
            @RequestParam UUID suiteId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        ImportResultDto result = service.importFromCsv(suiteId, file.getInputStream());
        return ResponseEntity.ok(result);
    }
}
