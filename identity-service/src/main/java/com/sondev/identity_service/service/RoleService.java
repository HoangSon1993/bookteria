package com.sondev.identity_service.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sondev.identity_service.dto.request.RoleRequest;
import com.sondev.identity_service.dto.response.RoleResponse;
import com.sondev.identity_service.entity.Permission;
import com.sondev.identity_service.entity.Role;
import com.sondev.identity_service.mapper.RoleMapper;
import com.sondev.identity_service.repository.PermissionRepository;
import com.sondev.identity_service.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService(
            RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(r -> roleMapper.toRoleResponse(r)).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
