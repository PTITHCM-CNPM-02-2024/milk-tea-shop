package com.mts.backend.application.security;

import com.mts.backend.application.security.model.UserPrincipal;
import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface IJwtService {
    String generateToken(UserPrincipal userPrincipal);

    boolean validateToken(String token, UserPrincipal userPrincipal);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
