# 🔍 AUDIT BACKEND - Analyse des Entités vs Plan Mobile

## ⚠️ DÉCOUVERTES IMPORTANTES

Après avoir examiné les entités, DTOs et controllers du backend, j'ai identifié plusieurs **divergences critiques** entre le plan mobile initial et la réalité backend.

---

## 📋 CLIENT - Champs réels vs Plan initial

### ✅ Champs RÉELS dans ClientDto & Client Entity :

```
IDENTIFIANTS:
- numeroClient (Long) - ID auto-généré par DB (1,2,3...)
- codeClient (String, unique) - CLT2025... format

INFORMATIONS PERSONNELLES:
- fullName / nom + prenom (String) - Nouveau champ dans ClientDto
- telephone (String) - max 40 chars
- email (String) - max 100 chars, unique (pour login)
- adresse (String) - max 255 chars
- ville (String) - max 100 chars

DOCUMENTS IDENTITÉ (KYC):
- typeCni (TypeCNI enum) - CNI, Passport, etc.
- numCni (String) - max 50 chars
- dateNaissance (LocalDate)
- lieuNaissance (String) - max 100 chars
- profession (String) - max 100 chars
- photoPath (String) - path de photo
- cniRectoPath (String) - path CNI recto
- cniVersoPath (String) - path CNI verso

RELATIONS:
- utilisateur (OneToOne FK) - Lié à Utilisateur pour login
- codeCollecteurAssigne (String) - Matricule du collecteur assigné (optionnel, "0000" si aucun)
- nomCollecteur (String) - Nom du collecteur assigné

MÉTADONNÉES:
- statut (StatutGenerique) - ACTIF/INACTIF
- scoreEpargne (Integer) - Score KYC
- dateCreation (Instant)
- idAgence (Integer) - Agence responsable
```

### ❌ Champs MANQUANTS du plan initial :

1. **TypeCNI enum** - type de pièce d'identité (CNI, Passport, etc.)
2. **dateNaissance & lieuNaissance** - obligatoires pour KYC
3. **profession** - champ obligatoire
4. **Chemins photos** - photoPath, cniRectoPath, cniVersoPath (uploads)
5. **statut** - pas simplement "actif/inactif", c'est un StatutGenerique
6. **scoreEpargne** - score de score crédibilité
7. **dateCreation** - timestamp de création
8. **idAgence** - agence responsable

### 📝 ClientRegistrationRequest (pour inscription) :

```
REQUIS:
- fullName (String, max 150)
- phone (String, max 40)
- email (String, max 100) 
- password (String, 6-255)
- dateNaissance (LocalDate)
- lieuNaissance (String, max 100)
- profession (String, max 100)

OPTIONNEL:
- identityType (String)
- identityNumber (String)
- address (String)
- ville (String)
- collectorMatricule (String) - Matricule collecteur (défaut "0000")
- institutionId (Long) - Défaut = 1
```

---

## 📦 COMPTE - Champs réels

```
IDENTIFIANTS:
- idCompte (String) - Unique
- numCompte (String) - Numéro compte

SOLDES & MONTANTS:
- solde (BigDecimal) - Solde total
- soldeDisponible (BigDecimal) - Solde disponible
- tauxPenalite (BigDecimal)
- tauxBonus (BigDecimal)

DATES:
- dateOuverture (LocalDate)
- dateDerniereTransaction (Instant)
- dateCloture (LocalDate)

STATUTS:
- statut (StatutCompte enum) - OUVERT, BLOQUE, CLOTURE, etc.
- motifBlocage (String)
- statusApprobation (StatusApprobation) - EN_ATTENTE, APPROUVE, REJETE
- motifRejetApprobation (String)

WORKFLOW APPROBATION:
- dateApprobation (Instant)
- superviseurApprobation (FK Employe)

RELATIONS:
- codeClient (FK Client)
- idTypeCompte (FK TypeCompte)

ENUMS:
- StatutCompte: OUVERT, BLOQUE, CLOTURE, GELÉ, SUSPENDU, etc.
- StatusApprobation: EN_ATTENTE, APPROUVE, REJETE
```

---

## 💳 TRANSACTION - Champs réels

