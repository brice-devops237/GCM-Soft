package com.example.soft.modules.auth.services.impl;

import com.example.soft.modules.auth.dtos.LoginRequest;
import com.example.soft.modules.auth.dtos.LoginResponse;
import com.example.soft.modules.auth.dtos.UserInfoDto;
import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.services.AuthService;
import com.example.soft.modules.auth.services.AuthantificationService;
import com.example.soft.modules.auth.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implémentation du service d'authentification (login + JWT).
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthantificationService authantificationService;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return null;
        }
        return login(request.getUsername().trim(), request.getPassword());
    }

    @Override
    public LoginResponse login(String username, String password) {
        if (username == null || username.isBlank() || password == null) {
            return null;
        }
        Optional<AuthantificationModel> opt = authantificationService.getByLogin(username);
        if (opt.isEmpty()) {
            return null;
        }
        AuthantificationModel user = opt.get();
        if (!authantificationService.matchesPassword(user, password)) {
            return null;
        }
        String token = jwtService.generateToken(user.getId(), user.getLogin());
        UserInfoDto userInfo = toUserInfo(user);
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresInSeconds(jwtService.getExpirationSeconds())
                .user(userInfo)
                .build();
    }

    @Override
    public UserInfoDto toUserInfo(AuthantificationModel user) {
        if (user == null) return null;
        return UserInfoDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .login(user.getLogin())
                .type(user.getType())
                .matricule(user.getMatricule())
                .service(user.getService())
                .sexe(user.getSexe())
                .build();
    }

    @Override
    public UserInfoDto getCurrentUserInfo(Integer id) {
        if (id == null) return null;
        try {
            AuthantificationModel user = authantificationService.getById(id);
            return toUserInfo(user);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserInfoDto getCurrentUserInfoByLogin(String login) {
        if (login == null || login.isBlank()) return null;
        return authantificationService.getByLogin(login)
                .map(this::toUserInfo)
                .orElse(null);
    }
}
