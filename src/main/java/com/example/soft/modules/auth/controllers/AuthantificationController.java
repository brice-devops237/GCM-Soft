package com.example.soft.modules.auth.controllers;

import com.example.soft.modules.auth.dtos.AuthantificationDto;
import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.services.AuthantificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion CRUD des utilisateurs (table {@code authantification}).
 *
 * <p>
 * Tous les endpoints nécessitent une authentification par token JWT.
 * </p>
 *
 * <p>
 * Endpoints :
 * <ul>
 *     <li>GET /api/auth/users : liste tous les utilisateurs (DTO sans mot de passe)</li>
 *     <li>GET /api/auth/users/{id} : détail d'un utilisateur</li>
 *     <li>POST /api/auth/users : création (mot de passe hashé côté service)</li>
 *     <li>PUT /api/auth/users/{id} : mise à jour</li>
 *     <li>DELETE /api/auth/users/{id} : suppression</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class AuthantificationController {

    private final AuthantificationService authantificationService;

    @GetMapping
    public ResponseEntity<List<AuthantificationDto>> getAll() {
        List<AuthantificationDto> list = authantificationService.getAll().stream()
                .map(AuthantificationDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthantificationDto> getById(@PathVariable Integer id) {
        try {
            AuthantificationModel u = authantificationService.getById(id);
            return ResponseEntity.ok(AuthantificationDto.fromEntity(u));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AuthantificationModel user) {
        try {
            AuthantificationModel created = authantificationService.create(user);
            return ResponseEntity.status(201).body(AuthantificationDto.fromEntity(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AuthantificationModel user) {
        try {
            AuthantificationModel updated = authantificationService.update(id, user);
            return ResponseEntity.ok(AuthantificationDto.fromEntity(updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            authantificationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
