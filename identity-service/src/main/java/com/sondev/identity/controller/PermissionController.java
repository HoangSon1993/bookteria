package com.sondev.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sondev.identity.dto.request.PermissionRequest;
import com.sondev.identity.dto.response.ApiResponse;
import com.sondev.identity.dto.response.PermissionResponse;
import com.sondev.identity.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable("permission") String permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}
