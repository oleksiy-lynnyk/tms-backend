package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.entity.Version;
import org.example.tmsstriker.repository.VersionRepository;
import org.example.tmsstriker.mapper.VersionMapper;
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

    public VersionDTO create(VersionDTO dto) {
        Version version = versionMapper.toEntity(dto);
        version.setId(null);
        return versionMapper.toDto(repository.save(version));
    }

    public VersionDTO update(UUID id, VersionDTO dto) {
        Version version = repository.findById(id).orElseThrow();
        version.setTitle(dto.getTitle());             // ← тут title
        version.setSlug(dto.getSlug());
        version.setDescription(dto.getDescription());
        return versionMapper.toDto(repository.save(version));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
