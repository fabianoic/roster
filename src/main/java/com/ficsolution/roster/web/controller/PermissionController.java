package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.service.PermissionService;
import com.ficsolution.roster.web.dto.permission.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> retrieveAllPermissions() {
        List<PermissionResponse> responses = permissionService.retrieveAllPermissions().stream()
                .map(PermissionResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
