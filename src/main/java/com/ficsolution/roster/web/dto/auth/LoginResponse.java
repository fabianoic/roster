package com.ficsolution.roster.web.dto.auth;

public record LoginResponse(String token, long expiresInSeconds) {
}
