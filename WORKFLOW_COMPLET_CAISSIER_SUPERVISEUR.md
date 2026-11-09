# 🎯 WORKFLOW COMPLET - IMPLÉMENTATION CAISSIER & SUPERVISEUR
**ADAPTÉ À LA STRUCTURE EXISTANTE - À VALIDER AVANT IMPLÉMENTATION**

---

## 📋 PHASE 1: BACKEND - STRUCTURE & SERVICES (5 étapes)

### 1.1 ENUMS (✅ DÉJÀ EXISTANTS)
- `StatusValidation.java` : EN_ATTENTE, VALIDEE, REJETEE (workflow caissier) ✅
- `StatusApprobation.java` : EN_ATTENTE, APPROUVE, REJETE (workflow superviseur) ✅
- `TypeEmploye.java` : COLLECTEUR, CAISSIER, SUPERVISEUR, AUDITOR ✅

### 1.2 MODIFICATIONS ENTITÉS (MINIMALES)
- `Transaction.java` : +`statusValidation` (enum) + getter/setter ✅ (À VÉRIFIER)
- `Compte.java` : +`statusApprobation`, `dateApprobation`, `superviseurApprobation` (relation) ✅ (À VÉRIFIER)
- `Employe.java` : Aucune modification (typeEmploye existe déjà) ✅

### 1.3 DTOs EXISTANTS À VÉRIFIER / À AJOUTER
**Existants:**
- `TransactionDto` ✅
- `CompteDto` ✅
- `ClientDto` ✅
- `EmployeDto` ✅

**À AJOUTER/COMPLÉTER:**
- `CaissierDashboardDTO.java` : KPIs caissier (transactions/jour, clients, collecteurs, total validé)
- `SuperviseurDashboardDTO.java` : KPIs superviseur (approbations en attente, meilleur collecteur, taux collection, clients)
- `CollectionKPIDTO.java` : Historique collection par période (date, montant, count, nom collecteur)
- `ReportingFinancierDTO.java` : Reporting (frais ouverture, taux, gains par rôle)

### 1.4 SERVICES EXISTANTS À UTILISER / COMPLÉTER

#### Services Existants (À RÉUTILISER) ✅
- `TransactionService.java` : create, getAll, update, delete
- `CompteService.java` : compte management
- `ClientService.java` : client view/management
- `EmployeService.java` : employé management
- `DashboardService.java` : déjà existant
- `ReportingService.java` : déjà existant
- `TransactionOfflineService.java` : sync offline ✅
- `DemandeOuvertureCompteService.java` : approbation compte existante

#### Services À CRÉER (dans `/services/implementations/` - AVEC LES AUTRES)

**A. `CaissierService.java`** (À côté de ClientService, TransactionService, etc.)

**A. `CaissierService.java` (NOUVEAU)**
**Responsabilités:**
- Récupérer transactions EN_ATTENTE pour validation (filtre agenceZone)
- Valider transaction → statusValidation = VALIDEE, crédite Compte.solde
- Rejeter transaction → statusValidation = REJETEE, enregistre motif
- Dashboard caissier (KPIs agence)
- Voir clients agence (read-only)
- Voir collecteurs agence (read-only)
- Générer reçu (PDF + QR code) - utiliser FormatRecu enum existant
- **Logique métier:** Chaque transaction validée → Caissier gagne 2% du montant

**B. `SuperviseurService.java` (NOUVEAU)**
**Responsabilités:**
- Récupérer comptes EN_ATTENTE d'approbation (filtre agenceZone)
- Approuver compte → statusApprobation = APPROUVE, client peut transactionner
- Rejeter compte → statusApprobation = REJETE, enregistre motif
- Dashboard superviseur (KPIs agence)
- Historique collection (Daily/Weekly/Monthly/Semi-Annual)
- Meilleur collecteur ranking
- Voir clients agence (read-only)
- Voir collecteurs agence (read-only)
- **Logique métier:** Supervision = +1% du montant total collecté

