package com.example.soft.modules.auth.services.impl;

import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.repositories.AuthantificationRepository;
import com.example.soft.modules.auth.services.AuthantificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des utilisateurs (table {@code authantification}).
 *
 * <p>
 * Utilise BCrypt pour la vérification des mots de passe. Les mots de passe existants
 * en base (éventuellement en clair ou ancien hash) peuvent être migrés progressivement
 * vers BCrypt lors de la première connexion réussie.
 * </p>
 *
 * <p>
 * Toutes les méthodes d'écriture sont transactionnelles ; les lectures sont en {@code readOnly}
 * lorsque c'est pertinent.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthantificationServiceImpl implements AuthantificationService {

    private final AuthantificationRepository authantificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthantificationModel create(AuthantificationModel user) {
        if (user == null) throw new IllegalArgumentException("Utilisateur null");
        if (user.getLogin() == null || user.getLogin().isBlank())
            throw new IllegalArgumentException("Login obligatoire");
        if (authantificationRepository.existsByLogin(user.getLogin()))
            throw new IllegalArgumentException("Un utilisateur existe déjà avec le login : " + user.getLogin());
        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new IllegalArgumentException("Mot de passe obligatoire");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return authantificationRepository.save(user);
    }

    @Override
    public AuthantificationModel update(Integer id, AuthantificationModel user) {
        if (id == null || user == null) throw new IllegalArgumentException("ID ou utilisateur null");
        AuthantificationModel existing = getById(id);
        user.setId(existing.getId());
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return authantificationRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthantificationModel getById(Integer id) {
        if (id == null) throw new IllegalArgumentException("ID null");
        return authantificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur trouvé avec l'ID " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthantificationModel> getByLogin(String login) {
        if (login == null || login.isBlank()) return Optional.empty();
        return authantificationRepository.findByLogin(login.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthantificationModel> getAll() {
        return authantificationRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        AuthantificationModel user = getById(id);
        authantificationRepository.delete(user);
    }

    @Override
    public boolean matchesPassword(AuthantificationModel user, String rawPassword) {
        if (user == null || rawPassword == null) return false;
        String encoded = user.getPassword();
        if (encoded == null || encoded.isBlank()) return false;
        if (encoded.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, encoded);
        }
        return encoded.equals(rawPassword);
    }
}
