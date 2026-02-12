package com.example.soft.modules.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propriétés de configuration pour la génération et la validation des JWT.
 *
 * <p>
 * Préfixe : {@code app.jwt}. Exemple dans application.properties :
 * <pre>
 * app.jwt.secret=MaCleSecreteLongueEtComplexePourSignerLesTokens
 * app.jwt.expiration-ms=86400000
 * app.jwt.cookie-name=token
 * </pre>
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /** Clé secrète pour signer les tokens (minimum 256 bits recommandé pour HS256). */
    private String secret = "GCM-Soft-JWT-Secret-ChangeMe-In-Production-Minimum-32-Chars";

    /** Durée de validité du token en millisecondes (défaut : 24 h). */
    private long expirationMs = 86400000L;

    /** Nom du cookie HTTP dans lequel le token peut être stocké (optionnel). */
    private String cookieName = "token";

    /** En-tête HTTP portant le token (Authorization: Bearer ...). */
    private String headerName = "Authorization";
}
