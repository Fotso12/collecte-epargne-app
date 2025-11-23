# collecte-epargne-app
Application de collecte d'épargne - Backend Spring Boot, Frontend Angular, Mobile Flutter

.
├── backend/                 # API Spring Boot
├── frontend-web/           # Interface d'administration Angular
├── frontend-mobile/        # Application collecteur Flutter
└── docs/          # Documentation technique et fonctionnelle

🚀 Technologies

Backend: Spring Boot (Java)
Frontend Web: Angular
Frontend Mobile: Flutter
Base de données: MySQL
Conteneurisation: Docker
CI/CD: GitHub Actions / GitLab CI
Monitoring: Prometheus + Grafana
Logs: ELK Stack

✨ Fonctionnalités Principales
1. Gestion des Clients

Création, modification et suppression des clients
Importation en masse (CSV/Excel)
Capture photo client
Gestion des pièces d'identité

2. Gestion des Comptes d'Épargne

Comptes multiples par client
Solde en temps réel
Historique complet des transactions
Règles de pénalité/bonus configurables

3. Module de Collecte Mobile

Liste des clients assignés au collecteur
Dépôt d'épargne (mode offline-first)
Signature électronique du client
Synchronisation sécurisée des transactions
Historique des collectes

4. Transactions Financières

Opérations: Dépôt, Retrait, Reversement
Validation par superviseur
Génération de reçus (PDF/SMS)

5. Reporting & Analytics

Rapports journaliers/hebdomadaires
Évolution de l'épargne
Export PDF/Excel
Tableaux de bord avec graphiques
Classement des collecteurs

6. Notifications

SMS, Email, Push notifications
Alertes de retard de cotisation
Confirmations de transactions

7. Sécurité

Authentification JWT/OAuth2
Gestion des rôles (Admin, Collecteur, Superviseur, Caissier)
Chiffrement des données sensibles
Audit logs complet

🔧 Prérequis
Backend

Java 17+
Maven 3.8+
MySQL 8+

Frontend Web

Node.js 18+ minimum
Angular CLI 17+

Frontend Mobile

Flutter SDK 3.16+
Android Studio / Xcode

DevOps

Docker 
Docker Compose 
Git

🤝 Contribution
Workflow Git

Créer une branche depuis develop
Nommer la branche: feature/nom-fonctionnalite ou fix/nom-bug
Commit avec convention: type(scope): message
Push et créer une Pull Request vers develop
Code review requis avant merge

Conventions de Commit
feat: nouvelle fonctionnalité
fix: correction de bug
docs: documentation
style: formatage
refactor: refactorisation
test: ajout de tests
chore: tâches de maintenance
📝 Documentation
La documentation complète sera disponible dans le dossier /documentation:

Architecture technique
Diagrammes UML
API documentation
Guide d'utilisation
Manuel DevOps