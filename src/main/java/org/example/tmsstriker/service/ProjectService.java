package org.example.tmsstriker.service;

import org.example.tmsstriker.dto.ProjectDTO;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.ProjectRepository;
import org.example.tmsstriker.repository.TestCaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository repo;
    private final TestCaseRepository testCaseRepository; // ← додати тут!
    private final ModelMapper mapper;

    public ProjectService(ProjectRepository repo, TestCaseRepository testCaseRepository, ModelMapper mapper) {
        this.repo = repo;
        this.testCaseRepository = testCaseRepository;
        this.mapper = mapper;
    }

    public List<ProjectDTO> findAll() {
        return repo.findAll().stream()
                .map(e -> {
                    ProjectDTO dto = mapper.map(e, ProjectDTO.class);
                    // Додаємо підрахунок кейсів для кожного проекту:
                    dto.setTestCaseCount(testCaseRepository.countByProjectId(e.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public ProjectDTO findById(UUID id) {
        return repo.findById(id)
                .map(e -> {
                    ProjectDTO dto = mapper.map(e, ProjectDTO.class);
                    dto.setTestCaseCount(testCaseRepository.countByProjectId(e.getId()));
                    return dto;
                })
                .orElseThrow(() -> new ApiException("Project not found: " + id, HttpStatus.NOT_FOUND));
    }




    public ProjectDTO create(ProjectDTO dto) {
        // Якщо код не передано з фронту — генеруємо автоматично (PR-1, PR-2, ...)
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            dto.setCode(generateNextProjectCode());
        } else if (repo.findByCode(dto.getCode()).isPresent()) {
            throw new ApiException("Project code already exists: " + dto.getCode(), HttpStatus.CONFLICT);
        }
        Project e = mapper.map(dto, Project.class);
        Project saved = repo.save(e);
        ProjectDTO result = mapper.map(saved, ProjectDTO.class);
        result.setTestCaseCount(testCaseRepository.countByProjectId(saved.getId()));
        return result;
    }

    public ProjectDTO update(UUID id, ProjectDTO dto) {
        Project e = repo.findById(id)
                .orElseThrow(() -> new ApiException("Project not found: " + id, HttpStatus.NOT_FOUND));
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        // Оновлення code, якщо треба — розкоментувати:
        // if (dto.getCode() != null && !dto.getCode().equals(e.getCode())) {
        //     if (repo.findByCode(dto.getCode()).isPresent()) {
        //         throw new ApiException("Project code already exists: " + dto.getCode());
        //     }
        //     e.setCode(dto.getCode());
        // }
        Project updated = repo.save(e);
        ProjectDTO result = mapper.map(updated, ProjectDTO.class);
        result.setTestCaseCount(testCaseRepository.countByProjectId(updated.getId()));
        return result;
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public Page<ProjectDTO> getPagedProjects(String search, Pageable pageable) {
        Page<Project> page;
        if (search != null && !search.isBlank()) {
            page = repo.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = repo.findAll(pageable);
        }
        return page.map(entity -> {
            ProjectDTO dto = mapper.map(entity, ProjectDTO.class);
            dto.setTestCaseCount(testCaseRepository.countByProjectId(entity.getId()));
            return dto;
        });
    }



    // --- Генерація унікального коду проекту (PR-1, PR-2, ...)
    private String generateNextProjectCode() {
        // Шукай найбільший номер серед існуючих code
        List<Project> projects = repo.findAll();
        int max = projects.stream()
                .map(Project::getCode)
                .filter(code -> code != null && code.startsWith("PR-"))
                .map(code -> {
                    try {
                        return Integer.parseInt(code.replace("PR-", ""));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);
        return "PR-" + (max + 1);
    }
}
