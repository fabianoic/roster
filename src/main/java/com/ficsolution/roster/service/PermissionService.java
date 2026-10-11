package com.ficsolution.roster.service;

import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Page<Permission> retrieveAllPermissions(Specification<Permission> specification, Pageable pageable) {
        return permissionRepository.findAll(specification, pageable);
    }
}
