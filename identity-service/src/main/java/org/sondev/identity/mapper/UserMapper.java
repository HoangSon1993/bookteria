package org.sondev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.sondev.identity.dto.request.UserCreationRequest;
import org.sondev.identity.dto.request.UserUpdateRequest;
import org.sondev.identity.dto.response.UserResponse;
import org.sondev.identity.entity.User;

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
