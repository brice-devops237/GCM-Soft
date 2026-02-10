@echo off
REM Lance l'application avec le profil dev (DevTools + rechargement auto)
echo Demarrage avec profil dev (auto-reload actif)...
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
