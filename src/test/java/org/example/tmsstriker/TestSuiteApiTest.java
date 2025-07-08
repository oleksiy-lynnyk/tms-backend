package org.example.tmsstriker;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TestSuiteApiTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String BASE = "/api/testsuites";
    private static final String PROJECT_BASE = "/api/projects";

    @Test
    @DisplayName("TS1: Create Suite → 201 + body contains id/code")
    void shouldCreateSuite() {
        // arrange: need a project first
        TestRestTemplate r = rest;
        var proj = r.postForEntity(PROJECT_BASE, makeProjectDTO("TS1"), ProjectDTO.class).getBody();
        UUID projectId = proj.getId();

        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setName("Suite API Test");
        dto.setDescription("Desc");
        dto.setProjectId(projectId);

        ResponseEntity<TestSuiteDTO> resp = r.postForEntity(BASE, dto, TestSuiteDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TestSuiteDTO body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getCode()).startsWith("TS-");
        assertThat(body.getProjectId()).isEqualTo(projectId);
    }

    @Test
    @DisplayName("TS2: Get Suite by ID → 200 + matches created")
    void shouldGetSuiteById() {
        var r = rest;
        var proj = r.postForEntity(PROJECT_BASE, makeProjectDTO("TS2"), ProjectDTO.class).getBody();
        UUID pid = proj.getId();
        var created = r.postForEntity(BASE, makeSuiteDTO(pid, "TS2"), TestSuiteDTO.class).getBody();
        UUID id = created.getId();

        ResponseEntity<TestSuiteDTO> resp = r.getForEntity(BASE + "/" + id, TestSuiteDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(id);
        assertThat(resp.getBody().getName()).isEqualTo(created.getName());
    }

    @Test
    @DisplayName("TS3: Update Suite → 200 + fields updated")
    void shouldUpdateSuite() {
        var r = rest;
        var proj = r.postForEntity(PROJECT_BASE, makeProjectDTO("TS3"), ProjectDTO.class).getBody();
        UUID pid = proj.getId();
        var dto = makeSuiteDTO(pid, "TS3");
        var created = r.postForEntity(BASE, dto, TestSuiteDTO.class).getBody();
        UUID id = created.getId();

        created.setName("TS3-upd");
        created.setDescription("upd-desc");
        HttpEntity<TestSuiteDTO> req = new HttpEntity<>(created);

        ResponseEntity<TestSuiteDTO> resp = r.exchange(BASE + "/" + id, HttpMethod.PUT, req, TestSuiteDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getName()).isEqualTo("TS3-upd");
        assertThat(resp.getBody().getDescription()).isEqualTo("upd-desc");
    }

    @Test
    @DisplayName("TS4: Delete Suite → 204 + not found thereafter")
    void shouldDeleteSuite() {
        var r = rest;
        var proj = r.postForEntity(PROJECT_BASE, makeProjectDTO("TS4"), ProjectDTO.class).getBody();
        UUID pid = proj.getId();
        var created = r.postForEntity(BASE, makeSuiteDTO(pid, "TS4"), TestSuiteDTO.class).getBody();
        UUID id = created.getId();

        ResponseEntity<Void> del = r.exchange(BASE + "/" + id, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<TestSuiteDTO> get = r.getForEntity(BASE + "/" + id, TestSuiteDTO.class);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("TS5: Duplicate name → 409 Conflict")
    void shouldConflictOnDuplicateName() {
        var r = rest;
        var proj = r.postForEntity(PROJECT_BASE, makeProjectDTO("TS5"), ProjectDTO.class).getBody();
        UUID pid = proj.getId();
        var dto = makeSuiteDTO(pid, "dup");
        r.postForEntity(BASE, dto, TestSuiteDTO.class);

        ResponseEntity<ErrorResponse> resp = r.postForEntity(BASE, dto, ErrorResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().message()).contains("Duplicate");
    }

    private static ProjectDTO makeProjectDTO(String suffix) {
        ProjectDTO p = new ProjectDTO();
        p.setName("proj-" + suffix + "-" + System.nanoTime());
        p.setDescription("desc");
        return p;
    }

    private static TestSuiteDTO makeSuiteDTO(UUID projectId, String suffix) {
        TestSuiteDTO s = new TestSuiteDTO();
        s.setName("suite-" + suffix + "-" + System.nanoTime());
        s.setDescription("desc");
        s.setProjectId(projectId);
        return s;
    }
}

