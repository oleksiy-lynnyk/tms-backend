// TestCaseInstanceDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestCaseInstanceDTO extends BaseDTO<UUID> {
    private UUID testCaseId;
    private UUID testRunId;
    private String status;
    private String result;
}
