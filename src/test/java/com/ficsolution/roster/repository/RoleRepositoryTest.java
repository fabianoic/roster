package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.roleId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindAllRoles() {
        // arrange

        // act
        List<Role> retrievedRoles = roleRepository.findAll();

        // assert
        assertNotNull(retrievedRoles);
        assertEquals(3, retrievedRoles.size());
    }

    @Test
    void saveRole() {
        // arrange
        Role role = new Role(UUID.randomUUID(), "TEST_ROLE");

        // act
        role = roleRepository.save(role);

        // assert

        assertNotNull(role.getId());
        assertEquals("TEST_ROLE", role.getName());
    }

    @Test
    void testFindById() {
        // act
        Role roleFound = roleRepository.findById(roleId).orElse(null);

        // assert
        assertNotNull(roleFound);
        assertEquals(roleId, roleFound.getId());
        assertEquals("MANAGER", roleFound.getName());
    }

    @Test
    void testDeleteRoleById() {
        Role roleToDelete = roleRepository.findById(roleId).get();
        // act
        roleRepository.delete(roleToDelete);
        Optional<Role> deletedRole = roleRepository.findById(roleId);
        // assert
        assertFalse(deletedRole.isPresent());
    }

    @Test
    void testUpdateRoleName() {
        // act
        Role newRoleName = new Role(roleId, "ROLE_TEST");
        Role updatedRole = roleRepository.save(newRoleName);
        // assert
        assertEquals(newRoleName.getId(), updatedRole.getId());
        assertNotEquals("MANAGER", updatedRole.getName());
        assertEquals(newRoleName.getName(), updatedRole.getName());
    }
}
