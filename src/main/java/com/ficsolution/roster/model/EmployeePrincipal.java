package com.ficsolution.roster.model;

import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class EmployeePrincipal implements UserDetails {

    @Getter
    private final UUID id;
    private final String email;
    private final String passwordHash;
    @Getter
    private final String roleName;
    @Getter
    private final Set<String> permissions;
    private final boolean accountLocked;
    private final EmployeeStatus status;

    public EmployeePrincipal(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.passwordHash = employee.getPassword();
        // resolvidos AQUI, dentro da transação
        this.roleName = employee.getRole().getName();
        this.permissions = employee.getRole().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toUnmodifiableSet());
        this.accountLocked = employee.isAccountLocked();
        this.status = employee.getStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(EmployeeStatus.ACTIVE);
    }
}
