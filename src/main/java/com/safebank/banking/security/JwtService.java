
package com.safebank.banking.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {
        @Value("${jwt.secret}")
private String secretKey;

//     // Secret key used to sign JWT
//     private static final String SECRET_KEY =
//             "SAFE_BANK_SECRET_KEY_2026_SECURE_JWT_KEY_123456789";

    // Token validity: 1 hour
    private static final long EXPIRATION_TIME =
            1000 * 60 * 60;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }


    // ==========================================
    // GENERATE JWT
    // ==========================================

    public String generateToken(String email ,String role ) {

        return Jwts.builder()

                .subject(email)
                .claim("role", role)

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }
    
    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }


    // ==========================================
    // EXTRACT EMAIL
    // ==========================================

    public String extractEmail(String token) {

        return getClaims(token)
                .getSubject();
    }


    // ==========================================
    // VALIDATE TOKEN
    // ==========================================

    public boolean isTokenValid(
            String token,
            String email) {

        try {

            String tokenEmail =
                    extractEmail(token);

            return tokenEmail.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }


    // ==========================================
    // CHECK EXPIRATION
    // ==========================================

    private boolean isTokenExpired(
            String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }


    // ==========================================
    // GET CLAIMS
    // ==========================================

    private Claims getClaims(
            String token) {

        return Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}