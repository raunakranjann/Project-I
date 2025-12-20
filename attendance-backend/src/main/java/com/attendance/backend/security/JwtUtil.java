package com.attendance.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY =
            "SMART_ATTENDANCE_SUPER_SECRET_KEY_256_BIT_LONG";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24; // 24 hours

    private final Key key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ============================
    // GENERATE TOKEN
    // ============================
    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================
    // EXTRACT USER ID
    // ============================
    public Long extractUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    // ============================
    // EXTRACT ROLE
    // ============================
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ============================
    // VALIDATE TOKEN
    // ============================
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ============================
    // INTERNAL
    // ============================
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