```
IDENTIFIANTS:
- idTransaction (String) - Unique
- reference (String) - Référence lisible

MONTANTS & BALANCES:
- montant (BigDecimal)
- soldeAvant (BigDecimal)
- soldeApres (BigDecimal)

WORKFLOW VALIDATION:
- statusValidation (StatusValidation) - EN_ATTENTE, VALIDEE, REJETEE
- statut (StatutTransaction) - EN_ATTENTE, VALIDEE_CAISSE, VALIDEE_SUPERVISEUR, TERMINEE, ANNULEE, REJETEE
- modeTransaction (ModeTransaction)
- typeTransaction (TypeTransaction)

DATES:
- dateTransaction (Instant)
- dateValidationCaisse (Instant)
- dateValidationSuperviseur (Instant)

ACTEURS:
- idEmployeInitiateur (FK Employe) - Collecteur qui crée
- idCaissierValidateur (FK Employe) - Caissier qui valide
- idSuperviseurValidateur (FK Employe) - Superviseur qui valide
- idCompte (FK Compte)

REJET:
- motifRejet (String)

SÉCURITÉ:
- signatureClient (String)
- hashTransaction (String)

AUTRES:
- description (String)
- recu (OneToOne Recu)
```

### Enums Transaction :

```
TypeTransaction: DEPOT, RETRAIT, COTISATION, INTERET, etc.
StatutTransaction: EN_ATTENTE, VALIDEE_CAISSE, VALIDEE_SUPERVISEUR, TERMINEE, ANNULEE, REJETEE
StatusValidation: EN_ATTENTE, VALIDEE, REJETEE
ModeTransaction: LIQUIDE, CHEQUE, VIREMENT, etc.
```

---

## 👤 COLLECTEUR (Employe avec TypeEmploye = COLLECTEUR)

```
IDENTIFIANTS:
- idEmploye (Integer) - ID auto-généré
- matricule (String) - max 50 chars, unique (ex: "0000" par défaut)

INFOS PERSO:
- utilisateur (OneToOne FK) - Login via Utilisateur
- typeEmploye (TypeEmploye enum) - COLLECTEUR, CAISSIER, SUPERVISEUR, ADMIN
- agenceZone (FK AgenceZone)

COMMISSION:
- commissionTaux (BigDecimal) - Commission en %

DATES:
- dateEmbauche (LocalDate)

HIÉRARCHIE:
- superviseur (FK Employe) - Son superviseur
- equipeSupervisee (OneToMany Employe) - Son équipe

PORTEFEUILLE:
- clientsAssignes (OneToMany Client) - Clients assignés

RELATIONS TRANSACTIONS:
- Transactions où il est initiateur (idEmployeInitiateur)
- Transactions où il est caissier (idCaissierValidateur)
```

### CollecteurKPIDTO (pour dashboard collecteur):

```
- idCollecteur (Integer)
- nomCollecteur (String)
- montantCollecte (BigDecimal) - Total des transactions
- nombreClients (long)
- nombreTransactions (long)
- gainsMoyens (BigDecimal) - Moyenne de gains
```

---

## 📊 TypeEmploye ENUM

```
COLLECTEUR - Agent qui collecte l'épargne auprès des clients
CAISSIER - Agent qui valide/traite les transactions
SUPERVISEUR - Superviseur qui approuve les comptes
ADMIN - Administrateur système
```

---

## ✅ CORRECTION DU PLAN MOBILE

### Models Dart à créer :

#### 1. ClientModel.dart
```dart
class ClientModel {
  // Identifiants
  int? numeroClient;
  String? codeClient;
  
  // Infos perso
  String? nom;
  String? prenom;
  String? email;
  String? telephone;
  String? adresse;
  String? ville;
  
  // KYC Documents
  String? typeCni; // CNI, Passport
  String? numCni;
  DateTime? dateNaissance;
  String? lieuNaissance;
  String? profession;
  String? photoPath;
  String? cniRectoPath;
  String? cniVersoPath;
  
  // Relations & Métadonnées
  String? statut; // ACTIF/INACTIF
  String? codeCollecteurAssigne;
  String? nomCollecteur;
  int? idAgence;
  int? scoreEpargne;
  DateTime? dateCreation;
}
```

#### 2. CompteModel.dart
```dart
class CompteModel {
  // Identifiants
  String? idCompte;
  String? numCompte;
  
  // Soldes
  double? solde;
  double? soldeDisponible;
  double? tauxPenalite;
  double? tauxBonus;
  
  // Dates
  DateTime? dateOuverture;
  DateTime? dateDerniereTransaction;
  DateTime? dateCloture;
  
  // Statuts
  String? statut; // OUVERT, BLOQUE, CLOTURE, etc.
  String? motifBlocage;
  String? statusApprobation; // EN_ATTENTE, APPROUVE, REJETE
  String? motifRejetApprobation;
  DateTime? dateApprobation;
  
  // Relations
  String? codeClient;
  int? idTypeCompte;
}
```

