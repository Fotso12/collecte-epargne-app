# CI/CD Workflow - Collecte Épargne App

## Overview
Ce projet utilise GitHub Actions pour automatiser la construction, les tests et le déploiement des trois composants de l'application :
- Backend : Spring Boot (Java 17)
- Frontend Web : Angular (Node.js)
- Frontend Mobile : Flutter

## Architecture CI/CD

### Workflow déclencheurs
- **Push** sur branches `main` et `test-integration`
- **Pull Requests** vers `main` et `test-integration`

### Jobs du Pipeline

#### 1. Build Backend (Maven)
- Java 17 avec Maven
- Compile le code source
- Exécute les tests (optionnel : `-DskipTests`)
- Génère un JAR exécutable
- Crée une image Docker

**Dockerfile Backend** :
- Build multi-stage
- Maven 3.9.4 pour la compilation
- JRE Alpine 17 pour le runtime
- Expose le port 8082
- Inclut un health check

#### 2. Build Frontend Web (Angular)
- Node.js 18.x
- Installe les dépendances npm
- Compile le code Angular
- Génère les fichiers static optimisés

**Dockerfile Web** :
- Build multi-stage
- Node.js Alpine pour la construction
- Nginx Alpine pour servir les fichiers
- Expose le port 80
- Configuration Nginx avec routing Angular

#### 3. Build Mobile (Flutter Web)
- Flutter 3.19.0
- Analyse du code avec `flutter analyze`
- Build web avec `flutter build web --release`
- Génère les fichiers static pour servir via Nginx

#### 4. Docker Build & Push
- Se déclenche après le succès de tous les jobs précédents
- Crée les images Docker pour backend, web et mobile
- Pousse les images vers Docker Hub avec deux tags :
  - `latest`
  - hash du commit (version spécifique)

**Identifiants Docker Hub** :
```
Username: darryl1234
Password: Configuré dans GitHub Secrets (DOCKER_PASSWORD)
```

Les identifiants seront automatiquement injectés lors de l'exécution du workflow.

## Configuration GitHub Secrets

Pour que le workflow fonctionne, les secrets GitHub sont déjà configurés :

1. `DOCKER_USERNAME`: `darryl1234`
2. `DOCKER_PASSWORD`: Token Docker Hub configuré dans GitHub Secrets

Les identifiants seront automatiquement récupérés lors de l'exécution du workflow.

## Fichiers d'Ignore

### Root (.gitignore)
Exclut les fichiers compilés, logs, et dépendances de tous les sous-projets.

### Backend (.gitignore)
- `target/` - Dossier Maven
- `.m2/` - Cache Maven
- `logs/` - Fichiers logs
- Fichiers IDE (.vscode, .idea, *.iml)

### Frontend Web (.gitignore)
- `node_modules/` - Dépendances npm
- `dist/` - Build compilé
- `.angular/` - Cache Angular
- `npm-debug.log`, `yarn-error.log`

### Frontend Mobile (.gitignore)
- `.dart_tool/` - Cache Dart
- `build/` - Artefacts de build
- `pubspec.lock` - Dépendances verrouillées
- `android/build/`, `android/.gradle/` - Build Android
- `ios/Pods/` - Dépendances iOS
- `*.apk`, `*.aab`, `*.so` - Artefacts compilés

## Docker Compose Local

Fichier `docker-compose.yml` fourni pour le déploiement local :

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter les services
docker-compose down

# Voir les logs
docker-compose logs -f
```

### Initialisation de la Base de Données

Le fichier SQL se trouvant dans `docker/mysql-init/01-init.sql` est automatiquement exécuté lors du premier démarrage du conteneur MySQL.

**Contenu initié** :
- Tables : institutions, users, roles, clients, collectors, accounts, transactions, etc.
- Données de base :
  - 1 institution par défaut (ID: 1, Code: DEF001)
  - 4 rôles : Admin, Superviseur, Collecteur, Auditeur
  - 1 utilisateur admin (admin@savings.local)

Pour réinitialiser la base de données :
```bash
# Arrêter Docker Compose
docker-compose down -v

