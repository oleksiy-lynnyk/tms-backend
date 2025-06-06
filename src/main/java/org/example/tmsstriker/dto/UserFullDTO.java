// src/main/java/org/example/tmsstriker/dto/UserFullDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserFullDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
}