#### 3. TransactionModel.dart
```dart
class TransactionModel {
  // Identifiants
  String? idTransaction;
  String? reference;
  
  // Montants
  double? montant;
  double? soldeAvant;
  double? soldeApres;
  
  // Workflow
  String? statusValidation; // EN_ATTENTE, VALIDEE, REJETEE
  String? statut; // EN_ATTENTE, VALIDEE_CAISSE, VALIDEE_SUPERVISEUR, TERMINEE, ANNULEE, REJETEE
  String? modeTransaction;
  String? typeTransaction;
  
  // Dates
  DateTime? dateTransaction;
  DateTime? dateValidationCaisse;
  DateTime? dateValidationSuperviseur;
  
  // Acteurs
  String? idEmployeInitiateur;
  String? idCaissierValidateur;
  String? idSuperviseurValidateur;
  String? idCompte;
  
  // Rejet
  String? motifRejet;
  
  // Sécurité
  String? signatureClient;
  String? hashTransaction;
  String? description;
}
```

#### 4. CollecteurModel.dart
```dart
class CollecteurModel {
  // Identifiants
  int? idEmploye;
  String? matricule;
  
  // Infos perso (via relation Utilisateur)
  String? nom;
  String? prenom;
  String? email;
  String? typeEmploye; // COLLECTEUR
  
  // Commission
  double? commissionTaux;
  
  // Dates
  DateTime? dateEmbauche;
  
  // Hiérarchie
  int? idSuperviseur;
  String? nomSuperviseur;
  int? idAgence;
  
  // KPIs (de CollecteurKPIDTO)
  double? montantCollecte;
  int? nombreClients;
  int? nombreTransactions;
  double? gainsMoyens;
}
```

---

## 🎯 ERREURS DÉTECTÉES DANS LE PLAN INITIAL

| Problème | Plan Initial | Réalité Backend | Correction Requise |
|----------|--------------|-----------------|-------------------|
| NuméroClient | Optionnel | Auto-généré (PK) | Ajouter numeroClient (Long) |
| Nom/Prénom | Combiné fullName | Séparé nom + prenom | Garder séparé dans modèle |
| Documents KYC | Manquants | Obligatoires (typeCni, numCni, dateNaissance, lieuNaissance, profession) | Ajouter tous les champs d'identité |
| Chemins photos | Manquants | Oui (photoPath, cniRectoPath, cniVersoPath) | Ajouter uploads |
| TypeCNI | Pas d'enum | TypeCNI enum (CNI, Passport, etc.) | Implémenter enum |
| StatutCompte | Manquant | StatutCompte enum | Ajouter enum |
| StatusApprobation | Manquant | StatusApprobation enum (EN_ATTENTE, APPROUVE, REJETE) | Ajouter enum |
| Transactions Statut | Simplifié | Complex (EN_ATTENTE, VALIDEE_CAISSE, VALIDEE_SUPERVISEUR, TERMINEE, ANNULEE, REJETEE) | Compléter les états |
| StatusValidation | Manquant | StatusValidation enum (EN_ATTENTE, VALIDEE, REJETEE) | Ajouter enum |
| Collecteur Matricule | défaut "0000" ✅ | Correct, matricule unique | OK |
| KPIs Collecteur | Simplifié | CollecteurKPIDTO avec 6 champs | Compléter avec gainsMoyens, scoreEpargne |
| Acteurs Transaction | 1 collecteur | 3 acteurs (initiateur, caissier, superviseur) | Ajouter tous les 3 |

---

## 📝 PROCHAINES ÉTAPES

1. ✅ Créer les 4 modèles Dart correctement mappés
2. ✅ Implémenter JSON serialization (json_annotation + build_runner)
3. ✅ Créer les enums côté mobile (TypeCNI, StatutCompte, StatusApprobation, StatusValidation, TypeTransaction, StatutTransaction, ModeTransaction)
4. ✅ Mettre à jour API services avec les bons DTOs
5. ✅ Créer screens avec les bons champs
6. ✅ Implémenter upload documents (photoPath, cniRectoPath, cniVersoPath)
