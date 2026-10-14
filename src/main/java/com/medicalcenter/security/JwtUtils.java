package com.medicalcenter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET = "MedicalCenterSuperSecretKeyForJwtSigning2026!";
    private static final long EXPIRATION_TIME_MS = 24L * 60 * 60 * 1000; // 24 hours

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String role, Integer identifier) {
        String subject = (identifier != null) ? String.valueOf(identifier) : "0";

        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .claim("id", identifier)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key)
                .compact();
    }

    /**
     * Επιστρέφει τα claims αν το token είναι έγκυρο και δεν έχει λήξει.
     * Αν το token έχει λήξει ή αλλοιωθεί, επιστρέφει null αντί να κρασάρει.
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return false;
        }
        return claims.getExpiration() != null && !claims.getExpiration().before(new Date());
    }

    public String extractRole(Claims claims) {
        return claims != null ? claims.get("role", String.class) : null;
    }

    public Integer extractId(Claims claims) {
        return claims != null ? claims.get("id", Integer.class) : null;
    }
}