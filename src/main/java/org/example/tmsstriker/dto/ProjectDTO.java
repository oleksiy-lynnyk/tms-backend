// ProjectDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProjectDTO extends BaseDTO<UUID> {
    private String name;
    private String description;
    private String code;
    private int testCaseCount;


}

