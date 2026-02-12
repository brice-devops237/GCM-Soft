# Lancer l'app avec rechargement automatique (dev)

**Pour que le rechargement fonctionne :**
1. L’app doit être lancée **avec le profil dev** (ex. `run-dev.bat`).
2. Après une modification de code Java ou de `pom.xml`, il faut **recompiler** (`mvn compile`) pour que DevTools détecte le changement dans `target/classes` et redémarre. Utiliser le script `watch-compile.ps1` en 2ᵉ terminal pour le faire automatiquement.

---

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
- Activer la **compilation automatique** (Build Automatically / Compile on save) pour que chaque sauvegarde mette à jour `target/classes` et déclenche le redémarrage DevTools.

---

## Dépannage : le rechargement ne se fait plus

| Cause | Solution |
|-------|----------|
| App lancée sans profil dev | Utiliser `run-dev.bat` ou ajouter `--spring.profiles.active=dev` au lancement. |
| Aucune recompilation après modification | Lancer `watch-compile.ps1` dans un 2ᵉ terminal, ou exécuter `mvn compile` après chaque modification. |
| Depuis l’IDE | Vérifier que le profil `dev` est bien passé et que la compilation à la sauvegarde est activée. |

**Profil dev** : désactive le cache Thymeleaf et active le restart + LiveReload DevTools.
