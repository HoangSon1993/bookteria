package org.sondev.identity.service;

import java.util.List;

import org.sondev.identity.dto.request.PermissionRequest;
import org.sondev.identity.dto.response.PermissionResponse;
import org.sondev.identity.entity.Permission;
import org.sondev.identity.mapper.PermissionMapper;
import org.sondev.identity.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map((p) -> permissionMapper.toPermissionResponse(p))
                .toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
