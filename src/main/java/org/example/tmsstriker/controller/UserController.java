// src/main/java/org/example/tmsstriker/controller/UserController.java
package org.example.tmsstriker.controller;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.UserFullDTO;
import org.example.tmsstriker.dto.UserShortDTO;
import org.example.tmsstriker.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Список користувачів (коротко, для assign/select) */
    @GetMapping("/short")
    public List<UserShortDTO> getAllShort() {
        return userService.findAllShort();
    }

    /** Повний список користувачів (для адмінки) */
    @GetMapping
    public List<UserFullDTO> getAllFull() {
        return userService.findAllFull();
    }

    /** Один користувач за id */
    @GetMapping("/{id}")
    public UserFullDTO getById(@PathVariable UUID id) {
        return userService.findById(id).orElse(null);
    }

    // Для MVP — без create/update/delete (робимо через SQL/migration/manual спочатку)
}

