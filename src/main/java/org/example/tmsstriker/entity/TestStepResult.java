// TestStepResult.java
package org.example.tmsstriker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class TestStepResult {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "instance_id", columnDefinition = "uuid")
    private UUID testCaseInstanceId;

    private String result;
    private String message;
}