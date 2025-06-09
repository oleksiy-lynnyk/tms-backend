package org.example.tmsstriker;

import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.service.CodeGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CodeGenerationParameterizedTest {

    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @Autowired
    private ProjectRepository projectRepository;

    private final Set<UUID> createdProjectIds = new HashSet<>();

    @ParameterizedTest(name = "EntityType={0}, Prefix={1}")
    @CsvSource({
            "test_case, TC-",
            "test_suite, TS-",
            "test_run, TR-"
    })
    @Transactional
    public void shouldGenerateUniqueCodesInParallel(String entityType, String prefix) throws Exception {
        // 1. Створення проекту
        Project project = new Project();
        project.setName("Test Project " + entityType + " " + UUID.randomUUID());
        project.setDescription("Test");
        project.setCode("P-" + UUID.randomUUID().toString().substring(0, 8));
        project = projectRepository.save(project);

        createdProjectIds.add(project.getId()); // щоб потім видалити

        // 2. Генерація кодів
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Set<String> codes = ConcurrentHashMap.newKeySet();
        List<Future<?>> futures = new ArrayList<>();

        UUID projectId = project.getId();
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                String code = codeGeneratorService.generateNextCode(entityType, projectId, prefix);
                codes.add(code);
            }));
        }

        for (Future<?> f : futures) f.get();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(codes)
                .hasSize(threadCount)
                .withFailMessage("Duplicate codes detected for " + entityType);
    }

    //@AfterEach
    //@Transactional
    //public void cleanupProjects() {
    //projectRepository.deleteAllById(createdProjectIds);
    //}
}
