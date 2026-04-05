package com.example.ai.assistant.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            logger.info("Токен прошел все проверки: действительный.");
            return true;
        } catch (SignatureException ex) {
            logger.error("Неверная подпись JWT (ключ токена не совпадает с секретным ключом сервера).", ex);
        } catch (MalformedJwtException ex) {
            logger.error("Некорректный JWT (неправильный формат).", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Срок действия JWT истек (Expired).", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("Неподдерживаемый JWT.", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT пуст (Illegal argument).", ex);
        }
        return false;
    }
}
