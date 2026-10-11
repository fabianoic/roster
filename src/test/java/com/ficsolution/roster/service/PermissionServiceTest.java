package com.ficsolution.roster.service;

import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private PermissionRepository permissionRepository;

    @Test
    void testRetrieveAllPermissions() {
        List<Permission> permissions = List.of(
                new Permission(UUID.randomUUID(), "ROLE_MANAGE", "Manage roles and their permissions"),
                new Permission(UUID.randomUUID(), "STORE_READ", "View stores")
        );
        Specification<Permission> specification = Specification.allOf();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Permission> page = new PageImpl<>(permissions, pageable, permissions.size());
        when(permissionRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Permission> retrievedPermissions = permissionService.retrieveAllPermissions(specification, pageable);

        assertNotNull(retrievedPermissions);
        assertEquals(2, retrievedPermissions.getContent().size());
        assertEquals("ROLE_MANAGE", retrievedPermissions.getContent().getFirst().getName());
        verify(permissionRepository, times(1)).findAll(specification, pageable);
    }
}
