package org.sondev.apigateway.repository;

import org.sondev.apigateway.dto.request.IntrospectRequest;
import org.sondev.apigateway.dto.response.ApiResponse;
import org.sondev.apigateway.dto.response.InstrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {


    @PostExchange(url = "/auth/introspect",contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<InstrospectResponse>> instrospect(@RequestBody IntrospectRequest request);
}