**C. `GainsService.java` (NOUVEAU)**
**Responsabilités:**
- Calculer gains collecteur: montant_collecté × 5%
- Calculer gains caissier: montant_validé × 2%
- Calculer gains superviseur: montant_supervisé × 1%
- Calculer gains institution: montant_collecté × 10%
- Frais ouverture compte: appliqué au 1er dépôt
- Taux épargne: % annuel appliqué au solde
- Tracker gains par employe/période (table Gains ou historique)

**D. `ReceiptService.java` (NOUVEAU - optionnel)**
**Responsabilités:**
- Générer PDF reçu (transaction validée)
- Générer QR code (référence transaction)
- Stocker reçu (fichier ou DB)

### 1.5 QUERY METHODS - REPOSITORIES À COMPLÉTER

#### `TransactionRepository.java` (À compléter)
```java
// Transactions EN_ATTENTE pour caissier validation
List<Transaction> findByStatusValidationAndCaissierValidateur_AgenceZone(
    StatusValidation status, 
    AgenceZone agenceZone
);

// Transactions par période + agence
List<Transaction> findByInitiateur_AgenceZoneAndDateTransactionBetween(
    AgenceZone agenceZone, 
    Instant startDate, 
    Instant endDate
);

// Somme montants par période + agence
BigDecimal sumMontantByAgenceAndPeriod(AgenceZone agenceZone, Instant start, Instant end);
```

#### `CompteRepository.java` (À compléter)
```java
// Comptes EN_ATTENTE d'approbation dans une agence
List<Compte> findPendingApprovalsByAgence(
    StatusApprobation status, 
    AgenceZone agenceZone
);

// Comptes par agence
List<Compte> findByAgenceZone(AgenceZone agenceZone);
```

#### `EmployeRepository.java` (À compléter)
```java
// Caissiers d'une agence
List<Employe> findCaissiersByAgenceZone(AgenceZone agenceZone, TypeEmploye type);

// Collecteurs d'une agence
List<Employe> findCollecteursByAgenceZone(AgenceZone agenceZone, TypeEmploye type);

// Superviseurs d'une agence
List<Employe> findSuperviseursByAgenceZone(AgenceZone agenceZone, TypeEmploye type);
```

#### `ClientRepository.java` (À compléter)
```java
// Clients assignés aux collecteurs d'une agence
List<Client> findByCollecteurAssigne_AgenceZone(AgenceZone agenceZone);
```

---

## 🎮 PHASE 2: CONTROLLERS (3 fichiers NOUVEAUX)

### 2.1 `CaissierController.java` (NOUVEAU)
**Endpoints:**
```
GET  /api/caissier/dashboard → CaissierDashboardDTO
GET  /api/caissier/transactions/pending → List<TransactionDTO>
GET  /api/caissier/transactions/{id} → TransactionDTO
POST /api/caissier/transactions/{id}/validate → {confirmé: true}
POST /api/caissier/transactions/{id}/reject → {motif: "..."}
GET  /api/caissier/clients → List<ClientDTO> (view only)
GET  /api/caissier/clients/{id} → ClientDTO
GET  /api/caissier/collecteurs → List<EmployeDTO> (view only)
GET  /api/caissier/collecteurs/{id} → EmployeDTO
POST /api/caissier/receipts/{transactionId}/generate → PDF
GET  /api/caissier/reporting/financial → ReportingFinancierDTO
```
**Security:** `@PreAuthorize("hasRole('CAISSIER')")`

### 2.2 `SuperviseurController.java` (NOUVEAU)
**Endpoints:**
```
GET  /api/superviseur/dashboard → SuperviseurDashboardDTO
GET  /api/superviseur/comptes/pending → List<CompteDTO>
POST /api/superviseur/comptes/{id}/approve → {confirmé: true}
POST /api/superviseur/comptes/{id}/reject → {motif: "..."}
GET  /api/superviseur/kpi/collection-history?period=DAILY → CollectionKPIDTO[]
GET  /api/superviseur/kpi/collection-history?period=WEEKLY
GET  /api/superviseur/kpi/collection-history?period=MONTHLY
GET  /api/superviseur/kpi/collection-history?period=SEMI_ANNUAL
GET  /api/superviseur/clients → List<ClientDTO> (view only)
GET  /api/superviseur/collecteurs → List<EmployeDTO> (view only)
GET  /api/superviseur/reporting/financial → ReportingFinancierDTO
```
**Security:** `@PreAuthorize("hasRole('SUPERVISEUR')")`

