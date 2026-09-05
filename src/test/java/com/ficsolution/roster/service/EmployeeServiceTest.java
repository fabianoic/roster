package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.EmailAlreadyExistsException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.exception.PasswordNotEqualException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UUID id = UUID.randomUUID();

    @Test
    void testCreateEmployee() {
        String email = "fabiano.fic@gmail.com";
        Role role = new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF");
        Employee employee = new Employee(id, "Fabiano Campos",
                email,
                "RANDOMHASHPASSWORD",
                role,
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("ENCODEDPASSWORD");
        when(roleService.retrieveById(role.getId())).thenReturn(role);
        Employee createdEmployee = employeeService.createEmployee(employee);

        assertNotNull(createdEmployee);
        assertEquals("Fabiano Campos", createdEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", createdEmployee.getEmail());
        verify(employeeRepository, times(1)).existsByEmail(email);
        verify(employeeRepository, times(1)).save(employee);
        verify(roleService, times(1)).retrieveById(employee.getRole().getId());
    }

    @Test
    void testCreateEmployee_emailExistException() {
        String email = "fabiano.fic@gmail.com";
        Employee employee = new Employee(id, "Fabiano Campos",
                email,
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.createEmployee(employee));
        verify(employeeRepository, times(1)).existsByEmail(email);
        verify(employeeRepository, times(0)).save(employee);
        verify(roleService, times(0)).retrieveById(employee.getRole().getId());
    }

    @Test
    void testCreateEmployee_roleException() {
        String email = "fabiano.fic@gmail.com";
        Role role = new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF");
        Employee employee = new Employee(id, "Fabiano Campos",
                email,
                "RANDOMHASHPASSWORD",
                role,
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.existsByEmail(email)).thenReturn(false);
        doThrow(ObjectNotFoundException.class).when(roleService).retrieveById(role.getId());

        assertThrows(ObjectNotFoundException.class, () -> employeeService.createEmployee(employee));
        verify(employeeRepository, times(1)).existsByEmail(email);
        verify(roleService, times(1)).retrieveById(role.getId());
        verify(employeeRepository, times(0)).save(employee);
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

        assertThrows(ObjectNotFoundException.class, () -> employeeService.retrieveEmployeeById(id));
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateEmployeeInfoNameAndEmail() {
        Role role = new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF");
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                role,
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        Employee editEmployee = new Employee(id, "Oscar",
                "oscar@gmail.com",
                "RANDOMHASHPASSWORD",
                role,
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(editEmployee);
        when(roleService.retrieveById(any(UUID.class))).thenReturn(role);

        Employee updatedEmployee = employeeService.updateEmployeeInfo(id, employee);

        assertNotNull(updatedEmployee);
        assertEquals("Oscar", updatedEmployee.getName());
        assertEquals("oscar@gmail.com", updatedEmployee.getEmail());
    }

    @Test
    void testUpdateEmployeePassword() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        Employee employeeNewPassword = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "NEWRANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeNewPassword);

        Employee updatedEmployee = employeeService.changeEmployeePassword(id, employee.getPassword(), employeeNewPassword.getPassword());

        assertNotNull(updatedEmployee);
        assertEquals("NEWRANDOMHASHPASSWORD", updatedEmployee.getPassword());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(employeeNewPassword);
    }

    @Test
    void testUpdateEmployeePassword_oldPasswordNotEqual() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        assertThrows(PasswordNotEqualException.class, () -> employeeService.changeEmployeePassword(id, "WRONGRANDOMHASHPASSWORD", null));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployeePassword_newPasswordIsNull() {
        Employee employee = new Employee(id, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        assertThrows(PasswordNotEqualException.class, () -> employeeService.changeEmployeePassword(id, "RANDOMHASHPASSWORD", null));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(0)).save(any(Employee.class));
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
