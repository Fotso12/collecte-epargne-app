# Configuration Mobile pour Docker Compose

## Pourquoi le mobile n'est pas dans Docker Compose ?

Flutter est un framework de développement mobile qui nécessite:
- Un émulateur Android/iOS ou un appareil physique
- Le SDK Flutter et les outils de build spécifiques
- Une interaction avec le système d'exploitation hôte

Ce n'est pas adapté à une conteneurisation complète en production.

## Approche recommandée pour le développement

### 1. Développement local avec ngrok

**Configuration** :
```bash
# 1. Démarrer Docker Compose (backend + web)
docker-compose up -d

# 2. Lancer ngrok
ngrok start --all

# 3. Mettre à jour les URLs dans le code Flutter
# frontend-mobile/savely/lib/services/auth_api.dart
static const String baseUrl = 'https://darryl-backend.ngrok.io';

# 4. Lancer l'app mobile
flutter run
```

### 2. Build & Déploiement en APK/AAB

```bash
# Build APK (Android)
cd frontend-mobile/savely
flutter build apk --release

# Build AAB (Google Play)
flutter build appbundle --release

# Build iOS
flutter build ipa --release
```

### 3. Distribution

- **APK** : Déployer sur des appareils Android en développement
- **AAB** : Soumettre à Google Play Store
- **IPA** : Soumettre à Apple App Store

## Architecture complète

```
┌─────────────────────────────────────────────────┐
│         Docker Compose (Conteneurs)              │
├─────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │  MySQL   │  │ Backend  │  │  Frontend    │   │
│  │ (3306)   │  │ (8082)   │  │  Web (80)    │   │
│  └──────────┘  └──────────┘  └──────────────┘   │
└─────────────────────────────────────────────────┘
              ↑
              │ ngrok tunnels
              ↓
┌─────────────────────────────────────────────────┐
│    Frontend Mobile (Local ou Device)             │
│    - flutter run / emulator                      │
│    - Connexion via ngrok URLs                   │
│    - Auth, transactions, etc.                   │
└─────────────────────────────────────────────────┘
```

## Fichiers de configuration

- `.github/workflows/ci-cd.yml` : CI/CD backend, web, mobile
- `docker-compose.yml` : Services backend, web, MySQL
- `ngrok.yml` : Configuration tunnels ngrok
- `NGROK_SETUP.md` : Guide détaillé ngrok

## Étapes suivantes

1. Configurer ngrok avec le token fourni
2. Mettre à jour les URLs dans le code mobile
3. Tester localement avec émulateur/device
4. Build APK/AAB pour distribution
