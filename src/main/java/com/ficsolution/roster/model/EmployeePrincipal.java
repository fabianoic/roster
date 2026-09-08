package com.ficsolution.roster.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EmployeePrincipal implements UserDetails {
    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String roleName;

    public EmployeePrincipal(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.passwordHash = employee.getPassword();
        this.roleName = employee.getRole().getName();
    }

    public UUID getId() { return id; }
    public String getRoleName() { return roleName; }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return passwordHash; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
