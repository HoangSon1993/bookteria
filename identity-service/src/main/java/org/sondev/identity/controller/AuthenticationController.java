package org.sondev.identity.controller;

import java.text.ParseException;

import org.sondev.identity.dto.request.AuthenticationRequest;
import org.sondev.identity.dto.request.IntrospectRequest;
import org.sondev.identity.dto.request.LogoutRequest;
import org.sondev.identity.dto.request.RefreshTokenRequest;
import org.sondev.identity.dto.response.ApiResponse;
import org.sondev.identity.dto.response.AuthenticationResponse;
import org.sondev.identity.dto.response.InstrospectResponse;
import org.sondev.identity.dto.response.RefreshTokenResponse;
import org.sondev.identity.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        // spotless:off
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
        // spotless:on
    }

    @PostMapping("/introspect")
    ApiResponse<InstrospectResponse> authenticated(@RequestBody IntrospectRequest request) {
        InstrospectResponse result;
        try {
            result = authenticationService.instrospect(request);
        } catch (JOSEException | ParseException e) {
            result = InstrospectResponse.builder().valid(false).build();
        }
        // spotless:off
        return ApiResponse.<InstrospectResponse>builder()
                .result(result)
                .build();
        // spotless:on
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        // spotless:off
        return ApiResponse.<Void>builder()
                .build();
        // spotless:on
    }

    @PostMapping("/refresh")
    ApiResponse<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        RefreshTokenResponse result = authenticationService.refreshToken(request);
        // spotless:off
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(result)
                .build();
        // spotless:on
    }
}
