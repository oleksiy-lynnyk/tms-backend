// TestRunDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;
import java.time.Instant;

/** DTO для запуску тестів */
@Data
public class TestRunDTO {
    private UUID id;
    private UUID projectId;
    private String code;
    private String name;
    private String description;
    private String status;
    private Instant startedAt;
    private Instant completedAt;
    private UserShortDTO assignedTo; // <--- Додаємо це поле
}