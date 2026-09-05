package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    private UUID id = UUID.randomUUID();

    @Test
    void testRetrieveAllRoles() {
        // arrange
        List<Role> roles = Arrays.asList(
                new Role(UUID.randomUUID(), "MANAGER"),
                new Role(UUID.randomUUID(), "KITCHEN PORTER"),
                new Role(UUID.randomUUID(), "SUPERVISOR")
        );
        when(roleRepository.findAll()).thenReturn(roles);
        // act
        List<Role> retrievedRoles = roleService.retrieveAllRoles();

        // assert
        assertNotNull(retrievedRoles);
        assertEquals(3, retrievedRoles.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveById() {
        // arrange
        Role role = new Role(id, "TEST_ROLE");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        // act
        Role retrievedRole = roleService.retrieveById(id);

        // assert
        assertNotNull(retrievedRole);
        assertEquals(role.getName(), retrievedRole.getName());
        assertEquals(id, retrievedRole.getId());
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void testRetrieveById_NotFound() {
        // arrange
        when(roleRepository.findById(id)).thenReturn(Optional.empty());
        // act & assert
        assertThrows(ObjectNotFoundException.class, () -> roleService.retrieveById(id));
        verify(roleRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateRoleName() {
        // arrange
        Role role = new Role(id, "TEST_ROLE");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // act
        Role updatedRole = roleService.updateRoleName(id, "ROLE_TEST");

        // assert
        assertNotNull(updatedRole);
        assertEquals("ROLE_TEST", updatedRole.getName());
        verify(roleRepository, times(1)).findById(id);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testCreateRole() {
        // arrange
        Role role = new Role(id, "TEST_ROLE");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // act & assert
        assertNotNull(roleService.createRole(role));
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void deleteRole() {
        // arrange
        Role role = new Role(id, "TEST_ROLE");
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        // act
        roleService.deleteRole(id);

        // assert
        verify(roleRepository).delete(role);
        verify(roleRepository).findById(id);
    }

}
