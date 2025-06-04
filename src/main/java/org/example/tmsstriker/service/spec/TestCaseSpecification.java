// TestCaseSpecification.java
package org.example.tmsstriker.service.spec;

import org.example.tmsstriker.entity.TestCase;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TestCaseSpecification {
    public static Specification<TestCase> belongsToSuite(UUID suiteId) {
        return (root, query, cb) -> cb.equal(root.get("testSuite").get("id"), suiteId);
    }

    public static Specification<TestCase> containsTextInAnyField(String text) {
        return (root, query, cb) -> {
            if (text == null || text.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("preconditions")), pattern),
                    cb.like(cb.lower(root.get("priority")), pattern),
                    cb.like(cb.lower(root.get("tags")), pattern),
                    cb.like(cb.lower(root.get("state")), pattern),
                    cb.like(cb.lower(root.get("owner")), pattern),
                    cb.like(cb.lower(root.get("type")), pattern),
                    cb.like(cb.lower(root.get("automationStatus")), pattern),
                    cb.like(cb.lower(root.get("component")), pattern),
                    cb.like(cb.lower(root.get("useCase")), pattern),
                    cb.like(cb.lower(root.get("requirement")), pattern)
                    // Не додавай тут steps!
            );
        };
    }
}
