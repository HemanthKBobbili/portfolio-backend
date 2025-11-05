package com.hkb.portfolio_backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class JwtTokenProvider {

    private final AppProperties appProperties;

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    public JwtTokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Generate JWT token based on authenticated user
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + getExpirationMs());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validate token integrity and expiration
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.info("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims string is empty");

        }
        return false;
    }

    /**
     * Retrieve secure signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(getSecret().getBytes());
    }

    private String getSecret() {
        return appProperties.getJwt().getSecret();
    }

    private long getExpirationMs() {
        return appProperties.getJwt().getExpirationMs();
    }
}
