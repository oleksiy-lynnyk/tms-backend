// ExecutionCommandDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ExecutionCommandDTO {
    private UUID testRunId;
    private String command;
}