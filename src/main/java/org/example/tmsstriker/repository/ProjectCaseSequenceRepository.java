package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.ProjectCaseSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface ProjectCaseSequenceRepository extends JpaRepository<ProjectCaseSequence, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ProjectCaseSequence s WHERE s.projectId = :projectId")
    Optional<ProjectCaseSequence> findByProjectIdForUpdate(@Param("projectId") UUID projectId);

    Optional<ProjectCaseSequence> findByProjectId(UUID projectId);
}



