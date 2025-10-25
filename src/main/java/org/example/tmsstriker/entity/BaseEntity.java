package org.example.tmsstriker.entity;

import lombok.Data;
import java.util.UUID;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    private UUID id;
}
