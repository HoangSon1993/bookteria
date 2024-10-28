package org.sondev.gateway.repository;

import org.sondev.gateway.dto.request.IntrospectRequest;
import org.sondev.gateway.dto.response.ApiResponse;
import org.sondev.gateway.dto.response.InstrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import reactor.core.publisher.Mono;

public interface IdentityClient {

    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<InstrospectResponse>> instrospect(@RequestBody IntrospectRequest request);
}
