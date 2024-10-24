package com.sondev.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sondev.identity.dto.request.RoleRequest;
import com.sondev.identity.dto.response.ApiResponse;
import com.sondev.identity.dto.response.RoleResponse;
import com.sondev.identity.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable("role") String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
