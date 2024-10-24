package com.sondev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sondev.identity.dto.request.RoleRequest;
import com.sondev.identity.dto.response.RoleResponse;
import com.sondev.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
