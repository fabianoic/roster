package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.Role;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {

    public static Specification<Role> nameContains(String name) {
        return ((root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }
}
