package com.example.multilab.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtil {

    // ⚠️ En production : stocker dans application.properties ou variable d'env
    // jwt.secret=votre-cle-secrete-tres-longue-minimum-256-bits
    private static final String SECRET = "MultiLab-Secret-Key-Pour-JWT-Minimum-256-Bits-2025-Change-Moi-En-Production!";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 heures

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Génère un token JWT après login réussi
     */
    public String generateToken(int userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le username du token
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrait le userId du token
     */
    public int extractUserId(String token) {
        return (int) parseClaims(token).get("userId");
    }

    /**
     * Extrait le rôle du token
     */
    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    /**
     * Vérifie si le token est valide et non expiré
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
