// TestCaseInstanceRepository.java
package org.example.tmsstriker.repository;

import org.example.tmsstriker.entity.TestCaseInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TestCaseInstanceRepository extends JpaRepository<TestCaseInstance, UUID> {
    List<TestCaseInstance> findByTestRunId(UUID testRunId);
}
