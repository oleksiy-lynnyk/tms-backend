// TestStepResultDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestStepResultDTO extends BaseDTO<UUID> {
    private UUID testCaseInstanceId;
    private String result;
    private String message;
}
