// src/main/java/org/example/tmsstriker/dto/UserShortDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserShortDTO {
    private UUID id;
    private String name;
}
