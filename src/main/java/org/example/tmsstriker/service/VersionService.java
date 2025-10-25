package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.entity.Version;
import org.example.tmsstriker.repository.VersionRepository;
import org.example.tmsstriker.mapper.VersionMapper;
import org.example.tmsstriker.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository repository;
    private final VersionMapper versionMapper;

    public List<VersionDTO> getByProject(UUID projectId) {
        return repository.findAllByProject_Id(projectId).stream()
                .map(versionMapper::toDto)
                .collect(Collectors.toList());
    }

    public VersionDTO findById(UUID id) {
        Version version = repository.findById(id)
                .orElseThrow(() -> new ApiException("Version not found", HttpStatus.NOT_FOUND));
        return versionMapper.toDto(version);
    }

    public VersionDTO create(VersionDTO dto) {
        Version version = versionMapper.toEntity(dto);
        version.setId(null);

        // ✅ ВИПРАВЛЕНО: переконуємося що name заповнений
        if (version.getName() == null && version.getTitle() != null) {
            version.setName(version.getTitle());
        }

        return versionMapper.toDto(repository.save(version));
    }

    public VersionDTO update(UUID id, VersionDTO dto) {
        Version version = repository.findById(id)
                .orElseThrow(() -> new ApiException("Version not found", HttpStatus.NOT_FOUND));

        version.setTitle(dto.getTitle());
        version.setName(dto.getTitle());    // ✅ ДОДАНО: синхронізуємо name з title
        version.setSlug(dto.getSlug());
        version.setDescription(dto.getDescription());

        return versionMapper.toDto(repository.save(version));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Version not found", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}