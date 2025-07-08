package org.example.tmsstriker;

import org.example.tmsstriker.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TestRunControllerTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String BASE = "/api/testruns";
    private static final String PROJECT_BASE = "/api/projects";
    private static final String SUITE_BASE = "/api/testsuites";
    private static final String CASE_BASE = "/api/cases";

    @Test
    @DisplayName("TR1: Create TestRun → 201 + body contains id")
    void shouldCreateTestRun() {
        UUID projectId = createTestProject().getId();

        TestRunDTO dto = new TestRunDTO();
        dto.setName("Smoke Test Run");
        dto.setProjectId(projectId);
        dto.setDescription("Basic smoke testing for new build");
        dto.setStatus("Created");

        ResponseEntity<TestRunDTO> resp = rest.postForEntity(BASE, dto, TestRunDTO.class);

        System.out.println("🔍 CREATE TESTRUN STATUS: " + resp.getStatusCode());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        TestRunDTO created = resp.getBody();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getProjectId()).isEqualTo(projectId);
        assertThat(created.getName()).isEqualTo("Smoke Test Run");
        assertThat(created.getDescription()).isEqualTo("Basic smoke testing for new build");
        assertThat(created.getStartedAt()).isNotNull();  // повинно встановитися автоматично
    }

    @Test
    @DisplayName("TR2: Get test runs by project with pagination → 200 + Page")
    void shouldGetTestRunsByProjectPaginated() {
        UUID projectId = createTestProject().getId();
        createTestRun(projectId, "Run1");
        createTestRun(projectId, "Run2");
        createTestRun(projectId, "Run3");
        createTestRun(projectId, "Run4");
        createTestRun(projectId, "Run5");

        ResponseEntity<String> resp = rest.getForEntity(
                BASE + "/project/" + projectId + "?page=0&size=3", String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        String body = resp.getBody();
        assertThat(body).contains("\"content\":");
        assertThat(body).contains("\"totalElements\":");
        assertThat(body).contains("\"size\":");

        System.out.println("🔍 TESTRUN PAGINATION: " + body);
    }

    @Test
    @DisplayName("TR3: Get TestRun by ID → 200 + full details")
    void shouldGetTestRunById() {
        UUID projectId = createTestProject().getId();
        TestRunDTO created = createTestRun(projectId, "GetById");
        UUID runId = created.getId();

        ResponseEntity<TestRunDTO> resp = rest.getForEntity(BASE + "/" + runId, TestRunDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        TestRunDTO retrieved = resp.getBody();
        assertThat(retrieved.getId()).isEqualTo(runId);
        assertThat(retrieved.getName()).isEqualTo(created.getName());
        assertThat(retrieved.getProjectId()).isEqualTo(projectId);
    }

    @Test
    @DisplayName("TR4: Update TestRun → 200 + fields updated")
    void shouldUpdateTestRun() {
        UUID projectId = createTestProject().getId();
        TestRunDTO created = createTestRun(projectId, "UpdateMe");

        created.setName("Updated Test Run");
        created.setDescription("Updated description");
        created.setStatus("In Progress");

        HttpEntity<TestRunDTO> req = new HttpEntity<>(created);
        ResponseEntity<TestRunDTO> resp = rest.exchange(
                BASE + "/" + created.getId(), HttpMethod.PUT, req, TestRunDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunDTO updated = resp.getBody();
        assertThat(updated.getName()).isEqualTo("Updated Test Run");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getStatus()).isEqualTo("In Progress");
        assertThat(updated.getId()).isEqualTo(created.getId());
    }

    @Test
    @DisplayName("TR5: Delete TestRun → 204 + not found thereafter")
    void shouldDeleteTestRun() {
        UUID projectId = createTestProject().getId();
        TestRunDTO created = createTestRun(projectId, "DeleteMe");
        UUID runId = created.getId();

        ResponseEntity<Void> del = rest.exchange(BASE + "/" + runId, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<ErrorResponse> get = rest.getForEntity(BASE + "/" + runId, ErrorResponse.class);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("TR6: Execute command → 200 + status updated")
    void shouldExecuteCommand() {
        UUID projectId = createTestProject().getId();
        TestRunDTO testRun = createTestRun(projectId, "ExecuteMe");

        ExecutionCommandDTO command = new ExecutionCommandDTO();
        command.setCommand("start");
        command.setComment("Starting test execution");
        command.setPayload("{\"priority\": \"high\"}");

        HttpEntity<ExecutionCommandDTO> req = new HttpEntity<>(command);
        ResponseEntity<TestRunDTO> resp = rest.exchange(
                BASE + "/" + testRun.getId() + "/execute", HttpMethod.POST, req, TestRunDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunDTO result = resp.getBody();
        assertThat(result.getStatus()).isEqualTo("start");  // команда стає статусом
        assertThat(result.getDescription()).isEqualTo("Starting test execution");  // коментар стає описом
    }

    @Test
    @DisplayName("TR7: Complete test run → 200 + completedAt set")
    void shouldCompleteTestRun() {
        UUID projectId = createTestProject().getId();
        TestRunDTO testRun = createTestRun(projectId, "CompleteMe");

        ResponseEntity<Void> resp = rest.exchange(
                BASE + "/" + testRun.getId() + "/complete", HttpMethod.POST, null, Void.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Перевірити що статус змінився та completedAt встановлено
        TestRunDTO updated = rest.getForEntity(BASE + "/" + testRun.getId(), TestRunDTO.class).getBody();
        assertThat(updated.getStatus()).isEqualTo("Completed");
        assertThat(updated.getCompletedAt()).isNotNull();

        System.out.println("🔍 COMPLETED AT: " + updated.getCompletedAt());
    }

    @Test
    @DisplayName("TR8: Clone test run → 200 + new id + copied fields")
    void shouldCloneTestRun() {
        UUID projectId = createTestProject().getId();
        TestRunDTO original = createTestRun(projectId, "CloneMe");

        ResponseEntity<TestRunDTO> resp = rest.exchange(
                BASE + "/" + original.getId() + "/clone", HttpMethod.POST, null, TestRunDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunDTO cloned = resp.getBody();
        assertThat(cloned.getId()).isNotEqualTo(original.getId());
        assertThat(cloned.getName()).contains("Clone");
        assertThat(cloned.getProjectId()).isEqualTo(projectId);
        assertThat(cloned.getStatus()).isEqualTo("Created");  // новий статус
        assertThat(cloned.getStartedAt()).isNotNull();  // нова дата старту
        assertThat(cloned.getCompletedAt()).isNull();   // ще не завершено

        System.out.println("🔍 CLONED: " + cloned.getName() + " with ID: " + cloned.getId());
    }

    @Test
    @DisplayName("TR9: Create TestRun with test cases → cases attached")
    void shouldCreateTestRunWithTestCases() {
        // Створити проект та тест-кейси
        UUID projectId = createTestProject().getId();
        UUID suiteId = createTestSuite(projectId).getId();
        UUID case1Id = createTestCase(suiteId, "Test Case 1").getId();
        UUID case2Id = createTestCase(suiteId, "Test Case 2").getId();

        TestRunDTO dto = new TestRunDTO();
        dto.setName("Test Run with Cases");
        dto.setProjectId(projectId);
        dto.setTestCaseIds(List.of(case1Id, case2Id));

        ResponseEntity<TestRunDTO> resp = rest.postForEntity(BASE, dto, TestRunDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        TestRunDTO created = resp.getBody();
        assertThat(created.getTestCaseIds()).hasSize(2);
        assertThat(created.getTestCaseIds()).containsExactlyInAnyOrder(case1Id, case2Id);
    }

    @Test
    @DisplayName("TR10: Various execution commands")
    void shouldHandleVariousExecutionCommands() {
        UUID projectId = createTestProject().getId();
        TestRunDTO testRun = createTestRun(projectId, "CommandTest");

        String[] commands = {"start", "pause", "resume", "stop"};

        for (String cmd : commands) {
            ExecutionCommandDTO command = new ExecutionCommandDTO();
            command.setCommand(cmd);
            command.setComment("Executing: " + cmd);

            HttpEntity<ExecutionCommandDTO> req = new HttpEntity<>(command);
            ResponseEntity<TestRunDTO> resp = rest.exchange(
                    BASE + "/" + testRun.getId() + "/execute", HttpMethod.POST, req, TestRunDTO.class);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody().getStatus()).isEqualTo(cmd);

            System.out.println("✅ Executed command: " + cmd);
        }
    }

    @Test
    @DisplayName("TR11: 404 for non-existent test run operations")
    void shouldReturn404ForNonExistentTestRun() {
        UUID nonExistentId = UUID.randomUUID();

        // GET
        ResponseEntity<ErrorResponse> getResp = rest.getForEntity(BASE + "/" + nonExistentId, ErrorResponse.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // DELETE
        ResponseEntity<ErrorResponse> delResp = rest.exchange(BASE + "/" + nonExistentId, HttpMethod.DELETE, null, ErrorResponse.class);
        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // EXECUTE
        ExecutionCommandDTO command = new ExecutionCommandDTO();
        command.setCommand("start");
        HttpEntity<ExecutionCommandDTO> req = new HttpEntity<>(command);
        ResponseEntity<ErrorResponse> execResp = rest.exchange(BASE + "/" + nonExistentId + "/execute", HttpMethod.POST, req, ErrorResponse.class);
        assertThat(execResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // COMPLETE
        ResponseEntity<ErrorResponse> completeResp = rest.exchange(BASE + "/" + nonExistentId + "/complete", HttpMethod.POST, null, ErrorResponse.class);
        assertThat(completeResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        //
    }

    private ProjectDTO createTestProject() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("Test Project " + System.nanoTime());
        dto.setDescription("Project for testing");
        return rest.postForEntity(PROJECT_BASE, dto, ProjectDTO.class).getBody();
    }

    private TestRunDTO createTestRun(UUID projectId, String name) {
        TestRunDTO dto = new TestRunDTO();
        dto.setName(name);
        dto.setProjectId(projectId);
        dto.setDescription("Test run for " + name);
        dto.setStatus("Created");
        return rest.postForEntity(BASE, dto, TestRunDTO.class).getBody();
    }

    private TestSuiteDTO createTestSuite(UUID projectId) {
        TestSuiteDTO dto = new TestSuiteDTO();
        dto.setName("Test Suite " + System.nanoTime());
        dto.setProjectId(projectId);
        dto.setDescription("Test suite for project");
        return rest.postForEntity(SUITE_BASE, dto, TestSuiteDTO.class).getBody();
    }

    private TestCaseDTO createTestCase(UUID suiteId, String title) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setTitle(title);
        dto.setSuiteId(suiteId);
        dto.setDescription("Test case: " + title);
        return rest.postForEntity(CASE_BASE, dto, TestCaseDTO.class).getBody();
    }
}