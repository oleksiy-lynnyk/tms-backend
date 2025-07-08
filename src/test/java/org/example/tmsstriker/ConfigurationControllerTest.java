package org.example.tmsstriker;

import org.example.tmsstriker.dto.ConfigurationDTO;
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
class ConfigurationControllerTest {

    @Autowired
    private TestRestTemplate rest;

    private static final String BASE = "/api/configurations";
    private static final String PROJECT_BASE = "/api/projects";

    @Test
    @DisplayName("CF1: Create Configuration → 201 + body contains id")
    void shouldCreateConfiguration() {
        UUID projectId = createTestProject().getId();

        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setProjectId(projectId);
        dto.setName("Test Config");
        dto.setSlug("test-config");
        dto.setDescription("Test configuration description");
        dto.setOs("Windows 11");
        dto.setBrowser("Chrome 120");
        dto.setDevice("Desktop");

        ResponseEntity<ConfigurationDTO> resp = rest.postForEntity(BASE, dto, ConfigurationDTO.class);

        System.out.println("🔍 CREATE CONFIG STATUS: " + resp.getStatusCode());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ConfigurationDTO created = resp.getBody();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getProjectId()).isEqualTo(projectId);
        assertThat(created.getName()).isEqualTo("Test Config");
        assertThat(created.getOs()).isEqualTo("Windows 11");
        assertThat(created.getBrowser()).isEqualTo("Chrome 120");
        assertThat(created.getDevice()).isEqualTo("Desktop");
    }

    @Test
    @DisplayName("CF2: Get configurations by project → 200 + filtered list")
    void shouldGetConfigurationsByProject() {
        UUID projectId = createTestProject().getId();
        createTestConfiguration(projectId, "Config1", "Windows", "Chrome");
        createTestConfiguration(projectId, "Config2", "Linux", "Firefox");

        // Створити конфігурацію для іншого проекту (не повинна повернутися)
        UUID otherProjectId = createTestProject().getId();
        createTestConfiguration(otherProjectId, "OtherConfig", "MacOS", "Safari");

        ResponseEntity<List<ConfigurationDTO>> resp = rest.exchange(
                BASE + "/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ConfigurationDTO>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ConfigurationDTO> configs = resp.getBody();
        assertThat(configs).hasSize(2);  // тільки 2 для нашого проекту

        // Перевірити що всі конфігурації належать правильному проекту
        assertThat(configs).allSatisfy(config ->
                assertThat(config.getProjectId()).isEqualTo(projectId)
        );

        // Перевірити що є обидві наші конфігурації
        assertThat(configs).extracting(ConfigurationDTO::getOs)
                .containsExactlyInAnyOrder("Windows", "Linux");
    }

    @Test
    @DisplayName("CF3: Update configuration → 200 + fields updated")
    void shouldUpdateConfiguration() {
        UUID projectId = createTestProject().getId();
        ConfigurationDTO created = createTestConfiguration(projectId, "UpdateMe", "Windows", "Chrome");

        created.setName("Updated Config Name");
        created.setBrowser("Firefox 115");
        created.setDescription("Updated description");

        HttpEntity<ConfigurationDTO> req = new HttpEntity<>(created);
        ResponseEntity<ConfigurationDTO> resp = rest.exchange(
                BASE + "/" + created.getId(), HttpMethod.PUT, req, ConfigurationDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ConfigurationDTO updated = resp.getBody();
        assertThat(updated.getName()).isEqualTo("Updated Config Name");
        assertThat(updated.getBrowser()).isEqualTo("Firefox 115");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getId()).isEqualTo(created.getId()); // ID не змінюється
        assertThat(updated.getProjectId()).isEqualTo(projectId); // ProjectId не змінюється
    }

    @Test
    @DisplayName("CF4: Delete configuration → 204 + not found thereafter")
    void shouldDeleteConfiguration() {
        UUID projectId = createTestProject().getId();
        ConfigurationDTO created = createTestConfiguration(projectId, "DeleteMe", "Windows", "Chrome");
        UUID configId = created.getId();

        ResponseEntity<Void> del = rest.exchange(BASE + "/" + configId, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<ErrorResponse> get = rest.getForEntity(BASE + "/" + configId, ErrorResponse.class);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("CF5: Create configuration with non-existent project → 404")
    void shouldRejectConfigurationWithInvalidProject() {
        UUID nonExistentProjectId = UUID.randomUUID();

        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setProjectId(nonExistentProjectId);
        dto.setName("Invalid Config");

        ResponseEntity<ErrorResponse> resp = rest.postForEntity(BASE, dto, ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("🔍 INVALID PROJECT RESPONSE: " + resp.getBody());
    }

    @Test
    @DisplayName("CF6: Get configurations for non-existent project → empty list")
    void shouldReturnEmptyListForNonExistentProject() {
        UUID nonExistentProjectId = UUID.randomUUID();

        ResponseEntity<List<ConfigurationDTO>> resp = rest.exchange(
                BASE + "/project/" + nonExistentProjectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ConfigurationDTO>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEmpty();
    }

    @Test
    @DisplayName("CF7: Update non-existent configuration → 404")
    void shouldReturn404ForNonExistentConfiguration() {
        UUID nonExistentId = UUID.randomUUID();
        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setName("Non-existent");

        HttpEntity<ConfigurationDTO> req = new HttpEntity<>(dto);
        ResponseEntity<ErrorResponse> resp = rest.exchange(
                BASE + "/" + nonExistentId, HttpMethod.PUT, req, ErrorResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("CF8: Verify all fields are persisted correctly")
    void shouldPersistAllConfigurationFields() {
        UUID projectId = createTestProject().getId();

        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setProjectId(projectId);
        dto.setName("Full Config Test");
        dto.setSlug("full-config-test");
        dto.setDescription("Complete configuration with all fields");
        dto.setOs("Ubuntu 22.04 LTS");
        dto.setBrowser("Chrome 120.0.6099");
        dto.setDevice("MacBook Pro M2");

        ConfigurationDTO created = rest.postForEntity(BASE, dto, ConfigurationDTO.class).getBody();

        // Отримати створену конфігурацію та перевірити всі поля
        ResponseEntity<List<ConfigurationDTO>> resp = rest.exchange(
                BASE + "/project/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ConfigurationDTO>>() {}
        );

        ConfigurationDTO retrieved = resp.getBody().stream()
                .filter(c -> c.getId().equals(created.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(retrieved.getName()).isEqualTo("Full Config Test");
        assertThat(retrieved.getSlug()).isEqualTo("full-config-test");
        assertThat(retrieved.getDescription()).isEqualTo("Complete configuration with all fields");
        assertThat(retrieved.getOs()).isEqualTo("Ubuntu 22.04 LTS");
        assertThat(retrieved.getBrowser()).isEqualTo("Chrome 120.0.6099");
        assertThat(retrieved.getDevice()).isEqualTo("MacBook Pro M2");
    }

    // Helper methods
    private ProjectDTO createTestProject() {
        ProjectDTO project = new ProjectDTO();
        project.setName("Config Test Project " + System.nanoTime());
        project.setDescription("Test project for configuration tests");

        ResponseEntity<ProjectDTO> resp = rest.postForEntity(PROJECT_BASE, project, ProjectDTO.class);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }

    private ConfigurationDTO createTestConfiguration(UUID projectId, String name, String os, String browser) {
        ConfigurationDTO dto = new ConfigurationDTO();
        dto.setProjectId(projectId);
        dto.setName(name + System.nanoTime());
        dto.setSlug(name.toLowerCase().replace(" ", "-"));
        dto.setDescription("Test configuration: " + name);
        dto.setOs(os);
        dto.setBrowser(browser);
        dto.setDevice("Test Device");

        ResponseEntity<ConfigurationDTO> resp = rest.postForEntity(BASE, dto, ConfigurationDTO.class);
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody();
    }
}
