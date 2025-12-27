package com.attendance.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // ============================
    // GENERATE TOKEN
    // ============================
    public String generateToken(Long userId, String role) {

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role) // ADMIN | ADMINISTRATION | TEACHER | STUDENT
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
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
        }
        catch (ExpiredJwtException e) {
            return false;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ============================
    // INTERNAL CLAIM PARSER
    // ============================
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
