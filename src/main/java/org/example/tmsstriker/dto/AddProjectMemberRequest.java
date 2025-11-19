package org.example.tmsstriker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.tmsstriker.entity.ProjectRole;

import java.util.UUID;

/**
 * Request для додавання користувача до проекту
 */
@Data
public class AddProjectMemberRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Role is required")
    private ProjectRole role;
}