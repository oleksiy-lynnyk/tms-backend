// BaseDTO.java (generic)
package org.example.tmsstriker.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BaseDTO<T> {
    private T id;
}