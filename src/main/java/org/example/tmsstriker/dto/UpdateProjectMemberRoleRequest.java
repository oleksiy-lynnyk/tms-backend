package org.example.tmsstriker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.tmsstriker.entity.ProjectRole;

/**
 * Request для оновлення ролі користувача в проекті
 */
@Data
public class UpdateProjectMemberRoleRequest {
    @NotNull(message = "Role is required")
    private ProjectRole role;
}