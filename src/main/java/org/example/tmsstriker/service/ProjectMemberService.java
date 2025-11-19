package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.AddProjectMemberRequest;
import org.example.tmsstriker.dto.ProjectMemberDTO;
import org.example.tmsstriker.dto.UpdateProjectMemberRoleRequest;
import org.example.tmsstriker.entity.AppUser;
import org.example.tmsstriker.entity.Project;
import org.example.tmsstriker.entity.ProjectMember;
import org.example.tmsstriker.entity.ProjectRole;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.AppUserRepository;
import org.example.tmsstriker.repository.ProjectMemberRepository;
import org.example.tmsstriker.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository userRepository;

    /**
     * Додати користувача до проекту
     */
    @Transactional
    public ProjectMemberDTO addMember(UUID projectId, AddProjectMemberRequest request) {
        // Перевірка існування проекту
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        // Перевірка існування користувача
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        // Перевірка, чи користувач вже є членом проекту
        if (memberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new ApiException("User is already a member of this project", HttpStatus.CONFLICT);
        }

        // Створення нового члена проекту
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(request.getRole());

        ProjectMember saved = memberRepository.save(member);
        return toDto(saved);
    }

    /**
     * Оновити роль користувача в проекті
     */
    @Transactional
    public ProjectMemberDTO updateMemberRole(UUID projectId, UUID userId, UpdateProjectMemberRoleRequest request) {
        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ApiException("Member not found in this project", HttpStatus.NOT_FOUND));

        member.setRole(request.getRole());
        ProjectMember updated = memberRepository.save(member);
        return toDto(updated);
    }

    /**
     * Видалити користувача з проекту
     */
    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        if (!memberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ApiException("Member not found in this project", HttpStatus.NOT_FOUND);
        }
        memberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    /**
     * Отримати всіх членів проекту
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getProjectMembers(UUID projectId) {
        // Перевірка існування проекту
        if (!projectRepository.existsById(projectId)) {
            throw new ApiException("Project not found", HttpStatus.NOT_FOUND);
        }

        return memberRepository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Отримати всі проекти користувача
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getUserProjects(UUID userId) {
        // Перевірка існування користувача
        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }

        return memberRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Перевірити, чи має користувач доступ до проекту
     */
    @Transactional(readOnly = true)
    public boolean hasAccess(UUID projectId, UUID userId) {
        return memberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    /**
     * Отримати роль користувача в проекті
     */
    @Transactional(readOnly = true)
    public ProjectRole getUserRole(UUID projectId, UUID userId) {
        return memberRepository.findUserRoleInProject(projectId, userId)
                .orElseThrow(() -> new ApiException("User is not a member of this project", HttpStatus.FORBIDDEN));
    }

    /**
     * Перевірити, чи є користувач адміністратором проекту
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(UUID projectId, UUID userId) {
        return memberRepository.isUserAdmin(projectId, userId);
    }

    /**
     * Конвертація Entity в DTO
     */
    private ProjectMemberDTO toDto(ProjectMember member) {
        ProjectMemberDTO dto = new ProjectMemberDTO();
        dto.setId(member.getId());
        dto.setProjectId(member.getProject().getId());
        dto.setProjectCode(member.getProject().getCode());
        dto.setProjectName(member.getProject().getName());
        dto.setUserId(member.getUser().getId());
        dto.setUsername(member.getUser().getUsername());
        dto.setUserFullName(member.getUser().getFullName());
        dto.setUserEmail(member.getUser().getEmail());
        dto.setRole(member.getRole());
        return dto;
    }
}