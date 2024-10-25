package org.sondev.identity.mapper;

import org.mapstruct.Mapper;
import org.sondev.identity.dto.request.PermissionRequest;
import org.sondev.identity.dto.response.PermissionResponse;
import org.sondev.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
