package org.example.tmsstriker.service;

import lombok.RequiredArgsConstructor;
import org.example.tmsstriker.dto.VersionDTO;
import org.example.tmsstriker.entity.Version;
import org.example.tmsstriker.repository.VersionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository repository;
    private final ModelMapper mapper;

    public List<VersionDTO> getByProject(UUID projectId) {
        return repository.findAllByProjectId(projectId).stream()
                .map(v -> mapper.map(v, VersionDTO.class))
                .collect(Collectors.toList());
    }

    public VersionDTO create(VersionDTO dto) {
        Version version = mapper.map(dto, Version.class);
        version.setId(null);
        return mapper.map(repository.save(version), VersionDTO.class);
    }

    public VersionDTO update(UUID id, VersionDTO dto) {
        Version version = repository.findById(id).orElseThrow();
        version.setTitle(dto.getTitle());
        version.setSlug(dto.getSlug());
        version.setDescription(dto.getDescription());
        return mapper.map(repository.save(version), VersionDTO.class);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}


