package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.ProjectMember;
import org.example.tmsstriker.entity.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    /**
     * Знайти всіх членів проекту
     */
    List<ProjectMember> findByProjectId(UUID projectId);

    /**
     * Знайти всі проекти користувача
     */
    List<ProjectMember> findByUserId(UUID userId);

    /**
     * Знайти конкретного члена проекту
     */
    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);

    /**
     * Перевірити, чи є користувач членом проекту
     */
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);

    /**
     * Видалити користувача з проекту
     */
    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);

    /**
     * Знайти всіх членів проекту з певною роллю
     */
    List<ProjectMember> findByProjectIdAndRole(UUID projectId, ProjectRole role);

    /**
     * Перевірити, чи є користувач адміністратором проекту
     */
    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END " +
           "FROM ProjectMember pm " +
           "WHERE pm.project.id = :projectId AND pm.user.id = :userId AND pm.role = 'ADMIN'")
    boolean isUserAdmin(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    /**
     * Отримати роль користувача в проекті
     */
    @Query("SELECT pm.role FROM ProjectMember pm " +
           "WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    Optional<ProjectRole> findUserRoleInProject(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    /**
     * Знайти всі ID проектів, до яких користувач має доступ
     */
    @Query("SELECT pm.project.id FROM ProjectMember pm WHERE pm.user.id = :userId")
    List<UUID> findProjectIdsByUserId(@Param("userId") UUID userId);
}