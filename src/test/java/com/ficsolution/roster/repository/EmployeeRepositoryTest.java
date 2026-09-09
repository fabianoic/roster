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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.employeeId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateEmployee() {
        // arrange
        Employee employee = new Employee(null, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.randomUUID(), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                0,
                null);

        // act
        Employee savedEmployee = employeeRepository.save(employee);

        // assert
        assertNotNull(savedEmployee);
        assertEquals("Fabiano Campos", savedEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", savedEmployee.getEmail());
    }

    @Test
    void testRetrieveAllEmployees() {
        List<Employee> retrievedAllEmployees = employeeRepository.findAll();

        assertFalse(retrievedAllEmployees.isEmpty());
        assertEquals(5, retrievedAllEmployees.size());
    }

    @Test
    void testRetrieveEmployeeById() {
        Optional<Employee> retrievedEmployee = employeeRepository.findById(employeeId);

        assertFalse(retrievedEmployee.isEmpty());
        assertEquals("Ana Silva", retrievedEmployee.get().getName());
        assertEquals("ana.silva@empresa.com", retrievedEmployee.get().getEmail());
    }

    @Test
    void testUpdateEmployeeNameAndPasswordAndStatus() {
        Employee employee = employeeRepository.findById(employeeId).get();
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
        Employee employee = employeeRepository.findById(employeeId).get();
        employeeRepository.delete(employee);

        assertFalse(employeeRepository.findById(employeeId).isPresent());
    }
}
