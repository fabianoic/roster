package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.EmployeePrincipal;
import com.ficsolution.roster.web.dto.auth.LoginRequest;
import com.ficsolution.roster.web.dto.auth.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();

        long EXPIRATION_SECONDS = 3600L;
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(employee.getId().toString())
                .claim("email", employee.getUsername())
                .claim("roles", List.of(employee.getRoleName()))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_SECONDS))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(token, EXPIRATION_SECONDS));
    }
}
