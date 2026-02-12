package com.example.soft.modules.auth.config;

import com.example.soft.modules.auth.models.AuthantificationModel;
import com.example.soft.modules.auth.services.AuthantificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Crée l'utilisateur <strong>root</strong> (mot de passe <strong>root</strong>) au démarrage
 * de l'application, uniquement s'il n'existe pas déjà en base.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RootUserInitializer implements ApplicationRunner {

    private static final String ROOT_LOGIN = "Root";
    private static final String ROOT_PASSWORD = "root";

    private final AuthantificationService authantificationService;

    @Override
    public void run(ApplicationArguments args) {
        if (authantificationService.getByLogin(ROOT_LOGIN).isPresent()) {
            log.debug("Utilisateur '{}' déjà présent en base, aucune création.", ROOT_LOGIN);
            return;
        }
        AuthantificationModel root = AuthantificationModel.builder()
                .nom("Root")
                .prenom("Administrateur")
                .login(ROOT_LOGIN)
                .password(ROOT_PASSWORD)
                .tel(0)
                .type("Administrateur")
                .matricule("ROOT")
                .service(0)
                .sexe("inconue")
                .build();
        try {
            authantificationService.create(root);
            log.info("Utilisateur '{}' créé avec succès (mot de passe : {}).", ROOT_LOGIN, ROOT_PASSWORD);
        } catch (Exception e) {
            log.error("Impossible de créer l'utilisateur '{}' au démarrage.", ROOT_LOGIN, e);
        }
    }
}
