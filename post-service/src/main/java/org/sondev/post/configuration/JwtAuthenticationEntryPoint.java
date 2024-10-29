package org.sondev.post.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.sondev.post.dto.response.ApiResponse;
import org.sondev.post.exception.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @note: Vì JwtAuthenticationEntryPoint chỉ được sử dụng 1 lần nên không cần tạo Bean.
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        /** Status code */
        response.setStatus(errorCode.getStatusCode().value());
        /** Content type */
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE); // MediaType.APPLICATION_JSON_VALUE ==> "application/json"
        /** Chuẩn bị nội dung body */
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        /** Gán nội dung body (đã to json) vào body */
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        /**Gửi request về cho client*/
        response.flushBuffer();
    }
}
