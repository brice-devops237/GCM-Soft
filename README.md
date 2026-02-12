# GCM Soft

Application web de **gestion des patients** et de leurs **paramètres cliniques** (signes vitaux, constantes : PAM, FC, FR, température, SaO2, PAS, PAD, poids, taille, IMC, etc.). Conçue pour un usage en contexte médical ou paramédical, avec authentification par **JWT**, interface **Thymeleaf** (Bootstrap 5), **toasts** (Notyf) et messages flash.

---

## Table des matières

- [Stack technique](#stack-technique)
- [Prérequis](#prérequis)
- [Installation et configuration](#installation-et-configuration)
- [Premier démarrage et utilisateur par défaut](#premier-démarrage-et-utilisateur-par-défaut)
- [Lancement](#lancement)
- [Architecture du projet](#architecture-du-projet)
- [Fonctionnalités](#fonctionnalités)
- [Interface utilisateur (menu, profil, déconnexion)](#interface-utilisateur-menu-profil-déconnexion)
- [Formulaire patient (nationalité, Select2)](#formulaire-patient-nationalité-select2)
- [Sécurité et authentification](#sécurité-et-authentification)
- [API et communication frontend / backend](#api-et-communication-frontend--backend)
- [Messages, toasts et exceptions](#messages-toasts-et-exceptions)
- [Routes principales](#routes-principales)
- [Variables d'environnement (production)](#variables-denvironnement-production)
- [Scripts et commandes](#scripts-et-commandes)
- [Dépannage](#dépannage)

---

## Stack technique

| Couche        | Technologie                          |
|---------------|--------------------------------------|
| Backend       | Java 17, Spring Boot 4.x              |
| Sécurité      | Spring Security, JWT (JJWT 0.12)      |
| Persistance   | Spring Data JPA, Hibernate, MySQL 8   |
| Migrations    | Flyway                               |
| Web           | Spring MVC, Thymeleaf                |
| Frontend      | HTML/CSS/JS, Bootstrap 5, Notyf (toasts) |

---

## Prérequis

- **JDK 17**
- **Maven 3.8+**
- **MySQL 8** (serveur local ou distant)
- Navigateur moderne (Chrome, Firefox, Edge)

---

## Installation et configuration

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd Soft
```

### 2. Base de données MySQL

- Créer une base (ou laisser Spring la créer si `createDatabaseIfNotExist=true`).
- Configurer les identifiants dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/company_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

### 3. JWT (authentification)

Dans `application.properties` :

```properties
app.jwt.secret=GCM-Soft-JWT-Secret-ChangeMe-In-Production-Minimum-32-Chars
app.jwt.expiration-ms=86400000
app.jwt.cookie-name=token
```

- **Production** : utiliser une clé forte (variable d’environnement) et une durée d’expiration adaptée.

### 4. Profils

- **prod** (défaut) : `spring.profiles.active=prod`
- **dev** : rechargement automatique (DevTools), Thymeleaf sans cache, logs plus verbeux. Fichier : `application-dev.properties`.

---

## Premier démarrage et utilisateur par défaut

Au premier lancement, si aucun utilisateur n’existe en base, un **utilisateur racine** est créé automatiquement :

| Champ    | Valeur  |
|----------|---------|
| Login    | `root`  |
| Mot de passe | `root`  |

**Important** : en production, changez immédiatement le mot de passe de cet utilisateur ou désactivez ce compte après avoir créé un compte administrateur.

---

## Lancement

### Développement (rechargement automatique)

```bash
# Windows
run-dev.bat

# Ou avec Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production

```bash
mvn clean package
java -jar target/Soft-0.0.1-SNAPSHOT.jar
```

L’application est accessible sur **http://localhost:9111** (port configurable dans `application.properties` : `server.port`).

---

## Architecture du projet

```
src/main/java/com/example/soft/
├── SoftApplication.java              # Point d’entrée Spring Boot
├── config/                           # Configuration globale
│   ├── SecurityConfig.java           # Sécurité (JWT, routes publiques/protégées)
│   ├── CurrentUserModelAdvice.java   # Injection username + servletPath (menu actif, profil)
│   └── JsonOrRedirectAuthenticationEntryPoint.java  # 401 → JSON ou redirection /login
└── modules/
    ├── ViewController.java           # Pages : /, /login, /dashboard, /logout, /profile, /settings
    ├── auth/                         # Module authentification
    │   ├── config/
    │   │   ├── JwtProperties.java
    │   │   └── RootUserInitializer.java   # Utilisateur root au premier démarrage
    │   ├── controllers/
    │   │   ├── AuthController.java       # POST /auth/login, /api/auth/login, /api/auth/me, /auth/logout
    │   │   └── AuthantificationController.java  # CRUD /api/auth/users
    │   ├── dtos/                     # LoginRequest, LoginResponse, UserInfoDto, AuthantificationDto
    │   ├── models/AuthantificationModel.java
    │   ├── repositories/AuthantificationRepository.java
    │   ├── security/
    │   │   ├── JwtAuthenticationFilter.java
    │   │   ├── CustomUserDetailsService.java
    │   │   └── AuthantificationUserDetailsService.java
    │   ├── services/
    │   │   ├── AuthService.java, JwtService.java, AuthantificationService.java
    │   │   └── impl/
    │   └── serviceImpl/               # (vide après nettoyage)
    ├── patients/
    │   ├── controllers/PatientsController.java
    │   ├── models/PatientsModel.java
    │   ├── repositories/PatientsRepository.java
    │   └── services/
    └── paramatre_patients/
        ├── controllers/ParametresPatientController.java
        ├── models/ParametresPatientModel.java
        ├── repositories/ParametresPatientRepository.java
        └── services/
```

```
src/main/resources/
├── application.properties            # Config principale (datasource, JPA, Flyway, JWT)
├── application-dev.properties       # Profil dev
├── db/migration/                     # Scripts Flyway (V1__baseline.sql)
├── static/                           # JS, CSS, assets
│   ├── js/
│   │   ├── app-toasts.js             # AppToasts.success / .error / .warning / .info (Notyf)
│   │   └── patients/                 # patients-core, patients-filters, patients-form, etc.
│   └── assets/
└── templates/
    ├── fragments/                    # head, aside, navbar, footer, js, profile, modal-confirm
    │   └── patients/
    │       ├── content/               # patients-table (tableau)
    │       └── modals/                # modal-patient, modal-parametres, modal-patient-nationalite-options
    └── pages/
        ├── authentification/login.html
        ├── dashboard.html
        └── patients/index.html       # Liste, filtres, modals patient + paramètres
```

---

## Fonctionnalités

### Authentification

- **Connexion**  
  - Page : `GET /login`.  
  - Formulaire : `POST /auth/login` (champs `username`, `password`) → cookie JWT + redirection vers `/dashboard`.  
  - En cas d’échec : redirection vers `/login` avec message flash `error`.
- **API login**  
  - `POST /api/auth/login` (JSON `{ "username", "password" }`) → `{ "token", "type", "expiresInSeconds", "user" }`.
- **Profil connecté**  
  - `GET /api/auth/me` (cookie ou header `Authorization: Bearer <token>`) → infos utilisateur (UserInfoDto).
- **Déconnexion**  
  - `GET /logout` ou `POST/GET /auth/logout` → suppression du cookie JWT + redirection vers `/login`.
- **Pages**  
  - `/` → redirection vers `/login`.  
  - `/dashboard` : page d’accueil après connexion (messages flash affichés en toast).  
  - `/profile`, `/settings` → redirection vers `/dashboard` (à compléter selon besoin).

### Gestion des patients

- **Liste** : `GET /patients` (vue Thymeleaf avec tableau).
- **Création** : formulaire modal → `POST /patients/create` → message flash `success` ou `error`.
- **Modification** :  
  - Chargement des données : `GET /patients/api/{id}` (JSON).  
  - Soumission : `POST /patients/edit/{id}`.  
  - Messages flash selon succès/erreur.
- **Suppression** : `GET /patients/delete/{id}` → message flash.
- **Activation / Désactivation** : `GET /patients/activate/{id}`, `GET /patients/deactivate/{id}`.
- **Recherche et filtres** :  
  - `GET /patients/search` (paramètres : code, noms, sexe, typePatient, categorie, estActif, dateDebut, dateFin).  
  - Recherche AJAX en temps réel : `GET /patients/api/search?...` (JSON), avec debounce et spinner.

### Paramètres cliniques (par patient)

- **Affichage** : sélection d’un patient → chargement des paramètres via `GET /parametres-patient/by-code?codePatient=...` (JSON).
- **Création** : formulaire modal → `POST /parametres-patient/create` → redirection avec message flash.
- **Modification** :  
  - Chargement : `GET /parametres-patient/get/{id}` (JSON).  
  - Soumission : `POST /parametres-patient/update` (FormData) → réponse JSON `{ "success", "message" }` → toast sans rechargement.
- **Suppression** : `GET /parametres-patient/delete/{id}` → redirection puis rechargement de la liste des paramètres côté client.

Champs gérés (entre autres) : date, heure, PAM (ta), FC, FR, température, SaO2, PAS, PAD, poids, taille, IMC, source, DDR, autres.

---

## Interface utilisateur (menu, profil, déconnexion)

- **Menu latéral (aside)** : lien « Tableau de bord » vers `/dashboard`, lien « Patients » vers `/patients`. L’élément actif est déterminé par le chemin de la requête (`servletPath`) fourni par `CurrentUserModelAdvice`.
- **Profil (navbar)** : affichage du **login** de l’utilisateur connecté (pas le rôle dans ce bloc). Données injectées via `CurrentUserModelAdvice` (`username`, `servletPath`).
- **Déconnexion** : liens « Déconnexion » (aside et profil) ouvrent un **modal de confirmation** avant redirection vers `/logout`. Le cookie JWT est supprimé, un message flash « Déconnexion réussie » peut être affiché sur la page de login.
- **Session expirée** : si le JWT est invalide ou expiré, le filtre redirige vers `/login?session=expired` ; la page login affiche un toast d’erreur puis nettoie l’URL.

---

## Formulaire patient (nationalité, Select2)

- Le champ **Nationalité** est un **select** enrichi avec **Select2** (recherche, liste déroulante).
- Les options sont des **gentilés** (nationalités) en français : *Camerounais(e)*, *Français(e)*, *Sénégalais(e)*, *Ivoirien(ne)*, *Belge*, *Américain(e)*, etc. (liste complète dans `templates/fragments/patients/modals/modal-patient-nationalite-options.html`).
- Select2 est initialisé sur la page patients avec `dropdownParent: $('#modalPatient')` pour un affichage correct dans le modal. En édition, la valeur chargée est synchronisée avec `trigger('change')`.

---

## Sécurité et authentification

- **Mécanisme** : JWT (HMAC-SHA256), stocké en cookie (nom configurable : `app.jwt.cookie-name`) ou envoyé en en-tête `Authorization: Bearer <token>`.
- **Filtre** : `JwtAuthenticationFilter` lit le token (cookie ou header), valide et remplit le `SecurityContext`. Pas de session serveur (stateless).
- **Routes publiques** (sans token) :  
  `/`, `/login`, `/login.html`, `/logout`, `/auth/login`, `/auth/logout`,  
  `/api/auth/login`, `/api/auth/register`, `/api/auth/logout`,  
  `/assets/**`, `/css/**`, `/js/**`, `/img/**`, `/error`, `/favicon.ico`, `/webjars/**`.
- **Toutes les autres routes** (y compris `/dashboard`, `/patients`, `/parametres-patient/**`, `/api/**` hors auth) nécessitent un JWT valide.
- **401** : pour les requêtes vers `/api/*` ou `Accept: application/json`, réponse JSON `{ "error": "Non authentifié" }` ; sinon redirection vers `/login`.

---

## API et communication frontend / backend

- **Cookie** : les requêtes `fetch` utilisent `credentials: 'same-origin'` pour envoyer le cookie JWT sur les appels relatifs au même domaine.
- **Réponses JSON** :  
  - Patients : `GET /patients/api/{id}`, `GET /patients/api/search?...` → objet ou liste.  
  - Paramètres : `GET /parametres-patient/by-code?codePatient=...`, `GET /parametres-patient/get/{id}` → objet ou liste.  
  - Mise à jour paramètres : `POST /parametres-patient/update` → `{ "success": true|false, "message": "..." }`.
- **Erreurs métier** : retournées en JSON (ex. `message` pour les paramètres) ou via redirect + flash (`success` / `error`) pour les formulaires classiques.
- **Cohérence** : les noms de champs (ex. `ta` pour PAM, `codePatient`, etc.) sont alignés entre le modèle backend, les formulaires et le JS.

---

## Messages, toasts et exceptions

- **Bibliothèque** : Notyf, exposée via `window.AppToasts` (`app-toasts.js`) :  
  `AppToasts.success(message)`, `AppToasts.error(message)`, `AppToasts.warning(message)`, `AppToasts.info(message)`.
- **Messages flash (serveur)** :  
  - **Login** : `error` affiché en alerte sur la page `/login`.  
  - **Dashboard** : `success` et `error` affichés en toast au chargement (attributs `data-flash-toast` + `data-message`).  
  - **Patients** : `success` et `error` convertis en toasts par `showFlashAsToasts()` et les alertes inline sont masquées/supprimées.
- **Erreurs formulaire patient** : messages backend mappés aux champs (ex. `error-noms`, `error-sexe`) et affichés sous les champs + toast si besoin.
- **API (fetch)** :  
  - Succès : toasts avec le message retourné (ex. « Les paramètres ont été modifiés avec succès »).  
  - Erreur : `AppToasts.error(...)` avec message serveur ou message générique.  
  - **401** : fonction `checkAuthResponse(response)` sur les pages protégées → toast « Session expirée. Veuillez vous reconnecter. » + redirection vers `/login`.
- **Recherche patients** : en cas d’erreur (hors annulation), toast « Erreur lors de la recherche des patients. ».

---

## Routes principales

| Méthode | Route | Description |
|--------|--------|-------------|
| GET | `/` | Redirection vers `/login` |
| GET | `/login` | Page de connexion |
| POST | `/auth/login` | Connexion (formulaire) → cookie JWT + redirect |
| GET/POST | `/auth/logout` | Déconnexion (suppression cookie) |
| GET | `/dashboard` | Tableau de bord (après connexion) |
| GET | `/profile`, `/settings` | Redirection vers dashboard |
| GET | `/patients` | Liste des patients (vue Thymeleaf) |
| POST | `/patients/create` | Création patient |
| GET | `/patients/api/{id}` | Détail patient (JSON) |
| POST | `/patients/edit/{id}` | Modification patient |
| GET | `/patients/delete/{id}` | Suppression patient |
| GET | `/patients/api/search?...` | Recherche patients (JSON, paramètres : code, noms, sexe, dateDebut, dateFin) |
| GET | `/parametres-patient/by-code?codePatient=...` | Paramètres d’un patient (JSON) |
| POST | `/parametres-patient/create` | Création paramètres |
| GET | `/parametres-patient/get/{id}` | Détail paramètres (JSON) |
| POST | `/parametres-patient/update` | Mise à jour paramètres (JSON) |
| GET | `/parametres-patient/delete/{id}` | Suppression paramètres |
| POST | `/api/auth/login` | Connexion API (JSON) → token |
| GET | `/api/auth/me` | Utilisateur connecté (JSON) |

---

## Variables d'environnement (production)

En production, il est recommandé de ne pas stocker les secrets dans les fichiers de configuration. Exemple avec des variables d’environnement :

| Variable | Exemple | Usage |
|----------|---------|--------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://serveur:3306/company_db?...` | URL JDBC MySQL |
| `SPRING_DATASOURCE_USERNAME` | `app_user` | Utilisateur BDD |
| `SPRING_DATASOURCE_PASSWORD` | `***` | Mot de passe BDD |
| `APP_JWT_SECRET` | Clé longue (≥ 32 caractères) | Signature JWT |
| `APP_JWT_EXPIRATION_MS` | `86400000` | Expiration token (ms) |
| `SERVER_PORT` | `9111` | Port HTTP |

Sous Linux/macOS : `export APP_JWT_SECRET=...` puis `java -jar target/Soft-0.0.1-SNAPSHOT.jar`. Sous Windows : définir les variables système ou utiliser un fichier `.env` avec un lanceur adapté.

---

## Scripts et commandes

| Action              | Commande |
|---------------------|----------|
| Compiler            | `mvn compile` |
| Lancer (dev)        | `run-dev.bat` ou `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"` |
| Package             | `mvn clean package` |
| Lancer le JAR       | `java -jar target/Soft-0.0.1-SNAPSHOT.jar` |

---

## Dépannage

| Problème | Piste de résolution |
|----------|----------------------|
| **Port 9111 déjà utilisé** | Changer `server.port` dans `application.properties` ou arrêter le processus qui utilise le port. |
| **Erreur de connexion MySQL** | Vérifier que MySQL est démarré, que l’URL/hôte/port sont corrects, et que l’utilisateur a les droits sur la base. `allowPublicKeyRetrieval=true` peut être nécessaire avec MySQL 8. |
| **Page blanche ou 404 sur les assets** | Vérifier que les chemins dans `head.html` / `js.html` correspondent aux fichiers sous `static/` (ex. `/assets/`, `/js/`). |
| **Token invalide / boucle de redirection** | Supprimer le cookie `token` (nom configuré par `app.jwt.cookie-name`) et se reconnecter. Vérifier que la clé JWT n’a pas changé entre deux démarrages si des tokens sont encore valides. |
| **Select2 ne s’affiche pas (nationalité)** | S’assurer que la librairie Select2 (CSS/JS) est chargée sur la page patients et que l’initialisation JS s’exécute après le chargement du DOM (et éventuellement après l’ouverture du modal). |
| **Flyway : erreur de migration** | Vérifier que les scripts sous `db/migration/` sont cohérents avec l’état de la base. En dev, `spring.flyway.baseline-on-migrate=true` permet de créer une baseline si la base existait déjà. |

---

## Résumé des points clés

- **Frontend / backend** : formulaires Thymeleaf + appels `fetch` avec cookie JWT et `credentials: 'same-origin'` ; réponses JSON et flash cohérentes.
- **Toasts** : tous les retours utilisateur (succès, erreur, session expirée, recherche) passent par `AppToasts` (Notyf).
- **Exceptions** : erreurs métier et 401 gérées côté client (toast + redirection login si 401).
- **Utilisateur par défaut** : `root` / `root` créé au premier démarrage si la base est vide.
- **UI** : menu actif selon `servletPath`, profil (login), déconnexion avec modal de confirmation.
- **Patient** : champ nationalité en Select2 avec liste de gentilés (Camerounais(e), Français(e), etc.).
- **Documentation** : ce README décrit l’ensemble des fonctionnalités, la configuration, l’architecture et les flux d’authentification et de données.

Pour toute question ou évolution, se référer à la structure des modules et aux contrôleurs listés ci-dessus.
