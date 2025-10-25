package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConfigurationDTO {
    private UUID id;
    private UUID projectId;
    private String name;
    private String slug;
    private String description;
    private String os;
    private String browser;
    private String device;
}
