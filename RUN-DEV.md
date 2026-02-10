# Lancer l'app avec rechargement automatique (dev)

## Option 1 : Deux terminaux (recommandé)

1. **Terminal 1** – Démarrer l’application en mode dev :
   ```bat
   run-dev.bat
   ```
   Ou :
   ```bat
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```

2. **Terminal 2** – Lancer la surveillance et la recompilation à chaque modification :
   ```powershell
   powershell -ExecutionPolicy Bypass -File watch-compile.ps1
   ```
   À chaque sauvegarde dans `src/`, Maven recompile et DevTools redémarre l’app.

## Option 2 : Un seul terminal

1. Lancer :
   ```bat
   run-dev.bat
   ```
2. Après chaque modification de code ou de template, dans un autre terminal (ou le même après avoir arrêté l’app) exécuter :
   ```bat
   mvn compile
   ```
   L’application redémarrera toute seule grâce à DevTools.

## Option 3 : Depuis l’IDE (Cursor / VS Code / IntelliJ)

- Lancer la classe `SoftApplication` avec le paramètre VM ou programme :  
  `--spring.profiles.active=dev`
- La compilation à la sauvegarde + DevTools assurent le redémarrage automatique.

---

**Profil dev** : désactive le cache Thymeleaf et active le restart + LiveReload DevTools.
