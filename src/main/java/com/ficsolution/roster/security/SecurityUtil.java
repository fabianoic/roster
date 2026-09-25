package com.ficsolution.roster.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static UUID currentEmployeeId() {
        return UUID.fromString(currentAuthentication().getName());
    }

    public static boolean hasAnyPermission(String... permissions) {
        Set<String> required = Set.of(permissions);
        return currentAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(required::contains);
    }

    public static void requirePermission(String... permissions) {
        if (!hasAnyPermission(permissions)) {
            throw new AccessDeniedException("Requires one of permissions: " + Arrays.toString(permissions));
        }
    }

    public static void requireOwnershipOrPermission(UUID ownerId, String... bypassPermissions) {
        if (hasAnyPermission(bypassPermissions)) {
            return;
        }
        if (!currentEmployeeId().equals(ownerId)) {
            throw new AccessDeniedException("Not permitted to access this resource.");
        }
    }

    public static void requireOwnershipOrPermission(Collection<UUID> acceptableOwnerIds, String... bypassPermissions) {
        if (hasAnyPermission(bypassPermissions)) {
            return;
        }
        if (!acceptableOwnerIds.contains(currentEmployeeId())) {
            throw new AccessDeniedException("Not permitted to access this resource.");
        }
    }

    private static Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
