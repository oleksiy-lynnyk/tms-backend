package org.example.tmsstriker;

import jakarta.transaction.Transactional;
import org.example.tmsstriker.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TestCaseApiTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String SUITES = "/api/testsuites";
    private static final String CASES  = "/api/cases";

    private UUID createSuite(UUID projectId) {
        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setName("Suite for cases");
        dto.setProjectId(projectId);
        return rest.postForEntity(SUITES, dto, TestSuiteDTO.class)
                .getBody().getId();
    }

    private UUID createProjectAndSuite() {
        // Використайте вже наявні тести з ProjectApiTest
        ProjectDTO p = rest.postForEntity("/api/projects",
                makeProject("case-proj"), ProjectDTO.class).getBody();
        return createSuite(p.getId());
    }

    @Test @DisplayName("C1: Create TestCase → 201 + body contains id/code")
    void shouldCreateCase() {
        UUID suiteId = createProjectAndSuite();
        TestCaseDTO dto = new TestCaseDTO();
        dto.setTitle("Case 1");
        dto.setSuiteId(suiteId);

        // Створити кроки через сеттери
        TestStepDTO step1 = new TestStepDTO();
        step1.setOrderIndex(0);
        step1.setAction("Action A");
        step1.setExpectedResult("Result A");

        TestStepDTO step2 = new TestStepDTO();
        step2.setOrderIndex(1);
        step2.setAction("Action B");
        step2.setExpectedResult("Result B");

        dto.setSteps(List.of(step1, step2));

        ResponseEntity<TestCaseDTO> resp =
                rest.postForEntity(CASES, dto, TestCaseDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TestCaseDTO body = resp.getBody();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getCode()).startsWith("TC-");
        assertThat(body.getTitle()).isEqualTo("Case 1");
        assertThat(body.getSteps()).hasSize(2)
                .extracting(TestStepDTO::getAction)
                .containsExactly("Action A", "Action B");
    }

    @Test @DisplayName("C2: Get TestCase by ID → 200 + matches created")
    void shouldGetCaseById() {
        UUID suiteId = createProjectAndSuite();
        TestCaseDTO created = rest.postForEntity(CASES,
                makeCase("GetMe", suiteId), TestCaseDTO.class).getBody();
        UUID id = created.getId();

        ResponseEntity<TestCaseDTO> resp =
                rest.getForEntity(CASES + "/" + id, TestCaseDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(id);
        assertThat(resp.getBody().getTitle()).isEqualTo("GetMe");
    }

    @Test @DisplayName("C3: Update TestCase → 200 + fields updated")
    void shouldUpdateCase() {
        UUID suiteId = createProjectAndSuite();
        TestCaseDTO dto = rest.postForEntity(CASES,
                makeCase("UpdMe", suiteId), TestCaseDTO.class).getBody();
        dto.setTitle("UpdDone");
        dto.setDescription("new desc");
        HttpEntity<TestCaseDTO> req = new HttpEntity<>(dto);

        ResponseEntity<TestCaseDTO> resp =
                rest.exchange(CASES + "/" + dto.getId(),
                        HttpMethod.PUT, req, TestCaseDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getTitle()).isEqualTo("UpdDone");
        assertThat(resp.getBody().getDescription()).isEqualTo("new desc");
    }

    @Test @DisplayName("C4: Delete TestCase → 204 + not found thereafter")
    void shouldDeleteCase() {
        UUID suiteId = createProjectAndSuite();
        UUID id = rest.postForEntity(CASES,
                        makeCase("DelMe", suiteId), TestCaseDTO.class)
                .getBody().getId();

        ResponseEntity<Void> del = rest.exchange(
                CASES + "/" + id, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<TestCaseDTO> get = rest.getForEntity(
                CASES + "/" + id, TestCaseDTO.class);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test @DisplayName("C6: Duplicate title → 409 Conflict")
    void shouldConflictOnDuplicateTitle() {
        UUID suiteId = createProjectAndSuite();
        TestCaseDTO dto1 = makeCase("dup-title", suiteId);
        rest.postForEntity(CASES, dto1, TestCaseDTO.class);

        TestCaseDTO dto2 = makeCase("dup-title", suiteId);
        ResponseEntity<ErrorResponse> resp =
                rest.postForEntity(CASES, dto2, ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().message()).contains("Duplicate Test Case title");
    }

    // …інші тести (404, missing suiteId тощо)…

    // хелпери
    private static TestCaseDTO makeCase(String title, UUID suiteId) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setTitle(title);
        dto.setSuiteId(suiteId);
        return dto;
    }

    private static ProjectDTO makeProject(String suffix) {
        ProjectDTO p = new ProjectDTO();
        p.setName("proj-" + suffix + "-" + System.nanoTime());
        p.setDescription("desc");
        return p;
    }
}
