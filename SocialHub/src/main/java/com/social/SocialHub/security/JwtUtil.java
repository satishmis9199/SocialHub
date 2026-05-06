package com.social.SocialHub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey123";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 🔐 Generate Token
    public String generateToken(UUID id, String username, String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id.toString())   // UUID → String
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key)
                .compact();
    }

    // 🔍 Extract all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 👤 Username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🆔 UUID (IMPORTANT FIX)
    public UUID extractId(String token) {
        String id = extractAllClaims(token).get("id", String.class);
        return UUID.fromString(id);
    }

    // 🎭 Role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ⏳ Expiry check
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // ✅ Validate Token (STRONG)
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}