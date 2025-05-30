// BulkFieldOperationDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class BulkFieldOperationDTO {
    private List<UUID> ids;
    private String fieldName;
    private Object newValue;
}
