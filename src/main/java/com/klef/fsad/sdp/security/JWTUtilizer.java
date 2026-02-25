package com.klef.fsad.sdp.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtilizer {
    
    // Increased key length for security (HS256 requires >= 256 bits)
    private final String SECRET_KEY_STRING = "your_secret_key_here_change_this_to_strong_secret_key_must_be_at_least_256_bits";
    
    // Generate a secure key from the string bytes
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    private final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public String generateToken(String username, String role, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", id);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateJWTToken(Long id, String username, String role) {
        return generateToken(username, role, id);
    }
    
    public Map<String, String> validateToken(String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Claims claims = Jwts.parserBuilder() // Use parserBuilder for newer JJWT versions
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            response.put("code", "200");
            response.put("message", "Valid token");
            response.put("username", claims.getSubject());
            response.put("role", (String) claims.get("role"));
            response.put("id", String.valueOf(claims.get("id")));
            
        } catch (Exception e) {
            response.put("code", "401");
            response.put("message", "Invalid or expired token");
        }
        return response;
    }
}