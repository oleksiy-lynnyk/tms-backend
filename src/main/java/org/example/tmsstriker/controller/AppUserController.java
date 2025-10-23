package org.example.tmsstriker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.AppUserFullDTO;
import org.example.tmsstriker.dto.AppUserShortDTO;
import org.example.tmsstriker.service.AppUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/app-users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService service;

    @GetMapping
    @Operation(summary = "Get all users (paged)", description = "Returns paginated list of users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval")
    })
    public ResponseEntity<?> getAllPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaged(pageable));
    }

    @GetMapping("/short")
    @Operation(summary = "Get all short users", description = "Returns list of short user representations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserShortDTO.class)))
    })
    public ResponseEntity<List<AppUserShortDTO>> getShort() {
        return ResponseEntity.ok(service.getShort());
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<AppUserFullDTO> create(
            @RequestBody(description = "User payload", required = true,
                    content = @Content(schema = @Schema(implementation = AppUserFullDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody AppUserFullDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns user details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<AppUserFullDTO> getById(
            @PathVariable @Schema(description = "UUID of the user", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<AppUserFullDTO> update(
            @PathVariable @Schema(description = "UUID of the user to update", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id,
            @RequestBody(description = "Updated user payload", required = true,
                    content = @Content(schema = @Schema(implementation = AppUserFullDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody AppUserFullDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Schema(description = "UUID of the user to delete", example = "d6b0ee32-69f5-4f45-b445-6c7e7d3ad2f5") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
