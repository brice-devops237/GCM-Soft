package com.example.soft.modules.auth.services;

import com.example.soft.modules.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service de génération et de validation des tokens JWT.
 *
 * <p>
 * Utilise HMAC-SHA256 pour la signature. La clé secrète est lue depuis
 * {@link JwtProperties}. Le sujet du token est le login de l'utilisateur ;
 * les claims contiennent l'id et le login pour éviter des allers-retours en base.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Génère un token JWT pour l'utilisateur identifié par id et login.
     *
     * @param id    identifiant de l'utilisateur
     * @param login login (sujet du token)
     * @return token JWT signé
     */
    public String generateToken(Integer id, String login) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(login)
                .claim("id", id)
                .claim("login", login)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Valide le token et extrait le login (sujet).
     *
     * @param token token JWT
     * @return le login si le token est valide
     * @throws ExpiredJwtException si le token est expiré
     * @throws io.jsonwebtoken.JwtException si signature invalide ou format incorrect
     */
    public String getLoginFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * Valide le token et extrait l'id utilisateur (claim "id").
     *
     * @param token token JWT
     * @return l'id utilisateur
     */
    public Integer getIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object id = claims.get("id");
        if (id instanceof Number) {
            return ((Number) id).intValue();
        }
        return null;
    }

    /**
     * Vérifie que le token est valide (signature et expiration).
     *
     * @param token token JWT
     * @return true si valide
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Token invalide ou expiré: {}", e.getMessage());
            return false;
        }
    }

    /** Durée de validité en secondes (pour la réponse API). */
    public long getExpirationSeconds() {
        return jwtProperties.getExpirationMs() / 1000;
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
