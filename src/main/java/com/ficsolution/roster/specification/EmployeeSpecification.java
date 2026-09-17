package com.ficsolution.roster.specification;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class EmployeeSpecification {

    public static Specification<Employee> hasRoleId(UUID roleId) {
        return ((root, query, criteriaBuilder) ->
                roleId == null ? null : criteriaBuilder.equal(root.get("role").get("id"), roleId));
    }

    public static Specification<Employee> hasName(String name) {
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.equal(root.get("name"), name));
    }

    public static Specification<Employee> hasEmail(String email) {
        return ((root, query, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.equal(root.get("email"), email));
    }

    public static Specification<Employee> hasStatus(EmployeeStatus status) {
        return ((root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }
}
