package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.repository.specification.PermissionSpecification;
import com.ficsolution.roster.service.PermissionService;
import com.ficsolution.roster.web.dto.common.PagedResponse;
import com.ficsolution.roster.web.dto.permission.PermissionFilter;
import com.ficsolution.roster.web.dto.permission.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<PagedResponse<PermissionResponse>> retrieveAllPermissions(@ModelAttribute PermissionFilter filter, @PageableDefault(sort = "name") Pageable pageable) {
        Specification<Permission> specification = Specification.allOf(
                PermissionSpecification.nameContains(filter.name())
        );
        Page<Permission> permissions = permissionService.retrieveAllPermissions(specification, pageable);
        return ResponseEntity.ok(PagedResponse.from(permissions.map(PermissionResponse::from)));
    }
}
