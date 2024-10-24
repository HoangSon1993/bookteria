package com.sondev.identity_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sondev.identity_service.dto.request.PermissionRequest;
import com.sondev.identity_service.dto.response.PermissionResponse;
import com.sondev.identity_service.entity.Permission;
import com.sondev.identity_service.mapper.PermissionMapper;
import com.sondev.identity_service.repository.PermissionRepository;

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
