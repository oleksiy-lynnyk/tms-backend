package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.UserFullDTO;
import org.example.tmsstriker.dto.UserShortDTO;
import org.example.tmsstriker.entity.User;
import org.example.tmsstriker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserShortDTO> findAllShort() {
        return userRepository.findAll().stream()
                .map(this::toShortDto)
                .collect(Collectors.toList());
    }

    public List<UserFullDTO> findAllFull() {
        return userRepository.findAll().stream()
                .map(this::toFullDto)
                .collect(Collectors.toList());
    }

    public Optional<UserFullDTO> findById(UUID id) {
        return userRepository.findById(id).map(this::toFullDto);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserFullDTO createUser(UserFullDTO dto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        userRepository.save(user);
        return toFullDto(user);
    }

    public UserFullDTO updateUser(UUID id, UserFullDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        userRepository.save(user);
        return toFullDto(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    // Маппери
    private UserShortDTO toShortDto(User user) {
        UserShortDTO dto = new UserShortDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    private UserFullDTO toFullDto(User user) {
        UserFullDTO dto = new UserFullDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
