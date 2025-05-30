// BulkTestCaseRequestDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;
import java.util.List;
import java.util.Map;

/** Запит для bulk-операцій над test cases */
@Data
public class BulkTestCaseRequestDTO {
    private List<UUID> ids;
    /** Якщо true – просто видалити всі ids */
    private boolean delete;
    /** Перемістити до suiteId */
    private UUID moveToSuiteId;
    /** Скопіювати до suiteId */
    private UUID copyToSuiteId;
    /** Карта <ім'я поля, операція> */
    private Map<String, BulkFieldOperationDTO> operations;
}

