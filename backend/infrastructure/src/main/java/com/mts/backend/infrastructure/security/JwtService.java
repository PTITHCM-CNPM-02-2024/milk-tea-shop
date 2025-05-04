package com.mts.backend.infrastructure.security;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {
    final JwtProperties jwtProperties;
    
    @Override
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userPrincipal.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .claims(claims)
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis())).issuer(jwtProperties.getIssuer()).expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }
    
    @Override
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenVersion", userPrincipal.getTokenVersion());
        claims.put("role", userPrincipal.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .claims(claims)
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis())).issuer(jwtProperties.getIssuer())
                .expiration(new Date(new Date().getTime() + 60 * 60 * 24 * 1000 *7 )) // 7 day
                .signWith(getSigningKey())
                .id(UUID.randomUUID().toString())
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token, UserPrincipal userPrincipal) {
        final String username = extractUsername(token);

        return username.equals(userPrincipal.getUsername()) &&
                !isTokenExpired(token);}
    
    @Override
    public boolean validateRefreshToken(String token, UserPrincipal userPrincipal) {
        final String username = extractUsername(token);
        final Long tokenVersion = extractClaim(token, claims -> claims.get("tokenVersion", Long.class));
        
        return username.equals(userPrincipal.getUsername()) &&
                !isTokenExpired(token) && tokenVersion.equals(userPrincipal.getTokenVersion());
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clockSkewSeconds(60)
                .build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}