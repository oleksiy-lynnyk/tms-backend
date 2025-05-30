// TestStepResultRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestStepResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TestStepResultRepository extends JpaRepository<TestStepResult, UUID> {
    List<TestStepResult> findByTestCaseInstanceId(UUID testCaseInstanceId);
}