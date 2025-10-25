// src/main/java/org/example/tmsstriker/dto/AppUserFullDTO.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AppUserFullDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
}

