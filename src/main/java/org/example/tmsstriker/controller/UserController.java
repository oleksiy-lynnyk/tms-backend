// src/main/java/org/example/tmsstriker/controller/UserController.java
package org.example.tmsstriker.controller;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.UserFullDTO;
import org.example.tmsstriker.dto.UserShortDTO;
import org.example.tmsstriker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** ПАГІНАЦІЯ для фронта */
    @GetMapping
    public Page<UserFullDTO> getAllPaged(Pageable pageable) {
        return userService.findAllPaged(pageable);
    }

    /** Один користувач за id */
    @GetMapping("/{id}")
    public UserFullDTO getById(@PathVariable UUID id) {
        return userService.findById(id).orElse(null);
    }

    /** Додати нового користувача */
    @PostMapping
    public UserFullDTO createUser(@RequestBody UserFullDTO dto) {
        return userService.createUser(dto);
    }

    /** Оновити користувача */
    @PutMapping("/{id}")
    public UserFullDTO updateUser(@PathVariable UUID id, @RequestBody UserFullDTO dto) {
        return userService.updateUser(id, dto);
    }

    /** Видалити користувача */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
    @GetMapping("/short")
    public List<UserShortDTO> getShortUsers() {
        return userService.getAllShort(); // цей метод мапить entity → UserShortDTO
    }

}


