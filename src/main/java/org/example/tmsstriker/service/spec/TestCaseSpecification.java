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
            if (text == null || text.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("steps")), pattern)
            );
        };
    }
}