package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TestStepRepository extends JpaRepository<TestStep, UUID> {
    List<TestStep> findByTestCase_IdOrderByOrderIndexAsc(UUID testCaseId);
}
