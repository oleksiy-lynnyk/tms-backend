package org.example.tmsstriker.dto;

import lombok.Data;
import org.example.tmsstriker.entity.ProjectRole;

import java.util.UUID;

/**
 * DTO для відображення інформації про члена проекту
 */
@Data
public class ProjectMemberDTO {
    private UUID id;
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private UUID userId;
    private String username;
    private String userFullName;
    private String userEmail;
    private ProjectRole role;
}