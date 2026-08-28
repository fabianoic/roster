package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.EmployeeNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    private UUID id = UUID.randomUUID();

    @Test
    void testCreateEmployee() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        employeeRepository.save(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee createdEmployee = employeeService.createEmployee(employee);

        assertNotNull(createdEmployee);
        assertEquals("Fabiano Campos", createdEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", createdEmployee.getEmail());
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
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> retrievedEmployees = employeeService.retrieveAllEmployees();

        assertNotNull(retrievedEmployees);
        assertEquals(2, retrievedEmployees.size());
        assertEquals("Fabiano Campos", retrievedEmployees.get(0).getName());
        assertEquals("Oscar", retrievedEmployees.get(1).getName());
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
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        Employee retrievedEmployee = employeeService.retrieveEmployeeById(id);

        assertNotNull(retrievedEmployee);
        assertEquals("Fabiano Campos", retrievedEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", retrievedEmployee.getEmail());
    }

    @Test
    void testRetrieveEmployeeById_NotFound() {
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.retrieveEmployeeById(id));
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateEmployeeNameAndEmail() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        Employee editEmployee = new Employee(id, "Oscar",
                "oscar@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(editEmployee);

        Employee updatedEmployee = employeeService.updateEmployee(id, employee);

        assertNotNull(updatedEmployee);
        assertEquals("Oscar", updatedEmployee.getName());
        assertEquals("oscar@gmail.com", updatedEmployee.getEmail());
    }

    @Test
    void testChangeEmployeeStatus() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee employeeStatusChanged = employeeService.changeEmployeeStatus(id);

        assertEquals(EmployeeStatus.INACTIVE, employeeStatusChanged.getStatus());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(employee);
    }
}
