package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.Permission;
import org.springframework.data.jpa.domain.Specification;

public class PermissionSpecification {

    public static Specification<Permission> nameContains(String name) {
        return ((root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }
}
