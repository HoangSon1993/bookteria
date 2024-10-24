package com.sondev.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sondev.identity_service.dto.request.UserCreationRequest;
import com.sondev.identity_service.dto.request.UserUpdateRequest;
import com.sondev.identity_service.dto.response.UserResponse;
import com.sondev.identity_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    /**
     * @General: Mapping from {@code UserUpdateRequest} to {@code User}
     * @Description: Trong {@code User}, {@code roles } có kiểu {@code Set<Role>}, nhưng trong {@code UserUpdateRequest} {@code roles } có kiểu {@code Set<String>}
     * nên không thể map tự động được mà phải map bằng tay.
     */
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);
}
