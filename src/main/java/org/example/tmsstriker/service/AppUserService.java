package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.AppUserFullDTO;
import org.example.tmsstriker.dto.AppUserShortDTO;
import org.example.tmsstriker.entity.AppUser;
import org.example.tmsstriker.exception.ApiException;
import org.example.tmsstriker.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public Page<AppUserFullDTO> getAllPaged(Pageable pageable) {
        return appUserRepository.findAll(pageable).map(this::toFullDto);
    }

    public List<AppUserShortDTO> getShort() {
        return appUserRepository.findAll().stream()
                .map(this::toShortDto)
                .collect(Collectors.toList());
    }

    public AppUserFullDTO getById(UUID id) {
        return appUserRepository.findById(id)
                .map(this::toFullDto)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    public AppUserFullDTO create(AppUserFullDTO dto) {
        AppUser appUser = new AppUser();
        appUser.setId(UUID.randomUUID());
        appUser.setUsername(dto.getUsername());
        appUser.setEmail(dto.getEmail());
        appUser.setFullName(dto.getFullName());
        appUserRepository.save(appUser);
        return toFullDto(appUser);
    }

    public AppUserFullDTO update(UUID id, AppUserFullDTO dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        appUser.setUsername(dto.getUsername());
        appUser.setEmail(dto.getEmail());
        appUser.setFullName(dto.getFullName());
        appUserRepository.save(appUser);
        return toFullDto(appUser);
    }

    public void delete(UUID id) {
        if (!appUserRepository.existsById(id)) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        appUserRepository.deleteById(id);
    }

    private AppUserShortDTO toShortDto(AppUser appUser) {
        AppUserShortDTO dto = new AppUserShortDTO();
        dto.setId(appUser.getId());
        dto.setUsername(appUser.getUsername());
        dto.setFullName(appUser.getFullName());
        return dto;
    }

    private AppUserFullDTO toFullDto(AppUser appUser) {
        AppUserFullDTO dto = new AppUserFullDTO();
        dto.setId(appUser.getId());
        dto.setUsername(appUser.getUsername());
        dto.setEmail(appUser.getEmail());
        dto.setFullName(appUser.getFullName());
        return dto;
    }
}
