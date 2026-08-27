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
        // arrange
        Role role = new Role(UUID.randomUUID(), "TEST_ROLE");
        role = roleRepository.save(role);
        // act
        Role roleFound = roleRepository.findById(role.getId()).orElse(null);

        // assert
        assertNotNull(roleFound);
        assertEquals(role.getId(), roleFound.getId());
        assertEquals(role.getName(), roleFound.getName());
    }

    @Test
    void testDeleteRoleById() {
        // arrange
        Role role = new Role(UUID.randomUUID(), "TEST_ROLE");
        role = roleRepository.save(role);
        // act
        roleRepository.delete(role);
        Optional<Role> deletedRole = roleRepository.findById(role.getId());
        // assert
        assertFalse(deletedRole.isPresent());
    }

    @Test
    void testUpdateRoleName() {
        // arrange
        Role role = new Role(UUID.randomUUID(), "TEST_ROLE");
        roleRepository.save(role);
        // act
        Role newRoleName = new Role(role.getId(), "ROLE_TEST");
        Role updatedRole = roleRepository.save(newRoleName);
        // assert
        assertEquals(newRoleName.getId(), updatedRole.getId());
        assertNotEquals(updatedRole.getName(), role.getName());
        assertEquals(newRoleName.getName(), updatedRole.getName());
    }
}
