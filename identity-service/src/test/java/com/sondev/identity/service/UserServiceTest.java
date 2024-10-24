package com.sondev.identity.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.sondev.identity.dto.request.UserCreationRequest;
import com.sondev.identity.dto.response.UserResponse;
import com.sondev.identity.entity.User;
import com.sondev.identity.exception.AppException;
import com.sondev.identity.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);
        request = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();
        userResponse = UserResponse.builder()
                .id("abc123")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
        user = User.builder()
                .id("abc123")
                .username("john")
                .build();
    }

/*
    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("abc123");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }
*/

    /*
    	@Test
    	void createUser_userExisted_fail() {
    		// GIVEN
    		when(userRepository.existsByUsername(anyString())).thenReturn(true);

    		// WHEN
    		var exception = org.junit.jupiter.api.Assertions.assertThrows(
    				AppException.class, () -> userService.createUser(request));
    		Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    	}
    */

    @Test
    @WithMockUser(username = "john")
    void getMyInfo_valid_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getId()).isEqualTo("abc123");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    @WithMockUser(username = "john")
    void getMyInfo_userNotFound_fail() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        // When
        var exception =
                org.junit.jupiter.api.Assertions.assertThrows(AppException.class, () -> userService.getMyInfo());
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
}
