package com.lpk.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretEncoded;  // Should be base64 encoded key (from Render env)

    private SecretKey jwtSecretKey;

    private final long JWT_EXPIRATION = 86400000;  // 1 day

    @PostConstruct
public void init() {
    byte[] decodedKey = Base64.getDecoder().decode(jwtSecretEncoded);
    System.out.println("Decoded key length: " + decodedKey.length * 8 + " bits");
    jwtSecretKey = Keys.hmacShaKeyFor(decodedKey);
}


    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
