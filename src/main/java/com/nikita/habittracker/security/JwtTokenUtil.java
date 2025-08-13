package com.nikita.habittracker.security; // or com.nikita.habittracker.util

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class JwtTokenUtil {

    Logger logger = Logger.getLogger(JwtTokenUtil.class.getName());

    @Value("${jwt-secret}")
    private String secretKey;

    private Key key;

    @Value("${jwt-expiration-ms}")
    private int jwtExpMS;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate JWT token
    public String generateToken(String name) {
        return Jwts.builder()
                .setSubject(name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpMS)) // 10 hours
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate JWT token
    public boolean validateToken(String token,String username) {
        try
        {
            String tokenUsername = getClaimsFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Invalid JWT signature: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.log(Level.SEVERE, "JWT token is expired: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.log(Level.SEVERE, "JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Get username from JWT token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}
