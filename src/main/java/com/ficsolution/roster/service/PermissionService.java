package com.ficsolution.roster.service;

import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Permission> retrieveAllPermissions() {
        return permissionRepository.findAll(Sort.by("name"));
    }
}
