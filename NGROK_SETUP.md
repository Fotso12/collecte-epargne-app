# Configuration ngrok - Collecte Épargne App

## Vue d'ensemble
ngrok permet d'exposer vos services locaux (backend, frontend web) à internet pour que le frontend mobile puisse les atteindre en développement.

## Configuration

### Token d'authentification
```
38Flnn9jDrFguLYVfJGCysq1Ui7_3LD1yyXFfiPZcGEYstThM
```

### Tunnels disponibles

#### Backend
- **Adresse locale** : `http://localhost:8082`
- **Domaine ngrok** : `https://darryl-backend.ngrok.io`
- **Utilisation** : API REST pour mobile et web

#### Frontend Web
- **Adresse locale** : `http://localhost:80`
- **Domaine ngrok** : `https://darryl-web.ngrok.io`
- **Utilisation** : Interface web accessible via mobile

## Installation

### Windows
```powershell
# Via Chocolatey
choco install ngrok

# Ou télécharger manuellement
# https://ngrok.com/download
```

### macOS / Linux
```bash
# Via Homebrew (macOS)
brew install ngrok

# Via apt (Linux)
sudo apt-get install ngrok

# Ou télécharger manuellement
# https://ngrok.com/download
```

## Utilisation

### 1. Configurer le token (première fois)
```bash
ngrok config add-authtoken 38Flnn9jDrFguLYVfJGCysq1Ui7_3LD1yyXFfiPZcGEYstThM
```

### 2. Démarrer les tunnels
```bash
# Démarrer tous les tunnels (fichier ngrok.yml)
ngrok start --all

# Ou démarrer un tunnel spécifique
ngrok start backend
ngrok start frontend-web
```

### 3. Vérifier les tunnels
Les URLs actives s'affichent dans la console ngrok. Exemple :
```
backend              http://localhost:8082  -> https://darryl-backend.ngrok.io
frontend-web         http://localhost:80    -> https://darryl-web.ngrok.io
```

## Utilisation dans le Frontend Mobile

### 1. Mettre à jour les URLs d'API

**Dans `frontend-mobile/savely/lib/services/auth_api.dart`** :
```dart
// Remplacer
static const String baseUrl = 'http://localhost:8082';

// Par
static const String baseUrl = 'https://darryl-backend.ngrok.io';
```

**Dans les autres services** (client_service.dart, employee_api.dart, etc.) :
```dart
// Même logique - utiliser les URLs ngrok au lieu de localhost
static const String apiUrl = 'https://darryl-backend.ngrok.io/api';
```

### 2. Ignorer les avertissements de certificat (si nécessaire)
```dart
HttpClient httpClient = HttpClient()
  ..badCertificateCallback = (X509Certificate cert, String host, int port) => true;
```

⚠️ **Ne pas utiliser en production !** C'est seulement pour le développement local.

## Troubleshooting

### ngrok : commande non trouvée
```bash
# Ajouter ngrok au PATH
# Windows: C:\Users\<Username>\AppData\Local\ngrok
# macOS: /usr/local/bin/ngrok
# Linux: /usr/local/bin/ngrok
```

### Erreur d'authentification
```bash
# Reconfigurer le token
ngrok config add-authtoken 38Flnn9jDrFguLYVfJGCysq1Ui7_3LD1yyXFfiPZcGEYstThM
```

### Les tunnels ne démarrent pas
```bash
# Vérifier que les services locaux sont actifs
# Backend doit tourner sur 8082
# Web doit tourner sur 80

# Démarrer Docker Compose
docker-compose up -d
```

### URL ngrok change à chaque démarrage
C'est normal. Les domaines personnalisés (darryl-backend.ngrok.io) restent les mêmes, 
mais vérifiez les URLs affichées dans la console ngrok.

## Performance et Limites

- **Gratuit** : ~1,500 requêtes/minute
- **Pro** : Limites plus élevées
- **Latence** : ~50-200ms ajoutée par ngrok

Pour plus d'info : https://ngrok.com/pricing

## Documentation supplémentaire

- https://ngrok.com/docs
- https://ngrok.com/docs/agent/config
