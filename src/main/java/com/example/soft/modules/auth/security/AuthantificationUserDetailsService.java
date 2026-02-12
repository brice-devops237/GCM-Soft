package com.example.soft.modules.auth.security;

import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.services.AuthantificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implémentation de {@link UserDetailsService} qui charge un utilisateur depuis la table
 * {@code authantification} par son login.
 *
 * <p>
 * Utilisé par Spring Security pour l'authentification classique (formulaire). Pour l'authentification
 * JWT, le filtre {@link JwtAuthenticationFilter} utilise directement le token et n'appelle pas
 * ce service ; ce service reste utile si on ajoute un login form côté serveur ou des tests.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthantificationUserDetailsService implements UserDetailsService {

    private final AuthantificationService authantificationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authantificationService.getByLogin(username)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
    }

    private UserDetails toUserDetails(AuthantificationModel u) {
        return new User(
                u.getLogin(),
                u.getPassword(),
                true,
                true,
                true,
                true,
                Stream.of("ROLE_USER").map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }
}
