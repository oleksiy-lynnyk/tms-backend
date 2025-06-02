package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class TestStep {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", columnDefinition = "uuid", nullable = false)
    private TestCase testCase;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(columnDefinition = "text")
    private String action;

    @Column(columnDefinition = "text")
    private String expectedResult;
}


