package org.sondev.gateway.service;

import org.sondev.gateway.dto.request.IntrospectRequest;
import org.sondev.gateway.dto.response.ApiResponse;
import org.sondev.gateway.dto.response.InstrospectResponse;
import org.sondev.gateway.repository.IdentityClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final IdentityClient identityClient;

    public Mono<ApiResponse<InstrospectResponse>> introspect(String token) {

        return identityClient.instrospect(
                IntrospectRequest.builder().token(token).build());
    }
}
