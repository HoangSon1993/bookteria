package org.sondev.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.sondev.apigateway.dto.request.IntrospectRequest;
import org.sondev.apigateway.dto.response.ApiResponse;
import org.sondev.apigateway.dto.response.InstrospectResponse;
import org.sondev.apigateway.repository.IdentityClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final IdentityClient identityClient;


    public Mono<ApiResponse<InstrospectResponse>> introspect(String token) {

        return identityClient.instrospect(IntrospectRequest.builder()
                .token(token)
                .build());
    }
}