### 2.3 `SuperAdminController.java` (NOUVEAU - pour test@example.com)
**Endpoints:**
```
GET  /api/superadmin/dashboard → stats globales
GET  /api/superadmin/companies → toutes AgenceZone
GET  /api/superadmin/companies/{id}/details → détails agence
GET  /api/superadmin/reporting/global → stats globales
```
**Security:** `@PreAuthorize("hasRole('ADMIN')")`

---

## 📱 PHASE 3: FRONTEND WEB - CAISSIER (3 pages principales)

### 3.1 Page: Dashboard Caissier
**Affiche:**
- KPIs: Transactions du jour, clients, collecteurs, montant total validé
- Graphiques: Collecte par collecteur (top 5)
- Dernières transactions (table paginated)

### 3.2 Page: Validation Transactions
**Features:**
- Liste transactions EN_ATTENTE (avec filter, search, pagination)
- Clique transaction → modal détails
- Modal détails: Montant, client, collecteur, compte, frais estimés
- Buttons: ✅ Valider | ❌ Rejeter
- Si ✅ Valider: Confirmation simple + mise à jour immédiate
- Si ❌ Rejeter: Modal motif + validation
- Status update live après action

### 3.3 Page: Clients & Collecteurs (View Only)
**Clients section:**
- Liste clients assignés collecteurs agence
- Clique → modal détails (CNI, comptes, solde, score)

**Collecteurs section:**
- Liste collecteurs agence
- Clique → modal détails (KPIs jour, clients assignés, total collecté)

---

## 📱 PHASE 4: FRONTEND WEB - SUPERVISEUR (4 pages principales)

### 4.1 Page: Dashboard Superviseur
**Affiche:**
- KPIs: Comptes en attente, clients, collecteurs, taux collection
- Meilleur collecteur (ranking)
- Graphique: Collection hebdo/mensuel
- Sélecteur période: Daily/Weekly/Monthly/Semi-Annual

### 4.2 Page: Approbations Comptes
**Features:**
- Liste comptes EN_ATTENTE (paginated, searchable)
- Clique → modal détails compte
- Modal: Client, type compte, frais, taux
- Buttons: ✅ Approuver | ❌ Rejeter
- Si ✅ Approuver: Confirmation + compte devient ACTIF
- Si ❌ Rejeter: Modal motif
- Après action: Décrémente count, met à jour liste

### 4.3 Page: Collecteurs & Clients (View Only)
**Collecteurs:**
- Ranking par montant collecté (top 10)
- Clique → détails KPIs

**Clients:**
- Liste tous les clients assignés agence
- Clique → détails complets

### 4.4 Page: Reporting Financier
**Features:**
- Sélecteur période (date range picker)
- Export buttons: Excel, CSV
- Tableau data:
  - Montant collecté
  - Frais ouverture
  - Commissions par rôle (collecteur 5%, caissier 2%, superviseur 1%)
  - Gains institution (10%)
  - Taux épargne appliqué
- Graphiques: Pie chart distribution gains

---

## 📱 PHASE 5: FRONTEND MOBILE - COLLECTEUR (DÉJÀ EXISTANT) ✅

### 5.1 Adaptation Existant (Vérifier)
**Fonctionnalités déjà présentes:**
- Dashboard KPIs ✅
- Créer transaction ✅
- Voir ses clients ✅
- Synchronisation offline ✅

**À AJOUTER/ADAPTER:**
- Afficher statut transaction: EN_ATTENTE / VALIDEE / REJETEE / MOTIF (si rejet)
- Dans créer transaction: Permettre de sélectionner caissier de son agence
- Afficher gains du jour (option)

---

## 📱 PHASE 6: FRONTEND MOBILE - CLIENT (DÉJÀ EXISTANT) ✅

### 6.1 Adaptation Existant (Vérifier)
**Fonctionnalités déjà présentes:**
- Inscription client ✅
- Dashboard (solde) ✅
- Historique transactions ✅
- Voir comptes ✅

