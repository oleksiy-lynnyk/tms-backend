// ExecutionCommandDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ExecutionCommandDTO {
    private String command; // "start", "pause", "resume", "stop", "run_case"
    private String comment; // опціонально
    private String payload; // JSON-рядок для специфічних команд
}