# Redémarrer (reconstruit la BD)
docker-compose up -d
```

### Services
- **MySQL** : Port 3306
  - Database: `savings_collector`
  - User: `epargne_user`
  - Password: `epargne_password`
  - Init Scripts: `/docker/mysql-init/*.sql`

- **Backend** : Port 8082
  - URL: `http://localhost:8082`
  - Health check: `http://localhost:8082/actuator/health`

- **Web** : Port 80
  - URL: `http://localhost`
  - API proxy vers backend automatique

- **Mobile Web** : Port 3000
  - URL: `http://localhost:3000`
  - Version web de l'app Flutter
  - API proxy vers backend ngrok

## Étapes de déploiement

### Déploiement automatique (GitHub Actions)

1. **Commit et Push** sur `test-integration` :
   ```bash
   git add .
   git commit -m "Feature: Description du changement"
   git push origin test-integration
   ```

2. **GitHub Actions** s'exécute automatiquement :
   - ✅ Build backend
   - ✅ Build web
   - ✅ Check mobile
   - ✅ Build & Push images Docker

3. **Vérifier le statut** :
   - Accédez à : `GitHub > Actions > Workflows`
   - Consultez les logs pour chaque job

4. **Images Docker disponibles** :
   - `darryl1234/collecte-epargne-backend:latest`
   - `darryl1234/collecte-epargne-web:latest`
   - `darryl1234/collecte-epargne-mobile:latest`

### Déploiement manual

Pour tester localement avant de pusher :

```bash
# Backend
cd backend/collecte-epargne-backend
mvn clean package -DskipTests
docker build -t darryl1234/collecte-epargne-backend:latest .

# Web
cd frontend-web/SAVELYWEB
npm install
npm run build
docker build -t darryl1234/collecte-epargne-web:latest .

# Démarrer avec Docker Compose
docker-compose up -d
```

## Configuration ngrok pour Frontend Mobile

Pour exposer le backend et le web au frontend mobile en développement :

### Installation ngrok
```bash
# Télécharger depuis https://ngrok.com/download
# Ou via Chocolatey (Windows)
choco install ngrok
```

### Configuration
Un fichier `ngrok.yml` est fourni avec la configuration complète :
- **Auth Token** : `38Flnn9jDrFguLYVfJGCysq1Ui7_3LD1yyXFfiPZcGEYstThM`
- **Backend Tunnel** : `darryl-backend.ngrok.io` → `localhost:8082`
- **Web Tunnel** : `darryl-web.ngrok.io` → `localhost:80`

### Lancement ngrok
```bash
# Démarrer ngrok avec la configuration
ngrok start --all

# Ou démarrer un tunnel spécifique
ngrok start backend
ngrok start frontend-web
```

### Mise à jour du Frontend Mobile
Dans `frontend-mobile/savely/lib/services/`, mettez à jour les URLs pour utiliser ngrok :

**auth_api.dart** :
```dart
static const String ngrokUrl = 'https://darryl-backend.ngrok.io';
```

**client_service.dart** (ou équivalent) :
```dart
static const String apiUrl = 'https://darryl-backend.ngrok.io/api';
```

**Note** : Les URLs ngrok sont dynamiques et changent à chaque redémarrage. 
Vérifiez les URLs actuelles lors du lancement de ngrok.

## Troubleshooting

### Les tests Maven échouent
Utilisez `-DskipTests` pour ignorer les tests

### Node modules non trouvés
```bash
cd frontend-web/SAVELYWEB
rm -rf node_modules package-lock.json
npm install
```

### Flutter analyse échoue
```bash
cd frontend-mobile/savely
flutter clean
flutter pub get
flutter analyze
```

### Connexion Docker échoue
Vérifiez les secrets GitHub :
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`

## Fichiers créés/modifiés

- `.github/workflows/ci-cd.yml` - Workflow GitHub Actions
- `Dockerfile` (backend) - Image backend
- `Dockerfile` (web) - Image web
- `nginx.conf` (web) - Configuration serveur web
- `.dockerignore` - Exclusions Docker
- `docker-compose.yml` - Déploiement local
- `.gitignore` (root + sous-dossiers) - Exclusions git

## Prochaines étapes

1. ✅ Configurer les secrets GitHub (DOCKER_USERNAME, DOCKER_PASSWORD)
2. ✅ Committer et pousser les changements sur `test-integration`
3. ✅ Vérifier l'exécution du workflow dans GitHub Actions
4. ✅ Déployer les images Docker sur le serveur de production (si applicable)
