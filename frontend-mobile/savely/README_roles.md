# Rôles et inscription (aligné sur `collector.sql`)

## Référentiel rôles côté base `savings_collector`
- Table `roles` (codes) :  
  - `admin` (Administrateur)  
  - `supervisor` (Superviseur)  
  - `collector` (Agent collecteur)  
  - `auditor` (Auditeur)  
- Table `users` référence `role_id`.
- Table `clients` est séparée : un client n’a pas d’entrée dans `users` par défaut, sauf si vous ajoutez un rôle `client`.

## Comportement de l’app mobile (écran inscription)
- Seuls deux types de compte sont proposés :  
  - **Compte client** → cherche un rôle dont le `code` est `client` (ou dont le nom contient "client").  
  - **Compte collecteur** → rôle avec `code` `collector` (ou nom contenant "collecteur/collector").  
- Aucun rôle `admin`, `supervisor` ou `auditor` n’est exposé dans ce formulaire public.  
- Le bouton Connexion reste non implémenté côté backend.

## Préparer les rôles pour l’app
1) Ajouter un rôle `client` si vous voulez que l’inscription crée des clients côté `users` :
```sql
INSERT INTO roles (code, label) VALUES ('client', 'Client');
```
2) Vérifier que le rôle `collector` existe déjà (fourni dans `collector.sql`).

## Générer l’admin par défaut
Insérer un admin (mot de passe en clair à remplacer par un hash dans votre service) :
```sql
INSERT INTO users (institution_id, role_id, full_name, email, phone, password_hash, status)
VALUES (1, 1, 'Admin Principal', 'admin@savings.local', '+2250100000000', '$2a$10$CHANGE_ME_HASH', 'active');
```
- `role_id` = 1 correspond au code `admin` dans le dump `collector.sql`.  
- Remplacez `institution_id` par la valeur existante dans votre table `institutions`.  
- Utilisez un hash BCrypt dans la colonne `password_hash` (exemple ci-dessus à recalculer).

## Flux de création côté app
- L’app appelle `GET /api/roles` puis filtre les rôles `client` et `collector`.  
- À l’inscription, elle envoie `POST /api/utilisateurs` avec `idRole` correspondant au type sélectionné.  
- La création de compte **caissier/superviseur/admin** doit rester réservée au dashboard admin (non exposée ici).

## À ajuster côté backend
- Exposer un rôle `client` (ou décider que les clients ne sont pas des entrées `users`).  
- Hacher les mots de passe (le service actuel stocke en clair).  
- Ajouter un endpoint de login si besoin.  
- Optionnel : tracer `createdBy` pour savoir quel admin a créé un utilisateur.

