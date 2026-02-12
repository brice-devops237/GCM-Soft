# GCM Soft

Application web de gestion des patients et de leurs paramètres cliniques (signes vitaux, constantes). Authentification par JWT, interface Thymeleaf avec toasts et messages flash.

---

## Table des matières

- [Stack technique](#stack-technique)
- [Prérequis](#prérequis)
- [Installation et configuration](#installation-et-configuration)
- [Lancement](#lancement)
- [Architecture du projet](#architecture-du-projet)
- [Fonctionnalités](#fonctionnalités)
- [Sécurité et authentification](#sécurité-et-authentification)
- [API et communication frontend / backend](#api-et-communication-frontend--backend)
- [Messages, toasts et exceptions](#messages-toasts-et-exceptions)
- [Scripts et commandes](#scripts-et-commandes)

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
│   └── JsonOrRedirectAuthenticationEntryPoint.java  # 401 → JSON ou redirection /login
└── modules/
    ├── ViewController.java           # Pages : /, /login, /dashboard, /logout, /profile, /settings
    ├── auth/                         # Module authentification
    │   ├── config/JwtProperties.java
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
    ├── fragments/                    # head, aside, navbar, footer, js, profile
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

## Scripts et commandes

| Action              | Commande |
|---------------------|----------|
| Compiler            | `mvn compile` |
| Lancer (dev)        | `run-dev.bat` ou `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"` |
| Package             | `mvn clean package` |
| Lancer le JAR       | `java -jar target/Soft-0.0.1-SNAPSHOT.jar` |

---

## Résumé des points clés

- **Frontend / backend** : formulaires Thymeleaf + appels `fetch` avec cookie JWT et `credentials: 'same-origin'` ; réponses JSON et flash cohérentes.
- **Toasts** : tous les retours utilisateur (succès, erreur, session expirée, recherche) passent par `AppToasts` (Notyf).
- **Exceptions** : erreurs métier et 401 gérées côté client (toast + redirection login si 401).
- **Documentation** : ce README décrit l’ensemble des fonctionnalités, la configuration, l’architecture et les flux d’authentification et de données.

Pour toute question ou évolution, se référer à la structure des modules et aux contrôleurs listés ci-dessus.
