package org.sondev.identity.mapper;

import org.mapstruct.Mapper;
import org.sondev.identity.dto.request.ProfileCreationRequest;
import org.sondev.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
