package com.sondev.identity.mapper;

import org.mapstruct.Mapper;

import com.sondev.identity.dto.request.PermissionRequest;
import com.sondev.identity.dto.response.PermissionResponse;
import com.sondev.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
