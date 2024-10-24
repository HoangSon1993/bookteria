package com.sondev.identity.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.sondev.identity.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID") // message chính là key của enums
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD") // message chính là key của enums
    String password;

    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;

    String city;
}