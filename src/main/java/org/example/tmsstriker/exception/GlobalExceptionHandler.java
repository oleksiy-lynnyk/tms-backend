// src/main/java/org/example/tmsstriker/exception/GlobalExceptionHandler.java
package org.example.tmsstriker.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex.getMessage(), ex.getStatus().value()));
    }

    // Додай інші хендлери за потреби

    // DTO для помилки
    public record ErrorResponse(String message, int status) {}
}

