# Analyse de la base de données savings_collector.sql

## 📋 Structure générale

### Tables principales

1. **`institutions`** - Les organisations gérant l'épargne
   - `id`, `name`, `code`, `contact_email`, `contact_phone`, `timezone`
   - Exemple : Institution par Défaut (id=1)

2. **`users`** - Utilisateurs du système (Staff uniquement)
   - `id`, `institution_id`, `role_id`, `full_name`, `email`, `phone`
   - `password_hash`, `status` (active/pending/blocked)
   - `last_login_at`, `created_at`, `updated_at`
   - **Utilisateur admin par défaut** : admin@savings.local

3. **`roles`** - Rôles système
   - 1: admin (Administrateur)
   - 2: supervisor (Superviseur)
   - 3: collector (Agent collecteur)
   - 4: auditor (Auditeur)
   - ⚠️ **PAS de rôle "client"** - les clients ne sont pas dans users

4. **`clients`** - Clients épargnants (séparés des users)
   - `id`, `institution_id`, `collector_id` (assigné à un collecteur)
   - `full_name`, `phone`, `identity_type`, `identity_number`
   - `address`, `avatar_url`, `status`, `created_at`

5. **`collectors`** - Extension des users pour collecteurs
   - `id` (référence users.id)
   - `badge_code`, `zone`, `device_id`
   - `availability` (available/on_route/inactive)

6. **`accounts`** - Comptes d'épargne
   - `id`, `client_id`, `rule_id`, `label`
   - `balance`, `currency` (XOF par défaut)
   - `status` (open/frozen/closed)

7. **`transactions`** - Transactions d'épargne
   - `id`, `account_id`, `collector_id`
   - `type` (deposit/withdrawal/collector_transfer)
   - `amount`, `txn_reference`, `status`, `metadata`
   - `operation_at`, `created_at`

8. **`reversements`** - Reversements des collecteurs
   - `id`, `collector_id`, `supervisor_id`
   - `amount`, `proof_url`, `status`
   - `submitted_at`, `validated_at`

9. **`savings_rules`** - Règles d'épargne
   - `id`, `institution_id`, `label`, `type` (bonus/penalty)
   - `rate`, `condition_json`, `is_active`

10. **`sessions`** - Gestion des sessions
    - `id`, `user_id`, `refresh_token`, `device_info`
    - `ip_address`, `expires_at`, `created_at`

11. **`notifications`** - Système de notifications
    - `id`, `institution_id`, `recipient_type`, `recipient_id`
    - `channel` (sms/email/push), `template_code`, `payload`
    - `status`, `sent_at`

## 🔄 Comparaison avec le backend Spring actuel

### Différences majeures

| Aspect | savings_collector.sql | Backend Spring actuel |
|--------|----------------------|----------------------|
| **Users** | Uniquement staff (admin, supervisor, collector, auditor) | Staff + clients mélangés |
| **Clients** | Table séparée `clients` | Intégrés dans `utilisateur` via OneToOne |
| **Nom utilisateur** | `full_name` (un seul champ) | `nom` + `prenom` (séparés) |
| **Institution** | Obligatoire (`institution_id`) | Absente |
| **Password** | `password_hash` (bcrypt) | `password` en clair ⚠️ |
| **Status** | `active`/`pending`/`blocked` | `ACTIF`/`INACTIF`/`SUSPENDU` |
| **Collectors** | Table d'extension avec `badge_code`, `zone` | Intégrés dans `employe` |
| **Rôles** | 4 rôles (pas de client) | 5 rôles (avec client) |

## ✅ Points positifs de savings_collector.sql

1. **Séparation claire** : Staff (`users`) vs Clients (`clients`)
2. **Multi-tenant** : Support des institutions multiples
3. **Sécurité** : Mots de passe hachés (bcrypt)
4. **Sessions** : Gestion des tokens de refresh
5. **Traçabilité** : `last_login_at`, `updated_at`
6. **Notifications** : Système intégré
7. **Règles d'épargne** : Bonus/pénalités configurables
8. **Reversements** : Gestion des remises de collecte

## 🎯 Recommandations

### Option 1 : Migrer vers savings_collector.sql (recommandé)

**Avantages** :
- Structure plus propre et évolutive
- Multi-tenant prêt
- Sécurité renforcée
- Fonctionnalités avancées (notifications, règles, sessions)

**Étapes** :
1. Créer la BD `savings_collector` avec le script SQL
2. Adapter le backend Spring aux entités savings_collector
3. Modifier le frontend pour s'aligner

### Option 2 : Garder le backend Spring actuel

**Actions nécessaires** :
1. Implémenter bcrypt pour les passwords
2. Ajouter support des institutions
3. Créer table `clients` séparée
4. Ajouter système de sessions
5. Implémenter notifications

## 📝 Migration suggérée

### Backend Spring

1. **Nouvelles entités** :
   - `Institution`
   - `Client` (séparé de User)
   - `Collector` (extension de User/Employe)
   - `Account`
   - `Transaction`
   - `Reversement`
   - `SavingsRule`
   - `Session`
   - `Notification`

2. **Adapter User/Utilisateur** :
   - Ajouter `institution_id`
   - Remplacer `nom + prenom` par `full_name` OU garder les deux
   - Remplacer `password` par `password_hash`
   - Adapter `status` (String au lieu d'enum)
   - Ajouter `last_login_at`, `updated_at`

3. **Services à créer** :
   - `SessionService` pour gestion des tokens
   - `NotificationService` pour SMS/Email/Push
   - `SavingsRuleService` pour bonus/pénalités
   - `ReversementService` pour gestion des remises

### Frontend Flutter

1. **Adapter les modèles** :
   - `UserModel` avec `fullName` (ou garder nom/prenom)
   - Ajouter `institutionId`
   - Nouveau modèle `ClientModel`

2. **Dashboards à enrichir** :
   - Dashboard Client : afficher `accounts` et `transactions`
   - Dashboard Collecteur : gérer `reversements` et voir clients assignés
   - Dashboard Admin : gérer institutions, règles, notifications

## 🚀 Prochaines étapes suggérées

1. **Décider** : Migrer vers savings_collector.sql OU adapter l'existant
2. **Sécurité** : Implémenter bcrypt pour les passwords
3. **Sessions** : Mettre en place JWT ou refresh tokens
4. **Institutions** : Créer l'institution par défaut
5. **Tests** : Créer utilisateurs de test pour chaque rôle

## 🔐 Utilisateur admin par défaut

```sql
Email: admin@savings.local
Password: (haché avec bcrypt $2a$12$...)
Role: admin (id=1)
Institution: Institution par Défaut (id=1)
Status: active
```

Pour se connecter, il faudra soit :
- Connaître le mot de passe original (non visible dans le hash)
- Créer un nouveau compte admin via un script
- Utiliser un endpoint de "reset password"

