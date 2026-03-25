package com.projectmanagement.clientprojectmanagement.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Secret key — must be at least 256 bits (32 characters)
    // In production: store this in application.properties or environment variable!
    private static final String SECRET = "projecthub-super-secret-key-2026-abcdefghij";
    private static final long EXPIRY   = 1000 * 60 * 60 * 24; // 24 hours in ms

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ── Generate token ────────────────────────────────────────
    // Called after successful login
    // Stores userId, email, role inside the token
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email",  email);
        claims.put("role",   role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Validate token ────────────────────────────────────────
    // Returns true if token is valid and not expired
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Extract claims ────────────────────────────────────────
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token)  { return extractClaims(token).getSubject(); }
    public String extractRole(String token)   { return (String) extractClaims(token).get("role"); }
    public Long   extractUserId(String token) {
        Object id = extractClaims(token).get("userId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }
}