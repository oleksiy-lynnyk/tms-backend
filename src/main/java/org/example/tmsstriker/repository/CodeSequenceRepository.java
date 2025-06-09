package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.CodeSequence;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface CodeSequenceRepository extends JpaRepository<CodeSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM CodeSequence cs WHERE cs.entityType = :entityType AND cs.projectId = :projectId")
    Optional<CodeSequence> findByEntityTypeAndProjectIdForUpdate(
            @Param("entityType") String entityType,
            @Param("projectId") UUID projectId
    );

}


