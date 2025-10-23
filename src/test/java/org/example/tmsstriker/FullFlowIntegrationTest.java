package org.example.tmsstriker;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.dto.TestCaseDTO;
import org.example.tmsstriker.dto.TestRunDTO;
import org.example.tmsstriker.dto.TestSuiteDTO;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.example.tmsstriker.repository.TestRunRepository;
import org.example.tmsstriker.repository.TestSuiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class FullFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private TestSuiteRepository suiteRepository;
    @Autowired private TestCaseRepository caseRepository;
    @Autowired private TestRunRepository runRepository;

// Замінити фіксовані назви на унікальні:

    @Test
    void createProjectSuiteCaseRunAndAssignSuccessfully() {
        String timestamp = String.valueOf(System.nanoTime());

        // 1) Create Project - УНІКАЛЬНА НАЗВА
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Integration Test Project " + timestamp);  // ✅ Унікально
        projectDTO.setDescription("Integration test description");

        ResponseEntity<ProjectDTO> projectResponse =
                rest.postForEntity("/api/projects", projectDTO, ProjectDTO.class);
        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        UUID projectId = projectResponse.getBody().getId();
        assertNotNull(projectId);

        // 2) Create Test Suite - УНІКАЛЬНА НАЗВА
        TestSuiteDTO suiteDTO = new TestSuiteDTO();
        suiteDTO.setName("Integration Suite " + timestamp);  // ✅ Унікально
        suiteDTO.setDescription("Suite description");
        suiteDTO.setProjectId(projectId);

        ResponseEntity<TestSuiteDTO> suiteResponse =
                rest.postForEntity("/api/test-suites", suiteDTO, TestSuiteDTO.class);
        assertEquals(HttpStatus.CREATED, suiteResponse.getStatusCode());
        UUID suiteId = suiteResponse.getBody().getId();
        assertNotNull(suiteId);

        // 3) Create Test Case - УНІКАЛЬНА НАЗВА
        TestCaseDTO caseDTO = new TestCaseDTO();
        caseDTO.setTitle("Integration Test Case " + timestamp);  // ✅ Унікально
        caseDTO.setDescription("Case description");
        caseDTO.setProjectId(projectId);
        caseDTO.setSuiteId(suiteId);

        ResponseEntity<TestCaseDTO> caseResponse =
                rest.postForEntity("/api/test-cases", caseDTO, TestCaseDTO.class);
        assertEquals(HttpStatus.CREATED, caseResponse.getStatusCode());
        UUID caseId = caseResponse.getBody().getId();
        assertNotNull(caseId);

        // 4) Create Test Run - УНІКАЛЬНА НАЗВА
        TestRunDTO runDTO = new TestRunDTO();
        runDTO.setName("Integration Test Run " + timestamp);  // ✅ Унікально
        runDTO.setProjectId(projectId);
        runDTO.setTestCaseIds(List.of(caseId));

        ResponseEntity<TestRunDTO> runResponse =
                rest.postForEntity("/api/test-runs", runDTO, TestRunDTO.class);
        assertEquals(HttpStatus.CREATED, runResponse.getStatusCode());
        UUID runId = runResponse.getBody().getId();
        assertNotNull(runId);
        assertTrue(runResponse.getBody().getTestCaseIds().contains(caseId));

        // 5) GET and verify Test Run
        ResponseEntity<TestRunDTO> getResponse =
                rest.getForEntity("/api/test-runs/" + runId, TestRunDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertTrue(getResponse.getBody().getTestCaseIds().contains(caseId));
    }
}
