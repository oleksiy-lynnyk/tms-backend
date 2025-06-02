// ProjectDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectDTO extends BaseDTO<UUID> {
    private String name;
    private String description;
    private String code;
    private int testCaseCount;


}

