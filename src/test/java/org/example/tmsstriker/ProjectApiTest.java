package org.example.tmsstriker;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.example.tmsstriker.dto.ErrorResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ProjectApiTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String BASE = "/api/projects";

    @Test
    @DisplayName("P1: Create Project → 201 + body contains id/code")
    void shouldCreateProject() {

        // СТАЛО (унікальна назва):
        ProjectDTO dto = make("P1-create");  // ✅ Використовуємо хелпер

        ResponseEntity<ProjectDTO> resp =
                rest.postForEntity(BASE, dto, ProjectDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProjectDTO body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getCode()).startsWith("PR-");
        assertThat(body.getName()).startsWith("proj-P1-create-");  // Оновити assertion
    }

    @Test
    @DisplayName("P2: Get Project by ID → 200 + matches created")
    void shouldGetProjectById() {
        // arrange
        ProjectDTO created = rest.postForEntity(BASE, make("P2"), ProjectDTO.class).getBody();
        UUID id = created.getId();

        // act
        ResponseEntity<ProjectDTO> resp =
                rest.getForEntity(BASE + "/" + id, ProjectDTO.class);

        // assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(id);
        assertThat(resp.getBody().getName()).isEqualTo(created.getName());
    }

    @Test
    @DisplayName("P3: Update Project → 200 + fields updated")
    void shouldUpdateProject() {
        // arrange
        ProjectDTO p = rest.postForEntity(BASE, make("P3"), ProjectDTO.class).getBody();
        UUID id = p.getId();
        p.setName("P3-upd");
        p.setDescription("upd-desc");

        HttpEntity<ProjectDTO> req = new HttpEntity<>(p);
        // act
        ResponseEntity<ProjectDTO> resp =
                rest.exchange(BASE + "/" + id, HttpMethod.PUT, req, ProjectDTO.class);

        // assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getName()).isEqualTo("P3-upd");
        assertThat(resp.getBody().getDescription()).isEqualTo("upd-desc");
    }

    @Test
    @DisplayName("P4: Delete Project → 204 + not found thereafter")
    void shouldDeleteProject() {
        // arrange
        UUID id = rest.postForEntity(BASE, make("P4"), ProjectDTO.class)
                .getBody().getId();

        // act
        ResponseEntity<Void> del =
                rest.exchange(BASE + "/" + id, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<ProjectDTO> get =
                rest.getForEntity(BASE + "/" + id, ProjectDTO.class);

        // assert
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("P5: Delete Project cascades suites, cases, runs")
    void shouldCascadeDeleteEntireTree() {
        // arrange: create full tree under one project
        ProjectDTO proj = rest.postForEntity(BASE, make("P5"), ProjectDTO.class).getBody();
        UUID projectId = proj.getId();

        // create suite
        TestSuiteDTO suite = new TestSuiteDTO();
        suite.setName("cascade-suite");
        suite.setProjectId(projectId);
        UUID suiteId = rest.postForEntity("/api/test-suites", suite, TestSuiteDTO.class)
                .getBody().getId();

        // create case
        TestCaseDTO c = new TestCaseDTO();
        c.setTitle("cascade-case");
        c.setProjectId(projectId);
        c.setSuiteId(suiteId);
        UUID caseId = rest.postForEntity("/api/test-cases", c, TestCaseDTO.class)
                .getBody().getId();

        // create run
        TestRunDTO run = new TestRunDTO();
        run.setName("cascade-run");
        run.setProjectId(projectId);
        run.setTestCaseIds(List.of(caseId));
        UUID runId = rest.postForEntity("/api/test-runs", run, TestRunDTO.class)
                .getBody().getId();

        // act: delete project
        rest.exchange(BASE + "/" + projectId, HttpMethod.DELETE, null, Void.class);

        // assert: all gone
        assertThat(rest.getForEntity(BASE + "/" + projectId, ProjectDTO.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(rest.getForEntity("/api/test-suites/" + suiteId, TestSuiteDTO.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(rest.getForEntity("/api/test-cases/" + caseId, TestCaseDTO.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(rest.getForEntity("/api/test-runs/" + runId, TestRunDTO.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("P6: Create duplicate → 409 Conflict")
    void shouldConflictOnDuplicateName() {
        // БУЛО (різні назви через nanoTime):
        // rest.postForEntity(BASE, make("dup"), ProjectDTO.class);
        // ProjectDTO dup = make("dup");

        // СТАЛО (однакові назви):
        String duplicateName = "Duplicate Project Test";

        // Створити перший проект
        ProjectDTO first = new ProjectDTO();
        first.setName(duplicateName);
        first.setDescription("First project");
        rest.postForEntity(BASE, first, ProjectDTO.class);

        // Спробувати створити другий з тією ж назвою
        ProjectDTO duplicate = new ProjectDTO();
        duplicate.setName(duplicateName);  // ✅ Така ж назва!
        duplicate.setDescription("Duplicate project");

        ResponseEntity<ErrorResponse> resp = rest.postForEntity(BASE, duplicate, ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().message()).contains("Duplicate");
    }

    @Test
    @DisplayName("P7: 404 on non-existent ID")
    void should404ForBadId() {
        UUID bad = UUID.randomUUID();
        assertThat(rest.getForEntity(BASE + "/" + bad, ProjectDTO.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(rest.exchange(BASE + "/" + bad, HttpMethod.DELETE, null, Void.class)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // helper
    private static ProjectDTO make(String suffix) {
        ProjectDTO p = new ProjectDTO();
        p.setName("proj-" + suffix + "-" + System.nanoTime());
        p.setDescription("desc");
        return p;
    }
}

