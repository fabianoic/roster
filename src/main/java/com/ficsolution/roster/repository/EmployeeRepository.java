package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"role", "role.permissions"})
    Optional<Employee> findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);
}
