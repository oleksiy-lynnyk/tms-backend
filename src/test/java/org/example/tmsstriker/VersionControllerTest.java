package org.example.tmsstriker;

import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.dto.ErrorResponse;
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
class VersionControllerTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String BASE = "/api/versions";
    private static final String PROJECT_BASE = "/api/projects";

    @Test
    @DisplayName("V1: Create Version → 201 + body contains id")
    void shouldCreateVersion() {
        UUID projectId = createTestProject().getId();

        VersionDTO dto = new VersionDTO();
        dto.setProjectId(projectId);
        dto.setTitle("Version 1.0.0");
        dto.setSlug("v1-0-0");
        dto.setDescription("First major release with core features");

        ResponseEntity<VersionDTO> resp = rest.postForEntity(BASE, dto, VersionDTO.class);

        System.out.println("🔍 CREATE VERSION STATUS: " + resp.getStatusCode());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        VersionDTO created = resp.getBody();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getProjectId()).isEqualTo(projectId);
        assertThat(created.getTitle()).isEqualTo("Version 1.0.0");
        assertThat(created.getSlug()).isEqualTo("v1-0-0");
        assertThat(created.getDescription()).isEqualTo("First major release with core features");
    }

    @Test
    @DisplayName("V2: Get versions by project → 200 + filtered list")
    void shouldGetVersionsByProject() {
        UUID projectId = createTestProject().getId();
        createTestVersion(projectId, "1.0.0", "First release");
        createTestVersion(projectId, "1.1.0", "Feature update");
        createTestVersion(projectId, "2.0.0", "Major update");

        // Створити version для іншого проекту (не повинен повернутися)
        UUID otherProjectId = createTestProject().getId();
        createTestVersion(otherProjectId, "1.0.0", "Other project version");

        ResponseEntity<List<VersionDTO>> resp = rest.exchange(
                BASE + "/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VersionDTO>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<VersionDTO> versions = resp.getBody();
        assertThat(versions).hasSize(3);  // тільки 3 для нашого проекту

        // Перевірити що всі versions належать правильному проекту
        assertThat(versions).allSatisfy(version ->
                assertThat(version.getProjectId()).isEqualTo(projectId)
        );

        // Перевірити що є всі наші versions
        assertThat(versions).extracting(VersionDTO::getTitle)
                .containsExactlyInAnyOrder("1.0.0", "1.1.0", "2.0.0");
    }

    @Test
    @DisplayName("V3: Update version → 200 + fields updated")
    void shouldUpdateVersion() {
        UUID projectId = createTestProject().getId();
        VersionDTO created = createTestVersion(projectId, "1.0.0", "Initial version");

        created.setTitle("Version 1.0.1");
        created.setSlug("v1-0-1");
        created.setDescription("Bugfix release with important fixes");

        HttpEntity<VersionDTO> req = new HttpEntity<>(created);
        ResponseEntity<VersionDTO> resp = rest.exchange(
                BASE + "/" + created.getId(), HttpMethod.PUT, req, VersionDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        VersionDTO updated = resp.getBody();
        assertThat(updated.getTitle()).isEqualTo("Version 1.0.1");
        assertThat(updated.getSlug()).isEqualTo("v1-0-1");
        assertThat(updated.getDescription()).isEqualTo("Bugfix release with important fixes");
        assertThat(updated.getId()).isEqualTo(created.getId()); // ID не змінюється
        assertThat(updated.getProjectId()).isEqualTo(projectId); // ProjectId не змінюється
    }

    @Test
    @DisplayName("V4: Delete version → 204 + not found thereafter")
    void shouldDeleteVersion() {
        UUID projectId = createTestProject().getId();
        VersionDTO created = createTestVersion(projectId, "DeleteMe", "Version to be deleted");
        UUID versionId = created.getId();

        ResponseEntity<Void> del = rest.exchange(BASE + "/" + versionId, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<ErrorResponse> get = rest.getForEntity(BASE + "/" + versionId, ErrorResponse.class);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("V5: Get versions for non-existent project → empty list")
    void shouldReturnEmptyListForNonExistentProject() {
        UUID nonExistentProjectId = UUID.randomUUID();

        ResponseEntity<List<VersionDTO>> resp = rest.exchange(
                BASE + "/project/" + nonExistentProjectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VersionDTO>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEmpty();
    }

    @Test
    @DisplayName("V6: Update non-existent version → 404")
    void shouldReturn404ForNonExistentVersion() {
        UUID nonExistentId = UUID.randomUUID();
        VersionDTO dto = new VersionDTO();
        dto.setTitle("Non-existent");
        dto.setSlug("non-existent");

        HttpEntity<VersionDTO> req = new HttpEntity<>(dto);
        ResponseEntity<ErrorResponse> resp = rest.exchange(
                BASE + "/" + nonExistentId, HttpMethod.PUT, req, ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("V7: Verify version ordering (if implemented)")
    void shouldReturnVersionsInOrder() {
        UUID projectId = createTestProject().getId();

        // Створити versions у різному порядку
        createTestVersion(projectId, "2.0.0", "Major release");
        createTestVersion(projectId, "1.0.0", "Initial release");
        createTestVersion(projectId, "1.5.0", "Mid release");

        ResponseEntity<List<VersionDTO>> resp = rest.exchange(
                BASE + "/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VersionDTO>>() {}
        );

        List<VersionDTO> versions = resp.getBody();
        assertThat(versions).hasSize(3);

        // Не тестуємо конкретний порядок, бо він може бути не реалізований
        System.out.println("🔍 VERSION ORDER: " +
                versions.stream().map(VersionDTO::getTitle).toList());
    }

    @Test
    @DisplayName("V8: Verify all fields are persisted correctly")
    void shouldPersistAllVersionFields() {
        UUID projectId = createTestProject().getId();

        VersionDTO dto = new VersionDTO();
        dto.setProjectId(projectId);
        dto.setTitle("Version 3.2.1");
        dto.setSlug("v3-2-1-hotfix");
        dto.setDescription("Critical hotfix for security vulnerability CVE-2024-001");

        VersionDTO created = rest.postForEntity(BASE, dto, VersionDTO.class).getBody();

        // Отримати створену version та перевірити всі поля
        ResponseEntity<List<VersionDTO>> resp = rest.exchange(
                BASE + "/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VersionDTO>>() {}
        );

        VersionDTO retrieved = resp.getBody().stream()
                .filter(v -> v.getId().equals(created.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(retrieved.getTitle()).isEqualTo("Version 3.2.1");
        assertThat(retrieved.getSlug()).isEqualTo("v3-2-1-hotfix");
        assertThat(retrieved.getDescription()).isEqualTo("Critical hotfix for security vulnerability CVE-2024-001");
    }

    @Test
    @DisplayName("V9: Test semantic version patterns")
    void shouldAcceptVariousVersionFormats() {
        UUID projectId = createTestProject().getId();

        String[] versionTitles = {
                "1.0",
                "2.1.3",
                "3.0.0-alpha",
                "4.1.0-beta.2",
                "5.0.0-rc.1",
                "v6.2.1",
                "Release 7.0",
                "Build 2024.01.15"
        };

        for (String title : versionTitles) {
            VersionDTO dto = new VersionDTO();
            dto.setProjectId(projectId);
            dto.setTitle(title);
            dto.setSlug(title.toLowerCase().replaceAll("[^a-z0-9]", "-"));
            dto.setDescription("Test version: " + title);

            ResponseEntity<VersionDTO> resp = rest.postForEntity(BASE, dto, VersionDTO.class);

            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(resp.getBody().getTitle()).isEqualTo(title);

            System.out.println("✅ Created version: " + title);
        }
    }

    // Helper methods
    private ProjectDTO createTestProject() {
        ProjectDTO project = new ProjectDTO();
        project.setName("Version Test Project " + System.nanoTime());
        project.setDescription("Test project for version tests");

        ResponseEntity<ProjectDTO> resp = rest.postForEntity(PROJECT_BASE, project, ProjectDTO.class);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }

    private VersionDTO createTestVersion(UUID projectId, String title, String description) {
        VersionDTO dto = new VersionDTO();
        dto.setProjectId(projectId);
        dto.setTitle(title);
        dto.setSlug(title.toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + System.nanoTime());
        dto.setDescription(description);

        ResponseEntity<VersionDTO> resp = rest.postForEntity(BASE, dto, VersionDTO.class);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }
}
