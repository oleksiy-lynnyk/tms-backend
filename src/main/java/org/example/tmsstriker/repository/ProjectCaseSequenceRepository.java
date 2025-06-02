package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.ProjectCaseSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectCaseSequenceRepository extends JpaRepository<ProjectCaseSequence, UUID> {}
