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
class TestRunCaseResultControllerTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String PROJECT_BASE = "/api/projects";
    private static final String SUITE_BASE = "/api/test-suites";
    private static final String CASE_BASE = "/api/test-cases";
    private static final String RUN_BASE = "/api/test-runs";
    private static final String USER_BASE = "/api/app-users";

    @Test
    @DisplayName("RES1: Set test case result → 200 + result saved")
    void shouldSetTestCaseResult() {
        TestRunSetup setup = createTestRunWithCase();
        AppUserFullDTO user = createTestUser();

        TestRunCaseResultDTO result = new TestRunCaseResultDTO();
        result.setStatus("Passed");
        result.setComment("Test executed successfully with all assertions passing");
        result.setExecutedBy(user.getId());

        ResponseEntity<TestRunCaseResultDTO> resp = rest.postForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                result, TestRunCaseResultDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunCaseResultDTO created = resp.getBody();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTestRunId()).isEqualTo(setup.runId);
        assertThat(created.getTestCaseId()).isEqualTo(setup.caseId);
        assertThat(created.getStatus()).isEqualTo("Passed");
        assertThat(created.getComment()).isEqualTo("Test executed successfully with all assertions passing");
        assertThat(created.getExecutedBy()).isEqualTo(user.getId());
        assertThat(created.getExecutedAt()).isNotNull();
    }

    @Test
    @DisplayName("RES2: Update existing result → 200 + result updated")
    void shouldUpdateExistingResult() {
        TestRunSetup setup = createTestRunWithCase();
        AppUserFullDTO user = createTestUser();

        // Створити початковий результат
        TestRunCaseResultDTO initialResult = new TestRunCaseResultDTO();
        initialResult.setStatus("Failed");
        initialResult.setComment("Initial failure");
        initialResult.setExecutedBy(user.getId());

        rest.postForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                initialResult, TestRunCaseResultDTO.class);

        // Оновити результат
        TestRunCaseResultDTO updatedResult = new TestRunCaseResultDTO();
        updatedResult.setStatus("Passed");
        updatedResult.setComment("Fixed and now passing");
        updatedResult.setExecutedBy(user.getId());

        ResponseEntity<TestRunCaseResultDTO> resp = rest.postForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                updatedResult, TestRunCaseResultDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunCaseResultDTO result = resp.getBody();
        assertThat(result.getStatus()).isEqualTo("Passed");
        assertThat(result.getComment()).isEqualTo("Fixed and now passing");
    }

    @Test
    @DisplayName("RES3: Get all results for test run → 200 + list")
    void shouldGetAllResultsForTestRun() {
        TestRunSetup setup = createTestRunWithMultipleCases();
        AppUserFullDTO user = createTestUser();

        // Встановити результати для кількох тест-кейсів
        setTestCaseResult(setup.runId, setup.case1Id, "Passed", "First test passed", user.getId());
        setTestCaseResult(setup.runId, setup.case2Id, "Failed", "Second test failed", user.getId());
        setTestCaseResult(setup.runId, setup.case3Id, "Skipped", "Third test skipped", user.getId());

        ResponseEntity<List<TestRunCaseResultDTO>> resp = rest.exchange(
                "/api/test-runs/" + setup.runId + "/cases",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TestRunCaseResultDTO>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TestRunCaseResultDTO> results = resp.getBody();
        assertThat(results).hasSize(3);

        // Перевірити що всі результати належать правильному test run
        assertThat(results).allSatisfy(result ->
                assertThat(result.getTestRunId()).isEqualTo(setup.runId)
        );

        // Перевірити статуси
        assertThat(results).extracting(TestRunCaseResultDTO::getStatus)
                .containsExactlyInAnyOrder("Passed", "Failed", "Skipped");
    }

    @Test
    @DisplayName("RES4: Get specific test case result → 200 + result details")
    void shouldGetSpecificTestCaseResult() {
        TestRunSetup setup = createTestRunWithCase();
        AppUserFullDTO user = createTestUser();

        // Встановити результат
        setTestCaseResult(setup.runId, setup.caseId, "Blocked", "Test blocked by environment issue", user.getId());

        ResponseEntity<TestRunCaseResultDTO> resp = rest.getForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                TestRunCaseResultDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunCaseResultDTO result = resp.getBody();
        assertThat(result.getTestRunId()).isEqualTo(setup.runId);
        assertThat(result.getTestCaseId()).isEqualTo(setup.caseId);
        assertThat(result.getStatus()).isEqualTo("Blocked");
        assertThat(result.getComment()).isEqualTo("Test blocked by environment issue");
        assertThat(result.getExecutedBy()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("RES5: Get result for non-existent combination → 404")
    void shouldReturn404ForNonExistentResult() {
        TestRunSetup setup = createTestRunWithCase();

        // Не встановлювати результат, одразу спробувати отримати
        ResponseEntity<ErrorResponse> resp = rest.getForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("RES6: Various result statuses")
    void shouldAcceptVariousResultStatuses() {
        TestRunSetup setup = createTestRunWithMultipleCases();
        AppUserFullDTO user = createTestUser();

        String[] statuses = {"Passed", "Failed", "Blocked", "Skipped", "Untested", "Retest"};
        UUID[] caseIds = {setup.case1Id, setup.case2Id, setup.case3Id};

        // Використати різні статуси для різних кейсів
        for (int i = 0; i < Math.min(statuses.length, caseIds.length); i++) {
            TestRunCaseResultDTO result = new TestRunCaseResultDTO();
            result.setStatus(statuses[i]);
            result.setComment("Test with status: " + statuses[i]);
            result.setExecutedBy(user.getId());

            ResponseEntity<TestRunCaseResultDTO> resp = rest.postForEntity(
                    "/api/test-runs/" + setup.runId + "/cases/" + caseIds[i] + "/result",
                    result, TestRunCaseResultDTO.class);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody().getStatus()).isEqualTo(statuses[i]);

            System.out.println("✅ Set result status: " + statuses[i]);
        }
    }

    @Test
    @DisplayName("RES7: Result without executor → accepted")
    void shouldAcceptResultWithoutExecutor() {
        TestRunSetup setup = createTestRunWithCase();

        TestRunCaseResultDTO result = new TestRunCaseResultDTO();
        result.setStatus("Automated");
        result.setComment("Executed by automation system");
        // executedBy не встановлюємо

        ResponseEntity<TestRunCaseResultDTO> resp = rest.postForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + setup.caseId + "/result",
                result, TestRunCaseResultDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        TestRunCaseResultDTO created = resp.getBody();
        assertThat(created.getStatus()).isEqualTo("Automated");
        assertThat(created.getExecutedBy()).isNull();
        assertThat(created.getExecutedByName()).isNull();
    }

    @Test
    @DisplayName("RES8: Invalid test run or case IDs → 404 or 400")
    void shouldRejectInvalidIds() {
        UUID invalidRunId = UUID.randomUUID();
        UUID invalidCaseId = UUID.randomUUID();

        TestRunCaseResultDTO result = new TestRunCaseResultDTO();
        result.setStatus("Passed");
        result.setComment("This should fail");

        // Невалідний run ID
        ResponseEntity<ErrorResponse> resp1 = rest.postForEntity(
                "/api/test-runs/" + invalidRunId + "/cases/" + UUID.randomUUID() + "/result",
                result, ErrorResponse.class);

        // Невалідний case ID з існуючим run
        TestRunSetup setup = createTestRunWithCase();
        ResponseEntity<ErrorResponse> resp2 = rest.postForEntity(
                "/api/test-runs/" + setup.runId + "/cases/" + invalidCaseId + "/result",
                result, ErrorResponse.class);

        // Обидві повинні повернути помилку
        assertThat(resp1.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST);
        assertThat(resp2.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST);

        System.out.println("🔍 Invalid run ID response: " + resp1.getStatusCode());
        System.out.println("🔍 Invalid case ID response: " + resp2.getStatusCode());
    }

    // Helper methods and data classes
    private static class TestRunSetup {
        UUID projectId;
        UUID suiteId;
        UUID caseId;
        UUID case1Id;
        UUID case2Id;
        UUID case3Id;
        UUID runId;
    }

    private TestRunSetup createTestRunWithCase() {
        TestRunSetup setup = new TestRunSetup();

        // Створити проект
        ProjectDTO project = createTestProject();
        setup.projectId = project.getId();

        // Створити тест-сьют
        TestSuiteDTO suite = createTestSuite(setup.projectId);
        setup.suiteId = suite.getId();

        // Створити тест-кейс
        TestCaseDTO testCase = createTestCase(setup.suiteId, "Result Test Case");
        setup.caseId = testCase.getId();

        // Створити тест-ран з кейсом
        TestRunDTO testRun = new TestRunDTO();
        testRun.setName("Result Test Run " + System.nanoTime());
        testRun.setProjectId(setup.projectId);
        testRun.setTestCaseIds(List.of(setup.caseId));

        TestRunDTO createdRun = rest.postForEntity(RUN_BASE, testRun, TestRunDTO.class).getBody();
        setup.runId = createdRun.getId();

        return setup;
    }

    private TestRunSetup createTestRunWithMultipleCases() {
        TestRunSetup setup = new TestRunSetup();

        // Створити проект
        ProjectDTO project = createTestProject();
        setup.projectId = project.getId();

        // Створити тест-сьют
        TestSuiteDTO suite = createTestSuite(setup.projectId);
        setup.suiteId = suite.getId();

        // Створити кілька тест-кейсів
        TestCaseDTO case1 = createTestCase(setup.suiteId, "Test Case 1");
        TestCaseDTO case2 = createTestCase(setup.suiteId, "Test Case 2");
        TestCaseDTO case3 = createTestCase(setup.suiteId, "Test Case 3");

        setup.case1Id = case1.getId();
        setup.case2Id = case2.getId();
        setup.case3Id = case3.getId();

        // Створити тест-ран з усіма кейсами
        TestRunDTO testRun = new TestRunDTO();
        testRun.setName("Multi-Case Test Run " + System.nanoTime());
        testRun.setProjectId(setup.projectId);
        testRun.setTestCaseIds(List.of(setup.case1Id, setup.case2Id, setup.case3Id));

        TestRunDTO createdRun = rest.postForEntity(RUN_BASE, testRun, TestRunDTO.class).getBody();
        setup.runId = createdRun.getId();

        return setup;
    }

    private ProjectDTO createTestProject() {
        ProjectDTO project = new ProjectDTO();
        project.setName("Result Test Project " + System.nanoTime());
        project.setDescription("Test project for result tests");

        ResponseEntity<ProjectDTO> resp = rest.postForEntity(PROJECT_BASE, project, ProjectDTO.class);
        return resp.getBody();
    }

    private TestSuiteDTO createTestSuite(UUID projectId) {
        TestSuiteDTO suite = new TestSuiteDTO();
        suite.setName("Result Test Suite " + System.nanoTime());
        suite.setProjectId(projectId);

        ResponseEntity<TestSuiteDTO> resp = rest.postForEntity(SUITE_BASE, suite, TestSuiteDTO.class);
        return resp.getBody();
    }

    private TestCaseDTO createTestCase(UUID suiteId, String title) {
        TestCaseDTO testCase = new TestCaseDTO();
        testCase.setTitle(title + " " + System.nanoTime());
        testCase.setSuiteId(suiteId);
        testCase.setDescription("Test case for result testing");

        ResponseEntity<TestCaseDTO> resp = rest.postForEntity(CASE_BASE, testCase, TestCaseDTO.class);
        return resp.getBody();
    }

    private AppUserFullDTO createTestUser() {
        AppUserFullDTO user = new AppUserFullDTO();
        user.setUsername("testuser" + System.nanoTime());
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");

        ResponseEntity<AppUserFullDTO> resp = rest.postForEntity(USER_BASE, user, AppUserFullDTO.class);
        return resp.getBody();
    }

    private void setTestCaseResult(UUID runId, UUID caseId, String status, String comment, UUID executedBy) {
        TestRunCaseResultDTO result = new TestRunCaseResultDTO();
        result.setStatus(status);
        result.setComment(comment);
        result.setExecutedBy(executedBy);

        rest.postForEntity(
                "/api/test-runs/" + runId + "/cases/" + caseId + "/result",
                result, TestRunCaseResultDTO.class);
    }
}