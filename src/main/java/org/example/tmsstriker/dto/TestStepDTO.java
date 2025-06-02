package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TestStepDTO {
    private UUID id;
    private Integer orderIndex;
    private String action;
    private String expectedResult;
}

