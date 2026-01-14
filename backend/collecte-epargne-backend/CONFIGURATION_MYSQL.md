# Configuration MySQL - savings_collector

## ✅ Configuration actuelle

Le backend est maintenant connecté à MySQL avec :
- **Base de données** : `savings_collector`
- **Utilisateur** : `root`
- **Mot de passe** : (vide)
- **Hôte** : `localhost:3306`

## 📋 Vérifications nécessaires

### 1. MySQL est démarré

Dans XAMPP ou votre gestionnaire MySQL :
- Démarrer le service MySQL
- Vérifier qu'il tourne sur le port 3306

### 2. Base de données existe

Ouvrir phpMyAdmin (http://localhost/phpmyadmin) et vérifier que la base `savings_collector` existe.

**Si elle n'existe pas**, créer la base :
```sql
CREATE DATABASE savings_collector CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Importer le schéma (optionnel)

Si tu veux utiliser le schéma complet de savings_collector.sql :
1. Ouvrir phpMyAdmin
2. Sélectionner la base `savings_collector`
3. Onglet "Importer"
4. Choisir le fichier `savings_collector.sql`
5. Cliquer "Exécuter"

⚠️ **OU** laisser Hibernate créer les tables automatiquement (`ddl-auto=update`)

## 🔧 Configuration Hibernate

Avec `spring.jpa.hibernate.ddl-auto=update` :
- Hibernate **crée automatiquement** les tables manquantes
- Met à jour le schéma existant
- Ne supprime PAS les données existantes

### Tables créées automatiquement

Le backend va créer/mettre à jour ces tables :
- `institutions` - Organisations
- `roles` - Rôles système
- `utilisateur` - Users staff (admin, collecteur, etc.)
- `clients` - Clients épargnants
- `employe` - Employés/Collecteurs

## 📊 Données initiales

Au premier démarrage, le backend crée automatiquement :

### Institution par défaut
```
id: 1
name: Institution par Défaut
code: DEF001
contact_email: contact@institution.com
```

### Rôles (5 rôles)
```
1. admin - Administrateur
2. supervisor - Superviseur
3. collector - Agent collecteur
4. auditor - Auditeur
5. client - Client (rôle fictif pour compatibilité)
```

## 🎯 Test de connexion

Une fois le backend démarré, vérifier dans phpMyAdmin :
1. Base `savings_collector` existe
2. Tables créées (institutions, roles, utilisateur, clients, employe)
3. Données initiales insérées

## ⚠️ Problèmes possibles

### Erreur : "Unknown database 'savings_collector'"
**Solution** : Créer la base manuellement dans phpMyAdmin

### Erreur : "Access denied for user 'root'@'localhost'"
**Solution** : Vérifier les identifiants MySQL dans application.properties

### Erreur : "Communications link failure"
**Solution** : Vérifier que MySQL est démarré (XAMPP)

### Erreur : "Table doesn't exist"
**Solution** : 
- Soit importer savings_collector.sql
- Soit laisser Hibernate créer les tables (redémarrer le backend)

## 🔒 Sécurité pour production

⚠️ **Important** : Cette configuration est pour le développement uniquement.

Pour la production :
- Créer un utilisateur MySQL dédié (pas root)
- Définir un mot de passe fort
- Activer SSL
- Changer `ddl-auto=validate` (pas update)
- Implémenter bcrypt pour les passwords utilisateurs

## 📝 Commandes utiles MySQL

### Vérifier la base
```sql
SHOW DATABASES;
USE savings_collector;
SHOW TABLES;
```

### Voir les données
```sql
SELECT * FROM institutions;
SELECT * FROM roles;
SELECT * FROM utilisateur;
SELECT * FROM clients;
```

### Réinitialiser (ATTENTION: Supprime tout!)
```sql
DROP DATABASE savings_collector;
CREATE DATABASE savings_collector CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

