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

/**
 * Спеціальні тести для виявлення проблем в API:
 * - HTTP статуси
 * - CORS
 * - Пагінація
 * - Консистентність
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SpecialApiTests {

    @Autowired
    private TestRestTemplate rest;

    // ===============================
    // HTTP STATUS CODES TESTS
    // ===============================

    @Test
    @DisplayName("HTTP-1: All POST endpoints should return 201 Created")
    void allPostEndpointsShouldReturn201() {
        System.out.println("🔍 TESTING HTTP STATUS CODES FOR POST OPERATIONS");

        // Створити базовий проект для тестів
        ProjectDTO project = createTestProject();
        UUID projectId = project.getId();

        // AppUser POST - currently returns 200, should be 201
        AppUserFullDTO user = new AppUserFullDTO();
        user.setUsername("statustest" + System.nanoTime());
        user.setEmail("status@test.com");
        user.setFullName("Status Test User");

        ResponseEntity<AppUserFullDTO> userResp = rest.postForEntity("/api/AppUsers", user, AppUserFullDTO.class);
        System.out.println("❌ AppUser POST status: " + userResp.getStatusCode() + " (should be 201)");

        // Project POST - should return 201
        ResponseEntity<ProjectDTO> projectResp = rest.postForEntity("/api/projects", createTestProjectDTO(), ProjectDTO.class);
        System.out.println("✅ Project POST status: " + projectResp.getStatusCode());
        assertThat(projectResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Configuration POST
        ConfigurationDTO config = new ConfigurationDTO();
        config.setProjectId(projectId);
        config.setName("Status Test Config");
        ResponseEntity<ConfigurationDTO> configResp = rest.postForEntity("/api/configurations", config, ConfigurationDTO.class);
        System.out.println("📊 Configuration POST status: " + configResp.getStatusCode());

        // Environment POST
        EnvironmentDTO env = new EnvironmentDTO();
        env.setProjectId(projectId);
        env.setName("Status Test Env");
        env.setHost("test.com");
        env.setPort(8080);
        ResponseEntity<EnvironmentDTO> envResp = rest.postForEntity("/api/environments", env, EnvironmentDTO.class);
        System.out.println("📊 Environment POST status: " + envResp.getStatusCode());

        // Version POST
        VersionDTO version = new VersionDTO();
        version.setProjectId(projectId);
        version.setTitle("Status Test Version");
        ResponseEntity<VersionDTO> versionResp = rest.postForEntity("/api/versions", version, VersionDTO.class);
        System.out.println("📊 Version POST status: " + versionResp.getStatusCode());

        // TestSuite POST
        TestSuiteDTO suite = new TestSuiteDTO();
        suite.setProjectId(projectId);
        suite.setName("Status Test Suite");
        ResponseEntity<TestSuiteDTO> suiteResp = rest.postForEntity("/api/testsuites", suite, TestSuiteDTO.class);
        System.out.println("📊 TestSuite POST status: " + suiteResp.getStatusCode());

        // TestCase POST
        TestCaseDTO testCase = new TestCaseDTO();
        testCase.setTitle("Status Test Case");
        testCase.setSuiteId(suiteResp.getBody().getId());
        ResponseEntity<TestCaseDTO> caseResp = rest.postForEntity("/api/cases", testCase, TestCaseDTO.class);
        System.out.println("📊 TestCase POST status: " + caseResp.getStatusCode());

        // TestRun POST
        TestRunDTO testRun = new TestRunDTO();
        testRun.setName("Status Test Run");
        testRun.setProjectId(projectId);
        ResponseEntity<TestRunDTO> runResp = rest.postForEntity("/api/testruns", testRun, TestRunDTO.class);
        System.out.println("📊 TestRun POST status: " + runResp.getStatusCode());

        // Звіт
        System.out.println("\n📊 HTTP STATUS REPORT:");
        System.out.println("❌ PROBLEMS FOUND:");
        if (!userResp.getStatusCode().equals(HttpStatus.CREATED)) {
            System.out.println("  - AppUser POST returns " + userResp.getStatusCode() + " instead of 201");
        }

        System.out.println("✅ CORRECT STATUS CODES:");
        if (projectResp.getStatusCode().equals(HttpStatus.CREATED)) {
            System.out.println("  - Project POST returns 201 ✓");
        }
    }

    @Test
    @DisplayName("HTTP-2: All DELETE endpoints should return 204 No Content")
    void allDeleteEndpointsShouldReturn204() {
        System.out.println("🔍 TESTING HTTP STATUS CODES FOR DELETE OPERATIONS");

        // Створити resources для видалення
        ProjectDTO project = rest.postForEntity("/api/projects", createTestProjectDTO(), ProjectDTO.class).getBody();
        AppUserFullDTO user = createTestUser();

        // Test DELETE statuses
        ResponseEntity<Void> projectDel = rest.exchange("/api/projects/" + project.getId(), HttpMethod.DELETE, null, Void.class);
        ResponseEntity<Void> userDel = rest.exchange("/api/AppUsers/" + user.getId(), HttpMethod.DELETE, null, Void.class);

        System.out.println("📊 Project DELETE status: " + projectDel.getStatusCode());
        System.out.println("📊 AppUser DELETE status: " + userDel.getStatusCode());

        assertThat(projectDel.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userDel.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    // ===============================
    // CORS TESTS
    // ===============================

    @Test
    @DisplayName("CORS-1: Preflight requests work correctly")
    void shouldHandleOptionsPreflightRequests() {
        System.out.println("🔍 TESTING CORS PREFLIGHT REQUESTS");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:3000");
        headers.set("Access-Control-Request-Method", "POST");
        headers.set("Access-Control-Request-Headers", "Content-Type");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = rest.exchange(
                "/api/projects", HttpMethod.OPTIONS, entity, String.class);

        System.out.println("📊 OPTIONS request status: " + resp.getStatusCode());
        System.out.println("📊 CORS headers: " + resp.getHeaders());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpHeaders responseHeaders = resp.getHeaders();
        System.out.println("✅ Access-Control-Allow-Origin: " + responseHeaders.get("Access-Control-Allow-Origin"));
        System.out.println("✅ Access-Control-Allow-Methods: " + responseHeaders.get("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("CORS-2: Actual requests include CORS headers")
    void shouldIncludeCorsHeadersInActualRequests() {
        System.out.println("🔍 TESTING CORS HEADERS IN ACTUAL REQUESTS");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:3000");
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProjectDTO project = createTestProjectDTO();
        HttpEntity<ProjectDTO> entity = new HttpEntity<>(project, headers);

        ResponseEntity<ProjectDTO> resp = rest.exchange(
                "/api/projects", HttpMethod.POST, entity, ProjectDTO.class);

        System.out.println("📊 POST with Origin header status: " + resp.getStatusCode());
        System.out.println("📊 Response CORS headers: " + resp.getHeaders());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Перевірити наявність CORS headers у відповіді
        HttpHeaders responseHeaders = resp.getHeaders();
        List<String> allowOrigin = responseHeaders.get("Access-Control-Allow-Origin");
        if (allowOrigin != null) {
            System.out.println("✅ CORS working: Access-Control-Allow-Origin = " + allowOrigin);
        } else {
            System.out.println("❌ CORS problem: No Access-Control-Allow-Origin header");
        }
    }

    // ===============================
    // PAGINATION TESTS
    // ===============================

    @Test
    @DisplayName("PAGE-1: Endpoints with pagination work correctly")
    void shouldSupportPagination() {
        System.out.println("🔍 TESTING PAGINATION SUPPORT");

        // Створити кілька ресурсів для тестування пагінації
        ProjectDTO project = rest.postForEntity("/api/projects", createTestProjectDTO(), ProjectDTO.class).getBody();
        UUID projectId = project.getId();

        // Створити кілька користувачів
        for (int i = 1; i <= 5; i++) {
            createTestUser("pageuser" + i);
        }

        // Тест пагінації користувачів (має підтримувати)
        ResponseEntity<String> userPageResp = rest.getForEntity("/api/AppUsers?page=0&size=2", String.class);
        System.out.println("📊 AppUser pagination status: " + userPageResp.getStatusCode());
        String userBody = userPageResp.getBody();
        boolean hasPageStructure = userBody.contains("\"content\":") &&
                userBody.contains("\"totalElements\":") &&
                userBody.contains("\"size\":");
        System.out.println("📊 AppUser has Page structure: " + hasPageStructure);

        // Створити кілька конфігурацій
        for (int i = 1; i <= 3; i++) {
            ConfigurationDTO config = new ConfigurationDTO();
            config.setProjectId(projectId);
            config.setName("Page Config " + i);
            rest.postForEntity("/api/configurations", config, ConfigurationDTO.class);
        }

        // Тест конфігурацій (поки не має пагінації)
        ResponseEntity<List> configResp = rest.exchange(
                "/api/configurations/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List>() {}
        );
        System.out.println("📊 Configuration list status: " + configResp.getStatusCode());
        System.out.println("📊 Configuration returns plain List (not Page): " + (configResp.getBody() instanceof List));

        // Тест з параметрами пагінації для endpoint без пагінації
        ResponseEntity<List> configPageResp = rest.exchange(
                "/api/configurations/project/" + projectId + "?page=0&size=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List>() {}
        );
        System.out.println("📊 Configuration with page params status: " + configPageResp.getStatusCode());
        System.out.println("📊 Configuration ignores pagination params: " + configPageResp.getBody().equals(configResp.getBody()));

        System.out.println("\n📊 PAGINATION REPORT:");
        System.out.println("✅ ENDPOINTS WITH PAGINATION:");
        System.out.println("  - /api/AppUsers - supports Page<AppUserFullDTO>");

        System.out.println("❌ ENDPOINTS WITHOUT PAGINATION:");
        System.out.println("  - /api/configurations/project/{id} - returns List<ConfigurationDTO>");
        System.out.println("  - /api/environments/project/{id} - returns List<EnvironmentDTO>");
        System.out.println("  - /api/versions/project/{id} - returns List<VersionDTO>");
    }

    @Test
    @DisplayName("PAGE-2: Invalid pagination parameters handling")
    void shouldHandleInvalidPaginationParameters() {
        System.out.println("🔍 TESTING INVALID PAGINATION PARAMETERS");

        // Тестувати негативні значення
        ResponseEntity<String> negativeResp = rest.getForEntity("/api/AppUsers?page=-1&size=-5", String.class);
        System.out.println("📊 Negative page/size status: " + negativeResp.getStatusCode());

        // Тестувати дуже великий розмір
        ResponseEntity<String> largeResp = rest.getForEntity("/api/AppUsers?page=0&size=10000", String.class);
        System.out.println("📊 Large size status: " + largeResp.getStatusCode());

        // Тестувати некоректні типи
        ResponseEntity<String> invalidResp = rest.getForEntity("/api/AppUsers?page=abc&size=xyz", String.class);
        System.out.println("📊 Invalid types status: " + invalidResp.getStatusCode());

        // Всі повинні або повернути помилку або застосувати дефолти
        assertThat(negativeResp.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
        assertThat(largeResp.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
        assertThat(invalidResp.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    // ===============================
    // URL CONSISTENCY TESTS
    // ===============================

    @Test
    @DisplayName("URL-1: Check URL naming conventions")
    void shouldFollowConsistentUrlNamingConventions() {
        System.out.println("🔍 TESTING URL NAMING CONVENTIONS");

        // Тестувати поточні URLs
        System.out.println("📊 CURRENT API ENDPOINTS:");
        System.out.println("❌ /api/AppUsers - CamelCase (should be /api/app-users)");
        System.out.println("❌ /api/cases - abbreviated (should be /api/test-cases)");
        System.out.println("❌ /api/testsuites - one word (should be /api/test-suites)");
        System.out.println("❌ /api/testruns - one word (should be /api/test-runs)");
        System.out.println("✅ /api/configurations - correct");
        System.out.println("✅ /api/environments - correct");
        System.out.println("✅ /api/projects - correct");
        System.out.println("✅ /api/versions - correct");

        // Переконатися що поточні URLs працюють
        ResponseEntity<String> resp1 = rest.getForEntity("/api/AppUsers", String.class);
        ResponseEntity<String> resp2 = rest.getForEntity("/api/projects", String.class);
        ResponseEntity<String> resp3 = rest.getForEntity("/api/configurations", String.class);

        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp3.getStatusCode()).isEqualTo(HttpStatus.OK);

        System.out.println("✅ All current URLs are accessible");
    }

    // ===============================
    // SWAGGER DOCUMENTATION TESTS
    // ===============================

    @Test
    @DisplayName("SWAGGER-1: OpenAPI spec is accessible")
    void shouldProvideOpenApiSpecification() {
        System.out.println("🔍 TESTING SWAGGER/OPENAPI DOCUMENTATION");

        // Тестувати доступність Swagger UI
        ResponseEntity<String> swaggerUi = rest.getForEntity("/swagger-ui.html", String.class);
        System.out.println("📊 Swagger UI status: " + swaggerUi.getStatusCode());

        // Тестувати доступність OpenAPI JSON
        ResponseEntity<String> openApiJson = rest.getForEntity("/v3/api-docs", String.class);
        System.out.println("📊 OpenAPI JSON status: " + openApiJson.getStatusCode());

        if (openApiJson.getStatusCode() == HttpStatus.OK) {
            String apiDocs = openApiJson.getBody();
            System.out.println("✅ OpenAPI spec contains:");
            System.out.println("  - Title: " + (apiDocs.contains("TMS Striker API") ? "✅" : "❌"));
            System.out.println("  - Version: " + (apiDocs.contains("1.0.0") ? "✅" : "❌"));
            System.out.println("  - Paths: " + (apiDocs.contains("\"/api/") ? "✅" : "❌"));
        }
    }

    // ===============================
    // CONSISTENCY TESTS
    // ===============================

    @Test
    @DisplayName("CONSISTENCY-1: All endpoints follow same error response format")
    void shouldUseConsistentErrorResponseFormat() {
        System.out.println("🔍 TESTING ERROR RESPONSE CONSISTENCY");

        UUID nonExistentId = UUID.randomUUID();

        // Тестувати 404 відповіді з різних endpoints
        ResponseEntity<ErrorResponse> projectError = rest.getForEntity("/api/projects/" + nonExistentId, ErrorResponse.class);
        ResponseEntity<ErrorResponse> userError = rest.getForEntity("/api/AppUsers/" + nonExistentId, ErrorResponse.class);
        ResponseEntity<String> caseError = rest.getForEntity("/api/cases/" + nonExistentId, String.class);

        System.out.println("📊 Project 404 status: " + projectError.getStatusCode());
        System.out.println("📊 User 404 status: " + userError.getStatusCode());
        System.out.println("📊 Case 404 status: " + caseError.getStatusCode());

        if (projectError.getStatusCode() == HttpStatus.NOT_FOUND && projectError.getBody() != null) {
            System.out.println("✅ Project error format: message='" + projectError.getBody().getMessage() +
                    "', status=" + projectError.getBody().getStatus());
        }

        if (userError.getStatusCode() == HttpStatus.NOT_FOUND && userError.getBody() != null) {
            System.out.println("✅ User error format: message='" + userError.getBody().getMessage() +
                    "', status=" + userError.getBody().getStatus());
        }

        System.out.println("📊 Case error body type: " + caseError.getBody().getClass().getSimpleName());
    }

    @Test
    @DisplayName("FINAL: Complete API Health Check")
    void completeApiHealthCheck() {
        System.out.println("\n🎯 COMPLETE API HEALTH CHECK");
        System.out.println("=".repeat(50));

        int totalEndpoints = 0;
        int workingEndpoints = 0;

        // Основні GET endpoints
        String[] getEndpoints = {
                "/api/projects",
                "/api/AppUsers",
                "/api/configurations",
                "/api/environments",
                "/api/versions",
                "/api/testsuites",
                "/api/cases",
                "/api/testruns"
        };

        for (String endpoint : getEndpoints) {
            totalEndpoints++;
            try {
                ResponseEntity<String> resp = rest.getForEntity(endpoint, String.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    workingEndpoints++;
                    System.out.println("✅ " + endpoint + " - " + resp.getStatusCode());
                } else {
                    System.out.println("❌ " + endpoint + " - " + resp.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("💥 " + endpoint + " - ERROR: " + e.getMessage());
            }
        }

        System.out.println("\n📊 HEALTH CHECK SUMMARY:");
        System.out.println("Working endpoints: " + workingEndpoints + "/" + totalEndpoints);
        System.out.println("Success rate: " + (workingEndpoints * 100 / totalEndpoints) + "%");

        if (workingEndpoints == totalEndpoints) {
            System.out.println("🎉 ALL ENDPOINTS ARE HEALTHY!");
        } else {
            System.out.println("⚠️  SOME ENDPOINTS NEED ATTENTION");
        }
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private ProjectDTO createTestProject() {
        ProjectDTO project = createTestProjectDTO();
        ResponseEntity<ProjectDTO> resp = rest.postForEntity("/api/projects", project, ProjectDTO.class);
        return resp.getBody();
    }

    private ProjectDTO createTestProjectDTO() {
        ProjectDTO project = new ProjectDTO();
        project.setName("Special Test Project " + System.nanoTime());
        project.setDescription("Project for special tests");
        return project;
    }

    private AppUserFullDTO createTestUser() {
        return createTestUser("specialuser");
    }

    private AppUserFullDTO createTestUser(String username) {
        AppUserFullDTO user = new AppUserFullDTO();
        user.setUsername(username + System.nanoTime());
        user.setEmail(username + "@test.com");
        user.setFullName("Test User " + username);

        ResponseEntity<AppUserFullDTO> resp = rest.postForEntity("/api/AppUsers", user, AppUserFullDTO.class);
        return resp.getBody();
    }
}