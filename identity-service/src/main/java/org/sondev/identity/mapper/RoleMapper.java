package org.sondev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sondev.identity.dto.request.RoleRequest;
import org.sondev.identity.dto.response.RoleResponse;
import org.sondev.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
