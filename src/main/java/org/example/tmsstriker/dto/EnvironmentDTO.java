package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EnvironmentDTO {
    private UUID id;
    private UUID projectId;
    private String name;
    private String slug;
    private String description;
    private String host;
    private Integer port;         // ← додайте
}

