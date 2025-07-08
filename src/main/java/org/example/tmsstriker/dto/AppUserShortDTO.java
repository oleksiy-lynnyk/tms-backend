package org.example.tmsstriker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor // Додає конструктор без аргументів
@AllArgsConstructor // Додає конструктор з усіма аргументами
public class AppUserShortDTO {
    private UUID id;
    private String username;
    private String fullName;
}

