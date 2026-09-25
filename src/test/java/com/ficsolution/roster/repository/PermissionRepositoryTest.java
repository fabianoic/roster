package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Set;

import static com.ficsolution.roster.util.Util.MANAGER_PERMISSIONS;
import static com.ficsolution.roster.util.Util.roleId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByNameIn() {
        // act
        List<Permission> permissions = permissionRepository.findByNameIn(Set.of("SHIFT_READ", "SHIFT_WRITE", "UNKNOWN"));

        // assert
        assertEquals(Set.of("SHIFT_READ", "SHIFT_WRITE"),
                Set.copyOf(permissions.stream().map(Permission::getName).toList()));
    }

    @Test
    void testSeededManagerHasAllPermissions() {
        // act
        Role manager = roleRepository.findById(roleId).orElseThrow();

        // assert
        assertEquals(Set.copyOf(MANAGER_PERMISSIONS),
                Set.copyOf(manager.getPermissions().stream().map(Permission::getName).toList()));
    }
}
