package com.example.soft.modules.auth.security;

import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.repositories.AuthantificationRepository;
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
 * Implémentation de {@link UserDetailsService} qui charge les utilisateurs
 * depuis la table {@code authantification}.
 *
 * <p>
 * Le "username" Spring Security correspond au champ {@code login} de la table.
 * Les rôles sont dérivés du champ {@code type} (ex: "ROLE_ADMIN", "ROLE_CAISSE").
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthantificationRepository authantificationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthantificationModel user = authantificationRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
        String role = user.getType() == null || user.getType().isBlank()
                ? "USER"
                : "ROLE_" + user.getType().toUpperCase().replace(" ", "_");
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}
