package com.ficsolution.roster.model;

import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
public class EmployeePrincipal implements UserDetails {

    @Getter
    private final UUID id;
    private final String email;
    private final String passwordHash;
    @Getter
    private final String roleName;
    private final boolean accountLocked;
    private final EmployeeStatus status;

    public EmployeePrincipal(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.passwordHash = employee.getPassword();
        this.roleName = employee.getRole().getName(); // resolvido AQUI, dentro da transação
        this.accountLocked = employee.isAccountLocked();
        this.status = employee.getStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
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
