package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class BulkTestCaseRequestDTO {
    private List<UUID> ids;
    private boolean delete;
    private UUID moveToSuiteId;
    private UUID copyToSuiteId;
    private List<BulkFieldOperationDTO> operations;
}
