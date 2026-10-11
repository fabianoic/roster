package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.Store;
import org.springframework.data.jpa.domain.Specification;

public class StoreSpecification {

    public static Specification<Store> nameContains(String name) {
        return ((root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    public static Specification<Store> addressContains(String address) {
        return ((root, _, criteriaBuilder) ->
                address == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
    }
}
