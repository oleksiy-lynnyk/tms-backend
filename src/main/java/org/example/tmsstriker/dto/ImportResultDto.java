// ImportResultDto.java
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImportResultDto {
    private int created;
    private int updated;
    private List<String> errors;

    public void incrementCreated() { created++; }
    public void addError(String error) { errors.add(error); }
}

