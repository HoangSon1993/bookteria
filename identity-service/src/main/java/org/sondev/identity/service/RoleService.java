package org.sondev.identity.service;

import java.util.HashSet;
import java.util.List;

import org.sondev.identity.dto.request.RoleRequest;
import org.sondev.identity.dto.response.RoleResponse;
import org.sondev.identity.entity.Permission;
import org.sondev.identity.entity.Role;
import org.sondev.identity.mapper.RoleMapper;
import org.sondev.identity.repository.PermissionRepository;
import org.sondev.identity.repository.RoleRepository;
import org.springframework.stereotype.Service;

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
