package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.AddProjectMemberRequest;
import org.example.tmsstriker.dto.ProjectMemberDTO;
import org.example.tmsstriker.dto.UpdateProjectMemberRoleRequest;
import org.example.tmsstriker.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
@Tag(name = "Project Members", description = "API для управління членами проекту")
public class ProjectMemberController {

    private final ProjectMemberService memberService;

    @GetMapping
    @Operation(summary = "Отримати всіх членів проекту", description = "Повертає список всіх користувачів, які мають доступ до проекту, з їх ролями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список членів успішно отримано",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectMemberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Проект не знайдено", content = @Content)
    })
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(
            @Parameter(description = "ID проекту", required = true)
            @PathVariable UUID projectId) {
        List<ProjectMemberDTO> members = memberService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    @Operation(summary = "Додати користувача до проекту", description = "Призначає користувача на проект з вказаною роллю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Користувач успішно доданий до проекту",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectMemberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Проект або користувач не знайдено", content = @Content),
            @ApiResponse(responseCode = "409", description = "Користувач вже є членом проекту", content = @Content)
    })
    public ResponseEntity<ProjectMemberDTO> addMember(
            @Parameter(description = "ID проекту", required = true)
            @PathVariable UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request) {
        ProjectMemberDTO member = memberService.addMember(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Оновити роль користувача в проекті", description = "Змінює роль користувача в проекті")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успішно оновлено",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectMemberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Користувач не є членом проекту", content = @Content)
    })
    public ResponseEntity<ProjectMemberDTO> updateMemberRole(
            @Parameter(description = "ID проекту", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "ID користувача", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProjectMemberRoleRequest request) {
        ProjectMemberDTO updated = memberService.updateMemberRole(projectId, userId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Видалити користувача з проекту", description = "Забирає у користувача доступ до проекту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Користувач успішно видалений з проекту", content = @Content),
            @ApiResponse(responseCode = "404", description = "Користувач не є членом проекту", content = @Content)
    })
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "ID проекту", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "ID користувача", required = true)
            @PathVariable UUID userId) {
        memberService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Отримати всі проекти користувача", description = "Повертає список всіх проектів, до яких користувач має доступ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектів успішно отримано",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectMemberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Користувач не знайдено", content = @Content)
    })
    public ResponseEntity<List<ProjectMemberDTO>> getUserProjects(
            @PathVariable UUID projectId,
            @Parameter(description = "ID користувача", required = true)
            @PathVariable UUID userId) {
        List<ProjectMemberDTO> projects = memberService.getUserProjects(userId);
        return ResponseEntity.ok(projects);
    }
}