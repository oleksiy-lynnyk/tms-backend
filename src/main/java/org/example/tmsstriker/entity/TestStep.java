package org.example.tmsstriker.entity;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Data
@Entity
@Table(name = "test_step")
public class TestStep {

    @Id
    @GeneratedValue(generator = "UUID")  // ДОДАНО!
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column
    private String action;

    @Column
    private String expectedResult;

    @Column
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;
}