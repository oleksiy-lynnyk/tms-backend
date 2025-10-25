package org.example.tmsstriker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;

    /** Record-style accessor for message (for tests using .message()). */
    public String message() {
        return this.message;
    }

    /** Record-style accessor for status (for tests using .status()). */
    public int status() {
        return this.status;
    }
}
