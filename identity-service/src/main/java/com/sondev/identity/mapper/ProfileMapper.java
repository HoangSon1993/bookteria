package com.sondev.identity.mapper;

import org.mapstruct.Mapper;

import com.sondev.identity.dto.request.ProfileCreationRequest;
import com.sondev.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