**À AJOUTER/ADAPTER:**
- Afficher statut compte: EN_ATTENTE / APPROUVE / REJETE (si approbation en cours)
- Afficher motif rejet si compte rejeté
- (Optionnel) Afficher reçu PDF pour transactions validées

---

## 🔐 PHASE 7: SÉCURITÉ & AUTHENTIFICATION

### 7.1 Roles & Permissions (Spring Security)
```
ROLE_ADMIN            → SuperAdmin (test@example.com) - Voir tout
ROLE_SUPERVISEUR      → Voir + modif seulement son agence
ROLE_CAISSIER         → Voir + modif seulement son agence
ROLE_COLLECTEUR       → Créer transactions, voir ses clients
ROLE_CLIENT           → Voir ses comptes, historique
```

### 7.2 Filtrage par AgenceZone (CRITIQUE)
```
@FilterByAgence
public CaissierDashboardDTO getDashboard() {
    // Récupère agenceZone de l'utilisateur depuis SecurityContext
    // Filtre TOUS les résultats par cette agenceZone
    // Exception: ROLE_ADMIN voit tout
}
```

### 7.3 Mapping Institution → AgenceZone
**PARTOUT dans le code:**
```
Institution institution → AgenceZone agenceZone
```
**Exceptions:**
- Certaines tables legacy peuvent avoir "institution_id" → mapping dans repository

---

## 💰 PHASE 8: LOGIQUE MÉTIER - GAINS & FRAIS

### 8.1 Configuration Frais & Taux
**À définir dans application.properties ou ConfigService:**
```
app.frais.ouverture-compte=5000              # CFA
app.taux.epargne=2                           # % annuel
app.commission.collecteur=5                  # % (FIXE)
app.commission.caissier=2                    # % (FIXE)
app.commission.superviseur=1                 # % (FIXE)
app.gains.institution=10                     # % (FIXE)
```

### 8.2 Calcul Gains (Exemple: Transaction 10,000 CFA)
```
Collecteur gagne: 10,000 × 5% = 500 CFA
Caissier gagne:   10,000 × 2% = 200 CFA
Superviseur gagne: 10,000 × 1% = 100 CFA
Institution gagne: 10,000 × 10% = 1,000 CFA
Client reçoit: 10,000 (crédité compte)
```

### 8.3 Table Gains (Nouvelle Table)
```sql
CREATE TABLE gains (
  id_gains INT AUTO_INCREMENT PRIMARY KEY,
  id_employe INT NOT NULL,
  montant_gains DECIMAL(15,2),
  type_gain ENUM('COLLECTEUR', 'CAISSIER', 'SUPERVISEUR', 'INSTITUTION'),
  date_transaction TIMESTAMP,
  id_transaction VARCHAR(50),
  id_agence INT,
  FOREIGN KEY (id_employe) REFERENCES employe(id_employe),
  FOREIGN KEY (id_agence) REFERENCES agence_zone(id_agence)
);

CREATE INDEX idx_gains_employe_date ON gains(id_employe, date_transaction);
CREATE INDEX idx_gains_agence_date ON gains(id_agence, date_transaction);
```

---

## ✅ PHASE 9: WORKFLOWS DÉTAILLÉS

### 9.1 Workflow: Validation Transaction Caissier
```
ÉTAPE 1: Collecteur crée transaction (mobile)
  → statusValidation = EN_ATTENTE
  → Envoie au serveur

ÉTAPE 2: Caissier voit transaction dans "Transactions Pending"
  → Page liste transactions EN_ATTENTE
  → Clique sur transaction

ÉTAPE 3: Modal détails (Montant, Client, Collecteur, Frais)
  
ÉTAPE 4A: OPTION VALIDER
  → Clique ✅ Valider
  → Modal confirmation: "Valider cette transaction de 10,000?"
  → Clique "Confirmer"
  → Backend:
    - Transaction.statusValidation = VALIDEE
    - Compte.solde += 10,000
    - Caissier gagne 2% (table Gains)
    - Email notification collecteur
    - Frontend: Affiche "✅ Validée" + retire de liste
  
ÉTAPE 4B: OPTION REJETER
  → Clique ❌ Rejeter
  → Modal motif: Input "motif de rejet"
  → Clique "Confirmer"
  → Backend:
    - Transaction.statusValidation = REJETEE
    - Enregistre motif
    - Email notification collecteur + motif
    - Frontend: Retire de liste
    
ÉTAPE 5: Collecteur voir statut (mobile)
  → Voit "REJETEE" + motif
  → Peut créer nouvelle transaction
```

