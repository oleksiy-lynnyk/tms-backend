// TestStepResultDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TestStepResultDTO extends BaseDTO<UUID> {
    private UUID testCaseInstanceId;
    private String result;
    private String message;
}
