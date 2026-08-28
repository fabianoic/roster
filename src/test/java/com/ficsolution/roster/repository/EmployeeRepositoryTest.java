package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private UUID id = UUID.randomUUID();

    @Test
    void testCreateEmployee() {
        // arrange
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.randomUUID(), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());

        // act
        Employee savedEmployee = employeeRepository.save(employee);

        // assert
        assertNotNull(savedEmployee);
        assertEquals("Fabiano Campos", savedEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", savedEmployee.getEmail());
    }

    @Test
    void testRetrieveAllEmployees() {
        List<Employee> employees = Arrays.asList(
                new Employee(id, "Fabiano Campos",
                        "fabiano.fic@gmail.com",
                        "RANDOMHASHPASSWORD",
                        new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                        EmployeeStatus.ACTIVE,
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                new Employee(UUID.randomUUID(), "Oscar",
                        "oscar@gmail.com",
                        "RANDOMHASHPASSWORD",
                        new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                        EmployeeStatus.ACTIVE,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        employeeRepository.saveAll(employees);

        List<Employee> retrievedAllEmployees = employeeRepository.findAll();

        assertFalse(retrievedAllEmployees.isEmpty());
        assertEquals(2, retrievedAllEmployees.size());
        assertEquals("Oscar", retrievedAllEmployees.get(1).getName());
    }

    @Test
    void testRetrieveEmployeeById() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        employeeRepository.save(employee);

        Optional<Employee> retrievedEmployee = employeeRepository.findById(id);

        assertFalse(retrievedEmployee.isEmpty());
        assertEquals("Fabiano Campos", retrievedEmployee.get().getName());
        assertEquals("fabiano.fic@gmail.com", retrievedEmployee.get().getEmail());
    }

    @Test
    void testUpdateEmployeeNameAndPasswordAndStatus() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        employeeRepository.save(employee);
        employee.setPassword("NEWRANDOMHASHPASSWORD");
        employee.setName("Oscar");
        employee.setStatus(EmployeeStatus.INACTIVE);

        Employee updatedEmployee = employeeRepository.save(employee);

        assertNotNull(updatedEmployee);
        assertEquals("Oscar", updatedEmployee.getName());
        assertEquals(EmployeeStatus.INACTIVE, updatedEmployee.getStatus());
        assertEquals("NEWRANDOMHASHPASSWORD", updatedEmployee.getPassword());
    }

    @Test
    void testDeleteEmployee() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        employeeRepository.save(employee);

        employeeRepository.delete(employee);

        assertFalse(employeeRepository.findById(id).isPresent());
    }
}