### 9.2 Workflow: Approbation Compte Superviseur
```
ÉTAPE 1: Collecteur crée compte pour client
  → Compte.statusApprobation = EN_ATTENTE
  → Via DemandeOuvertureCompte

ÉTAPE 2: Superviseur voit compte en attente
  → Page "Approbations Comptes"
  → Clique sur compte

ÉTAPE 3: Modal détails (Client, Type compte, Frais, Taux)

ÉTAPE 4A: OPTION APPROUVER
  → Clique ✅ Approuver
  → Modal confirmation: "Approuver ce compte?"
  → Clique "Confirmer"
  → Backend:
    - Compte.statusApprobation = APPROUVE
    - Compte.dateApprobation = now()
    - Client peut maintenant faire transactions
    - Email notification collecteur
    - Frontend: Retire de liste
  
ÉTAPE 4B: OPTION REJETER
  → Clique ❌ Rejeter
  → Modal motif: Input "motif de rejet"
  → Clique "Confirmer"
  → Backend:
    - Compte.statusApprobation = REJETE
    - Enregistre motif
    - Email notification collecteur + motif
    - Frontend: Retire de liste
    
ÉTAPE 5: Client voir statut (mobile)
  → Si APPROUVE: Peut créer transactions
  → Si REJETE: Affiche motif + peut créer nouveau compte
```

---

## 📊 PHASE 10: REPORTING & EXPORTS

### 10.1 ReportingFinancierDTO (À créer/compléter)
```java
public class ReportingFinancierDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal montantCollecte;
    private BigDecimal fraisOuverture;
    private BigDecimal commissionCollecteur;
    private BigDecimal commissionCaissier;
    private BigDecimal commissionSuperviseur;
    private BigDecimal gainsInstitution;
    private BigDecimal tauxEpargneApplique;
    // ...
}
```

### 10.2 Exports Support
- **PDF:** Reçus transactions (via ReceiptService)
- **Excel:** Reporting financier, KPIs
- **CSV:** Export données brutes

---

## 🗄️ PHASE 11: MODIFICATIONS BD (Migrations Flyway)

### 11.1 Colonnes Transaction (À VÉRIFIER si déjà présentes)
```sql
ALTER TABLE transaction ADD COLUMN IF NOT EXISTS status_validation VARCHAR(50);
CREATE INDEX IF NOT EXISTS idx_transaction_status_validation ON transaction(status_validation);
```

### 11.2 Colonnes Compte (À VÉRIFIER si déjà présentes)
```sql
ALTER TABLE compte ADD COLUMN IF NOT EXISTS status_approbation VARCHAR(50);
ALTER TABLE compte ADD COLUMN IF NOT EXISTS date_approbation TIMESTAMP;
ALTER TABLE compte ADD COLUMN IF NOT EXISTS id_superviseur_approbation INT;
ALTER TABLE compte ADD FOREIGN KEY (id_superviseur_approbation) REFERENCES employe(id_employe);
CREATE INDEX IF NOT EXISTS idx_compte_status_approbation ON compte(status_approbation);
```

### 11.3 Table Gains (NOUVELLE)
```sql
CREATE TABLE IF NOT EXISTS gains (
  id_gains INT AUTO_INCREMENT PRIMARY KEY,
  id_employe INT NOT NULL,
  montant_gains DECIMAL(15,2),
  type_gain VARCHAR(50),
  date_transaction TIMESTAMP,
  id_transaction VARCHAR(50),
  id_agence INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_employe) REFERENCES employe(id_employe),
  FOREIGN KEY (id_agence) REFERENCES agence_zone(id_agence)
);
CREATE INDEX idx_gains_employe_date ON gains(id_employe, date_transaction);
CREATE INDEX idx_gains_agence_date ON gains(id_agence, date_transaction);
```

