package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VersionRepository extends JpaRepository<Version, UUID> {
    List<Version> findAllByProject_Id(UUID projectId);
}

