package org.example.tmsstriker.dto;// src/main/java/org/example/tmsstriker/dto/UserShortDTO.java
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserShortDTO {
    private UUID id;
    private String name;
}