---

## ✅ PHASE 12: TESTING & VALIDATION

### 12.1 Unit Tests À Créer
- CaissierService: validate, reject, dashboard
- SuperviseurService: approve, reject, KPIs
- GainsService: calculations

### 12.2 Integration Tests
- Workflow: Collecteur → Caissier → Compte crédité
- Workflow: Collecteur → Superviseur → Compte approuvé
- Gains calculation after validation/approval

---

## 📝 RÉSUMÉ PRIORISATION

### 🔴 CRITIQUE (Must have - Sprint 1)
1. ✅ Enums StatusValidation, StatusApprobation (DÉJÀ EXISTENT)
2. ✅ Transaction.statusValidation field (À VÉRIFIER)
3. ✅ Compte.statusApprobation, dateApprobation fields (À VÉRIFIER)
4. CaissierService → valider/rejeter transactions
5. SuperviseurService → approuver/rejeter comptes
6. CaissierController + SuperviseurController
7. Dashboard caissier & superviseur (web)
8. Page validation transactions
9. Page approbation comptes
10. Security filtering by AgenceZone

### 🟠 IMPORTANT (Should have - Sprint 2)
11. GainsService + Table Gains
12. Reporting financier
13. Receipts generation (PDF + QR)
14. Collection history KPIs
15. Collecteur mobile: afficher statut transaction
16. Client mobile: afficher statut compte

### 🟡 NICE TO HAVE (Nice to have - Sprint 3+)
17. Advanced analytics
18. Audit trail détaillée
19. SMS notifications
20. Advanced mobile UI

---

## 📋 CHECKLIST PRÉ-IMPLÉMENTATION

### À VÉRIFIER AVANT DE COMMENCER
- ✅ Enums StatusValidation, StatusApprobation existent?
- ⚠️ Transaction.java a statusValidation field?
- ⚠️ Compte.java a statusApprobation fields?
- ✅ DemandeOuvertureCompteService existe? (approbation compte)
- ✅ TransactionOfflineService existe? (sync offline)
- ✅ ClientService, EmployeService existent?
- ✅ DashboardService, ReportingService existent?
- ⚠️ Mappers (TransactionMapper, CompteMapper, etc.) existent?
- ✅ TypeEmploye enum existe?
- ✅ AgenceZone entity existe?

### À AJOUTER
- 🔵 DTOs: CaissierDashboardDTO, SuperviseurDashboardDTO, CollectionKPIDTO
- 🔵 Services: CaissierService, SuperviseurService, GainsService (+ ReceiptService optionnel)
- 🔵 Controllers: CaissierController, SuperviseurController, SuperAdminController
- 🔵 Repository query methods (voir Phase 1.5)
- 🔵 Table Gains (migration BD)
- 🔵 Frontend pages (web + mobile adaptations)

---

## 🚀 ÉTAPES IMPLÉMENTATION PROPOSÉES

### SEMAINE 1: Backend Foundation
1. Vérifier/créer DTOs (CaissierDashboardDTO, etc.)
2. Créer CaissierService
3. Créer SuperviseurService
4. Créer GainsService
5. Créer Controllers (Caissier, Superviseur, SuperAdmin)
6. Ajouter query methods aux repositories
7. Compilation & tests basiques

### SEMAINE 2: Frontend Web - Caissier & Superviseur
1. Dashboard caissier
2. Transaction validation page
3. Dashboard superviseur
4. Account approval page
5. Reporting financier
6. Clients/Collecteurs view pages

### SEMAINE 3: Mobile Adaptations + Testing
1. Collecteur mobile: afficher statut transaction + sélectionner caissier
2. Client mobile: afficher statut compte + motif rejet
3. Receipts generation
4. Testing + bug fixes
5. Deployment

---

**✅ PRÊT À COMMENCER?**
**Valides-tu cet approche adapté à la structure existante?**
**Questions/modifications avant implémentation?**
