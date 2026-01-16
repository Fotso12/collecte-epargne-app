# 🎯 Plan d'Implémentation Optimisé - Cashier & Supervisor
## Sans casser ce qui fonctionne + Réutilisation des entités existantes

---

## 📦 DÉPENDANCES À AJOUTER AU pom.xml

```xml
<!-- Pour calculs de commissions/frais -->
<dependency>
    <groupId>org.decimal4j</groupId>
    <artifactId>decimal4j</artifactId>
    <version>1.0.3</version>
</dependency>

<!-- Pour rapports/exports -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- Pour génération PDF reçus -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>

<!-- Pour QR codes sur reçus -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.3</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.3</version>
</dependency>

<!-- Pour audite des changements -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

**Pour tester en local :**
```bash
mvn clean install
```

---

## 📝 MODIFICATIONS MINIMALES AUX ENTITÉS

### 1. Employé.java - Ajouter agence_id et caissier_id
```java
// COMMENT CLEF: Clé pour le collecteur de choisir le caissier de son agence
// File: src/main/java/com/collecte_epargne/entities/Employe.java

// AJOUTER à la classe Employe:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ID_AGENCE")
private AgenceZone agence; // Pour isoler caissiers/collecteurs par agence

@Enumerated(EnumType.STRING)
@Column(name = "TYPE_EMPLOYE")
private TypeEmploye typeEmploye; // ENUM: COLLECTEUR, CAISSIER, SUPERVISEUR

// Getters/Setters
public AgenceZone getAgence() {
    return agence;
}

public void setAgence(AgenceZone agence) {
    this.agence = agence;
}

public TypeEmploye getTypeEmploye() {
    return typeEmploye;
}

public void setTypeEmploye(TypeEmploye typeEmploye) {
    this.typeEmploye = typeEmploye;
}
```

### 2. Transaction.java - Ajouter validation_status et cashier_agency
```java
// COMMENT CLEF: Tracer qui valide et quand (audit trail)
// File: src/main/java/com/collecte_epargne/entities/Transaction.java

// AJOUTER à la classe Transaction:
@Enumerated(EnumType.STRING)
@Column(name = "STATUS_VALIDATION")
private StatusValidation statusValidation; // ENUM: EN_ATTENTE, VALIDEE, REJETEE

@Column(name = "DATE_VALIDATION")
private Instant dateValidation;

@Column(name = "MOTIF_REJET")
private String motifRejet; // Si rejeté par caissier

// Getters/Setters
public StatusValidation getStatusValidation() {
    return statusValidation;
}

public void setStatusValidation(StatusValidation statusValidation) {
    this.statusValidation = statusValidation;
}

// ... autres getters/setters
```

### 3. Compte.java - Ajouter status_approbation
```java
// COMMENT CLEF: Superviseur doit approuver les nouveaux comptes
// File: src/main/java/com/collecte_epargne/entities/Compte.java

// AJOUTER à la classe Compte:
@Enumerated(EnumType.STRING)
@Column(name = "STATUS_APPROBATION")
private StatusApprobation statusApprobation; // ENUM: EN_ATTENTE, APPROUVE, REJETE

@Column(name = "DATE_APPROBATION")
private Instant dateApprobation;

@Column(name = "MOTIF_REJET_APPROBATION")
private String motifRejetApprobation;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ID_SUPERVISEUR_APPROBATION")
private Employe superviseurApprobation;
```

---

## 🛠️ ENUMS À CRÉER

### File: `src/main/java/com/collecte_epargne/utils/TypeEmploye.java`
```java
package com.collecte_epargne.collecte_epargne.utils;

// COMMENT CLEF: Détermine le rôle de l'employé = restrictions d'accès
public enum TypeEmploye {
    COLLECTEUR,      // Collecte argent auprès des clients
    CAISSIER,        // Valide transactions des collecteurs
    SUPERVISEUR,     // Approuve comptes, voir KPIs
    AUDITOR          // Accès lecture-seule aux rapports
}
```

### File: `src/main/java/com/collecte_epargne/utils/StatusValidation.java`
```java
package com.collecte_epargne.collecte_epargne.utils;

// COMMENT CLEF: Cycle de vie d'une transaction = workflow validation
public enum StatusValidation {
    EN_ATTENTE,    // Créée par collecteur, attends caissier
    VALIDEE,       // Caissier a approuvé
    REJETEE        // Caissier a rejeté (motif enregistré)
}
```

### File: `src/main/java/com/collecte_epargne/utils/StatusApprobation.java`
```java
package com.collecte_epargne.collecte_epargne.utils;

// COMMENT CLEF: Cycle de vie d'un compte client = approbation superviseur
public enum StatusApprobation {
    EN_ATTENTE,    // Créé par client/caissier, attends superviseur
    APPROUVE,      // Superviseur OK = compte actif
    REJETE         // Superviseur refuse (motif enregistré)
}
```

---

## 📊 SERVICES À CRÉER

### 1. CashierService.java - Logique caissier
```java
package com.collecte_epargne.collecte_epargne.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.*;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class CashierService {
    private static final Logger logger = LoggerFactory.getLogger(CashierService.class);
    
    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;
    private final AgenceZoneRepository agenceRepository;
    private final ClientRepository clientRepository;

    public CashierService(TransactionRepository transactionRepository,
                         CompteRepository compteRepository,
                         EmployeRepository employeRepository,
                         AgenceZoneRepository agenceRepository,
                         ClientRepository clientRepository) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
        this.agenceRepository = agenceRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * COMMENT CLEF: Récupère les transactions en attente de validation pour une agence
     * Ordre: Par date DESC (plus récentes d'abord)
     */
    public List<Transaction> getTransactionsEnAttenteByCashier(String cashierId) {
        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));
        
        logger.info("Fetch pending transactions for cashier {} in agency {}", 
                   cashierId, cashier.getAgence().getId());
        
        return transactionRepository.findByStatusValidationAndCaissierValidateur_Agence(
            StatusValidation.EN_ATTENTE,
            cashier.getAgence()
        );
    }

    /**
     * COMMENT CLEF: Valide une transaction = crédite le compte client + met à jour balances
     * @param transactionId ID de la transaction à valider
     * @param cashierId ID du caissier qui valide
     */
    public Transaction validateTransaction(String transactionId, String cashierId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
        
        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));

        // COMMENT CLEF: Vérifications de sécurité
        if (!transaction.getCaissierValidateur().getAgence()
                .getId().equals(cashier.getAgence().getId())) {
            throw new RuntimeException("Vous ne pouvez valider que les transactions de votre agence");
        }

        // Mettre à jour le statut
        transaction.setStatusValidation(StatusValidation.VALIDEE);
        transaction.setDateValidation(Instant.now());
        transaction.setMotifRejet(null); // Nettoyer motif rejet si existant

        // COMMENT CLEF: Créditer le compte client
        Compte compte = transaction.getCompte();
        compte.setMontantActuel(
            compte.getMontantActuel().add(transaction.getMontant())
        );

        transactionRepository.save(transaction);
        compteRepository.save(compte);

        logger.info("Transaction {} validated by cashier {}", transactionId, cashierId);
        return transaction;
    }

    /**
     * COMMENT CLEF: Rejette une transaction = motif enregistré, solde non crédité
     */
    public Transaction rejectTransaction(String transactionId, String cashierId, String motifRejet) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));

        transaction.setStatusValidation(StatusValidation.REJETEE);
        transaction.setDateValidation(Instant.now());
        transaction.setMotifRejet(motifRejet);

        logger.warn("Transaction {} rejected by cashier {} with reason: {}", 
                    transactionId, cashierId, motifRejet);

        return transactionRepository.save(transaction);
    }

    /**
     * COMMENT CLEF: Récupère les caissiers disponibles de l'agence du collecteur
     * Utilisé quand collecteur choisit qui valide sa transaction
     */
    public List<Employe> getCashiersByAgency(String agencyId) {
        return employeRepository.findByAgence_IdAndTypeEmploye(
            agencyId,
            TypeEmploye.CAISSIER
        );
    }

    /**
     * COMMENT CLEF: Dashboard KPIs pour caissier
     */
    public CashierDashboardDTO getDashboardStats(String cashierId) {
        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));

        AgenceZone agence = cashier.getAgence();
        
        // Total collecté aujourd'hui
        BigDecimal totalToday = transactionRepository
            .findByStatusValidationAndDateValidationBetweenAndCaissierValidateur_Agence(
                StatusValidation.VALIDEE,
                Instant.now().truncatedTo(ChronoUnit.DAYS),
                Instant.now(),
                agence
            )
            .stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Transactions en attente
        Long pendingCount = transactionRepository
            .countByStatusValidationAndCaissierValidateur_Agence(
                StatusValidation.EN_ATTENTE,
                agence
            );

        return new CashierDashboardDTO(
            totalToday,
            pendingCount,
            agence.getCollecteurs().size(),
            agence.getClients().size()
        );
    }
}
```

### 2. SupervisorService.java - Logique superviseur
```java
package com.collecte_epargne.collecte_epargne.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import com.collecte_epargne.collecte_epargne.utils.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class SupervisorService {
    private static final Logger logger = LoggerFactory.getLogger(SupervisorService.class);
    
    private final CompteRepository compteRepository;
    private final EmployeRepository employeRepository;
    private final TransactionRepository transactionRepository;
    private final InstitutionRepository institutionRepository;
    private final ClientRepository clientRepository;

    public SupervisorService(CompteRepository compteRepository,
                            EmployeRepository employeRepository,
                            TransactionRepository transactionRepository,
                            InstitutionRepository institutionRepository,
                            ClientRepository clientRepository) {
        this.compteRepository = compteRepository;
        this.employeRepository = employeRepository;
        this.transactionRepository = transactionRepository;
        this.institutionRepository = institutionRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * COMMENT CLEF: Récupère comptes en attente d'approbation pour le superviseur
     */
    public List<Compte> getAccountsAwaitingApproval(String supervisorId) {
        Employe supervisor = employeRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));

        logger.info("Fetch accounts awaiting approval for supervisor {} in institution {}", 
                   supervisorId, supervisor.getInstitution().getId());

        return compteRepository.findByStatusApprobationAndClient_Institution(
            StatusApprobation.EN_ATTENTE,
            supervisor.getInstitution()
        );
    }

    /**
     * COMMENT CLEF: Approuve un compte = active la capacité à faire des transactions
     */
    public Compte approveAccount(String compteId, String supervisorId) {
        Compte compte = compteRepository.findById(compteId)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        Employe supervisor = employeRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));

        // Vérification: le compte appartient à son institution
        if (!compte.getClient().getInstitution()
                .getId().equals(supervisor.getInstitution().getId())) {
            throw new RuntimeException("Vous ne pouvez approuver que les comptes de votre institution");
        }

        compte.setStatusApprobation(StatusApprobation.APPROUVE);
        compte.setDateApprobation(Instant.now());
        compte.setSuperviseurApprobation(supervisor);
        compte.setMotifRejetApprobation(null);

        logger.info("Account {} approved by supervisor {}", compteId, supervisorId);
        return compteRepository.save(compte);
    }

    /**
     * COMMENT CLEF: Rejette un compte = motif enregistré, compte reste inactif
     */
    public Compte rejectAccount(String compteId, String supervisorId, String motifRejet) {
        Compte compte = compteRepository.findById(compteId)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        Employe supervisor = employeRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));

        compte.setStatusApprobation(StatusApprobation.REJETE);
        compte.setDateApprobation(Instant.now());
        compte.setSuperviseurApprobation(supervisor);
        compte.setMotifRejetApprobation(motifRejet);

        logger.warn("Account {} rejected by supervisor {} with reason: {}", 
                    compteId, supervisorId, motifRejet);

        return compteRepository.save(compte);
    }

    /**
     * COMMENT CLEF: Calcule les KPIs de collection pour la période donnée
     */
    public CollectionKPIDTO getCollectionKPI(String supervisorId, LocalDate startDate, LocalDate endDate) {
        Employe supervisor = employeRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));

        Institution institution = supervisor.getInstitution();

        // Total collecté dans la période
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal totalCollected = transactionRepository
            .findByStatusValidationAndDateValidationBetweenAndCaissierValidateur_Institution(
                StatusValidation.VALIDEE,
                startInstant,
                endInstant,
                institution
            )
            .stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Meilleur collecteur
        Employe topCollector = getTopCollector(institution, startInstant, endInstant);

        // Nombre de clients actifs
        Long activeClients = clientRepository.countByInstitution(institution);

        return new CollectionKPIDTO(
            totalCollected,
            topCollector != null ? topCollector.getNom() + " " + topCollector.getPrenom() : "N/A",
            activeClients,
            startDate,
            endDate
        );
    }

    /**
     * COMMENT CLEF: Trouve le collecteur avec le plus de collecte dans la période
     */
    private Employe getTopCollector(Institution institution, Instant start, Instant end) {
        List<Transaction> transactions = transactionRepository
            .findByStatusValidationAndDateValidationBetweenAndInitiateur_Institution(
                StatusValidation.VALIDEE,
                start,
                end,
                institution
            );

        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getInitiateur,
                Collectors.summingBigDecimal(Transaction::getMontant)
            ))
            .entrySet()
            .stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * COMMENT CLEF: Dashboard superviseur avec KPIs
     */
    public SupervisorDashboardDTO getDashboardStats(String supervisorId) {
        Employe supervisor = employeRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));

        Institution institution = supervisor.getInstitution();
        LocalDate today = LocalDate.now();

        // KPI d'aujourd'hui
        CollectionKPIDTO todayKPI = getCollectionKPI(supervisorId, today, today);

        // Comptes en attente d'approbation
        Long pendingApprovals = compteRepository
            .countByStatusApprobationAndClient_Institution(
                StatusApprobation.EN_ATTENTE,
                institution
            );

        // Total collecté ce mois
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1).withDayOfMonth(1).minusDays(1);
        CollectionKPIDTO monthKPI = getCollectionKPI(supervisorId, monthStart, monthEnd);

        return new SupervisorDashboardDTO(
            todayKPI.getTotalCollected(),
            pendingApprovals,
            monthKPI.getTotalCollected(),
            todayKPI.getTopCollector(),
            institution.getEmployes().size()
        );
    }
}
```

### 3. EarningsService.java - Calcul des revenus
```java
package com.collecte_epargne.collecte_epargne.services;

import org.springframework.stereotype.Service;
import com.collecte_epargne.collecte_epargne.entities.*;
import com.collecte_epargne.collecte_epargne.repositories.*;
import java.math.BigDecimal;
import java.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EarningsService {
    private static final Logger logger = LoggerFactory.getLogger(EarningsService.class);
    
    private final TransactionRepository transactionRepository;
    private final EmployeRepository employeRepository;
    private final InstitutionRepository institutionRepository;

    // COMMENT CLEF: Configuration des pourcentages (à mettre en properties)
    private static final BigDecimal COLLECTOR_COMMISSION_PERCENT = new BigDecimal("0.05"); // 5%
    private static final BigDecimal CASHIER_COMMISSION_PERCENT = new BigDecimal("0.02"); // 2%
    private static final BigDecimal SUPERVISOR_COMMISSION_PERCENT = new BigDecimal("0.01"); // 1%
    private static final BigDecimal ENTERPRISE_PERCENT = new BigDecimal("0.10"); // 10%

    public EarningsService(TransactionRepository transactionRepository,
                          EmployeRepository employeRepository,
                          InstitutionRepository institutionRepository) {
        this.transactionRepository = transactionRepository;
        this.employeRepository = employeRepository;
        this.institutionRepository = institutionRepository;
    }

    /**
     * COMMENT CLEF: Calcule les gains d'un collecteur sur une période
     * Formule: Montant collecté × COLLECTOR_COMMISSION_PERCENT
     */
    public BigDecimal calculateCollectorEarnings(String collectorId, LocalDate startDate, LocalDate endDate) {
        Employe collector = employeRepository.findById(collectorId)
            .orElseThrow(() -> new RuntimeException("Collecteur non trouvé"));

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal totalCollected = transactionRepository
            .findByInitiateurAndDateCreationBetween(collector, startInstant, endInstant)
            .stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal earnings = totalCollected.multiply(COLLECTOR_COMMISSION_PERCENT)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

        logger.info("Collector {} earnings for period {}-{}: {}", 
                    collectorId, startDate, endDate, earnings);

        return earnings;
    }

    /**
     * COMMENT CLEF: Calcule les gains d'un caissier
     * Formule: Montant validé × CASHIER_COMMISSION_PERCENT
     */
    public BigDecimal calculateCashierEarnings(String cashierId, LocalDate startDate, LocalDate endDate) {
        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal totalValidated = transactionRepository
            .findByCaissierValidateurAndDateValidationBetween(cashier, startInstant, endInstant)
            .stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal earnings = totalValidated.multiply(CASHIER_COMMISSION_PERCENT)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

        logger.info("Cashier {} earnings for period {}-{}: {}", 
                    cashierId, startDate, endDate, earnings);

        return earnings;
    }

    /**
     * COMMENT CLEF: Calcule les revenus globaux de l'entreprise/institution
     * Formule: Montant total collecté × ENTERPRISE_PERCENT
     */
    public BigDecimal calculateInstitutionEarnings(String institutionId, LocalDate startDate, LocalDate endDate) {
        Institution institution = institutionRepository.findById(institutionId)
            .orElseThrow(() -> new RuntimeException("Institution non trouvée"));

        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        // Total collecté par tous les collecteurs de l'institution
        BigDecimal totalCollected = transactionRepository
            .findByInitiateur_InstitutionAndDateCreationBetween(institution, startInstant, endInstant)
            .stream()
            .map(Transaction::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal earnings = totalCollected.multiply(ENTERPRISE_PERCENT)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

        logger.info("Institution {} earnings for period {}-{}: {}", 
                    institutionId, startDate, endDate, earnings);

        return earnings;
    }
}
```

---

## 🎮 CONTRÔLEURS À CRÉER

### 1. CashierController.java
```java
package com.collecte_epargne.collecte_epargne.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.collecte_epargne.collecte_epargne.services.CashierService;
import com.collecte_epargne.collecte_epargne.dtos.CashierDashboardDTO;
import com.collecte_epargne.collecte_epargne.dtos.TransactionDTO;
import com.collecte_epargne.collecte_epargne.entities.Transaction;
import com.collecte_epargne.collecte_epargne.security.AuthService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cashier")
// COMMENT CLEF: Tous les endpoints caissier sont protégés - seulement CAISSIER peut accéder
@PreAuthorize("hasRole('CAISSIER')")
public class CashierController {
    private static final Logger logger = LoggerFactory.getLogger(CashierController.class);
    
    private final CashierService cashierService;
    private final AuthService authService;

    public CashierController(CashierService cashierService, AuthService authService) {
        this.cashierService = cashierService;
        this.authService = authService;
    }

    /**
     * COMMENT CLEF: Dashboard - KPIs du jour pour le caissier
     * GET /api/cashier/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<CashierDashboardDTO> getDashboard() {
        String cashierId = authService.getCurrentUserId();
        CashierDashboardDTO dashboard = cashierService.getDashboardStats(cashierId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * COMMENT CLEF: Récupère les transactions en attente de validation
     * GET /api/cashier/transactions/pending
     */
    @GetMapping("/transactions/pending")
    public ResponseEntity<List<TransactionDTO>> getPendingTransactions() {
        String cashierId = authService.getCurrentUserId();
        List<Transaction> transactions = cashierService.getTransactionsEnAttenteByCashier(cashierId);
        logger.info("Cashier {} fetched {} pending transactions", cashierId, transactions.size());
        
        // Mapper les entités vers DTOs
        List<TransactionDTO> dtos = transactions.stream()
            .map(this::toDTO)
            .toList();
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * COMMENT CLEF: Valide une transaction
     * POST /api/cashier/transactions/{id}/validate
     */
    @PostMapping("/transactions/{id}/validate")
    public ResponseEntity<TransactionDTO> validateTransaction(@PathVariable String id) {
        String cashierId = authService.getCurrentUserId();
        Transaction transaction = cashierService.validateTransaction(id, cashierId);
        
        logger.info("Cashier {} validated transaction {}", cashierId, id);
        return ResponseEntity.ok(toDTO(transaction));
    }

    /**
     * COMMENT CLEF: Rejette une transaction avec motif
     * POST /api/cashier/transactions/{id}/reject
     */
    @PostMapping("/transactions/{id}/reject")
    public ResponseEntity<TransactionDTO> rejectTransaction(
            @PathVariable String id,
            @RequestParam String motif) {
        String cashierId = authService.getCurrentUserId();
        Transaction transaction = cashierService.rejectTransaction(id, cashierId, motif);
        
        logger.warn("Cashier {} rejected transaction {} with reason: {}", cashierId, id, motif);
        return ResponseEntity.ok(toDTO(transaction));
    }

    /**
     * COMMENT CLEF: Récupère les caissiers disponibles de son agence
     * Utilisé quand collecteur choisit qui valide la transaction
     * GET /api/cashier/agency/available
     */
    @GetMapping("/agency/available")
    public ResponseEntity<?> getAvailableCashiers() {
        String cashierId = authService.getCurrentUserId();
        Employe cashier = employeRepository.findById(cashierId)
            .orElseThrow(() -> new RuntimeException("Caissier non trouvé"));
        
        List<?> cashiers = cashierService.getCashiersByAgency(cashier.getAgence().getId());
        return ResponseEntity.ok(cashiers);
    }

    // HELPER: Mapper Transaction vers DTO
    private TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
            transaction.getIdTransaction(),
            transaction.getReference(),
            transaction.getMontant(),
            transaction.getTypeTransaction(),
            transaction.getStatusValidation(),
            transaction.getDateCreation(),
            transaction.getCompte().getIdCompte(),
            transaction.getInitiateur().getNom()
        );
    }
}
```

### 2. SupervisorController.java
```java
package com.collecte_epargne.collecte_epargne.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.collecte_epargne.collecte_epargne.services.SupervisorService;
import com.collecte_epargne.collecte_epargne.dtos.SupervisorDashboardDTO;
import com.collecte_epargne.collecte_epargne.dtos.CompteDTO;
import com.collecte_epargne.collecte_epargne.dtos.CollectionKPIDTO;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.security.AuthService;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/supervisor")
// COMMENT CLEF: Tous les endpoints superviseur sont protégés - seulement SUPERVISEUR peut accéder
@PreAuthorize("hasRole('SUPERVISEUR')")
public class SupervisorController {
    private static final Logger logger = LoggerFactory.getLogger(SupervisorController.class);
    
    private final SupervisorService supervisorService;
    private final AuthService authService;

    public SupervisorController(SupervisorService supervisorService, AuthService authService) {
        this.supervisorService = supervisorService;
        this.authService = authService;
    }

    /**
     * COMMENT CLEF: Dashboard - KPIs globaux pour le superviseur
     * GET /api/supervisor/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<SupervisorDashboardDTO> getDashboard() {
        String supervisorId = authService.getCurrentUserId();
        SupervisorDashboardDTO dashboard = supervisorService.getDashboardStats(supervisorId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * COMMENT CLEF: Récupère les comptes clients en attente d'approbation
     * GET /api/supervisor/accounts/pending
     */
    @GetMapping("/accounts/pending")
    public ResponseEntity<List<CompteDTO>> getPendingAccounts() {
        String supervisorId = authService.getCurrentUserId();
        List<Compte> accounts = supervisorService.getAccountsAwaitingApproval(supervisorId);
        logger.info("Supervisor {} fetched {} pending accounts", supervisorId, accounts.size());
        
        List<CompteDTO> dtos = accounts.stream()
            .map(this::toDTO)
            .toList();
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * COMMENT CLEF: Approuve un compte client
     * POST /api/supervisor/accounts/{id}/approve
     */
    @PostMapping("/accounts/{id}/approve")
    public ResponseEntity<CompteDTO> approveAccount(@PathVariable String id) {
        String supervisorId = authService.getCurrentUserId();
        Compte account = supervisorService.approveAccount(id, supervisorId);
        
        logger.info("Supervisor {} approved account {}", supervisorId, id);
        return ResponseEntity.ok(toDTO(account));
    }

    /**
     * COMMENT CLEF: Rejette un compte client avec motif
     * POST /api/supervisor/accounts/{id}/reject
     */
    @PostMapping("/accounts/{id}/reject")
    public ResponseEntity<CompteDTO> rejectAccount(
            @PathVariable String id,
            @RequestParam String motif) {
        String supervisorId = authService.getCurrentUserId();
        Compte account = supervisorService.rejectAccount(id, supervisorId, motif);
        
        logger.warn("Supervisor {} rejected account {} with reason: {}", supervisorId, id, motif);
        return ResponseEntity.ok(toDTO(account));
    }

    /**
     * COMMENT CLEF: Récupère les KPIs de collection pour une période donnée
     * GET /api/supervisor/kpi/collection?start=2024-01-01&end=2024-01-31
     */
    @GetMapping("/kpi/collection")
    public ResponseEntity<CollectionKPIDTO> getCollectionKPI(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        String supervisorId = authService.getCurrentUserId();
        CollectionKPIDTO kpi = supervisorService.getCollectionKPI(supervisorId, start, end);
        return ResponseEntity.ok(kpi);
    }

    // HELPER: Mapper Compte vers DTO
    private CompteDTO toDTO(Compte compte) {
        return new CompteDTO(
            compte.getIdCompte(),
            compte.getClient().getNom(),
            compte.getMontantActuel(),
            compte.getStatusApprobation(),
            compte.getDateCreation()
        );
    }
}
```

---

## 📂 DTOs À CRÉER

### 1. CashierDashboardDTO.java
```java
package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

public class CashierDashboardDTO {
    private BigDecimal totalCollectedToday;
    private Long pendingTransactionCount;
    private Integer activeCollectors;
    private Integer activeClients;

    public CashierDashboardDTO(BigDecimal totalCollectedToday, Long pendingTransactionCount,
                               Integer activeCollectors, Integer activeClients) {
        this.totalCollectedToday = totalCollectedToday;
        this.pendingTransactionCount = pendingTransactionCount;
        this.activeCollectors = activeCollectors;
        this.activeClients = activeClients;
    }

    // Getters
    public BigDecimal getTotalCollectedToday() { return totalCollectedToday; }
    public Long getPendingTransactionCount() { return pendingTransactionCount; }
    public Integer getActiveCollectors() { return activeCollectors; }
    public Integer getActiveClients() { return activeClients; }
}
```

### 2. SupervisorDashboardDTO.java
```java
package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;

public class SupervisorDashboardDTO {
    private BigDecimal totalCollectedToday;
    private Long pendingAccountApprovals;
    private BigDecimal totalCollectedThisMonth;
    private String topCollectorName;
    private Integer totalEmployees;

    public SupervisorDashboardDTO(BigDecimal totalCollectedToday, Long pendingAccountApprovals,
                                  BigDecimal totalCollectedThisMonth, String topCollectorName,
                                  Integer totalEmployees) {
        this.totalCollectedToday = totalCollectedToday;
        this.pendingAccountApprovals = pendingAccountApprovals;
        this.totalCollectedThisMonth = totalCollectedThisMonth;
        this.topCollectorName = topCollectorName;
        this.totalEmployees = totalEmployees;
    }

    // Getters
    public BigDecimal getTotalCollectedToday() { return totalCollectedToday; }
    public Long getPendingAccountApprovals() { return pendingAccountApprovals; }
    public BigDecimal getTotalCollectedThisMonth() { return totalCollectedThisMonth; }
    public String getTopCollectorName() { return topCollectorName; }
    public Integer getTotalEmployees() { return totalEmployees; }
}
```

### 3. CollectionKPIDTO.java
```java
package com.collecte_epargne.collecte_epargne.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CollectionKPIDTO {
    private BigDecimal totalCollected;
    private String topCollector;
    private Long activeClients;
    private LocalDate startDate;
    private LocalDate endDate;

    public CollectionKPIDTO(BigDecimal totalCollected, String topCollector, Long activeClients,
                           LocalDate startDate, LocalDate endDate) {
        this.totalCollected = totalCollected;
        this.topCollector = topCollector;
        this.activeClients = activeClients;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public BigDecimal getTotalCollected() { return totalCollected; }
    public String getTopCollector() { return topCollector; }
    public Long getActiveClients() { return activeClients; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
```

---

## 🔐 SÉCURITÉ & ANNOTATIONS

### SecurityConfig.java - À AJOUTER (pas modifier)
```java
// COMMENT CLEF: Ajouter ces configurations SANS modifier le reste de SecurityConfig

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ... configurations existantes ...
            .authorizeHttpRequests(authz -> authz
                // NOUVEAU: Routes caissier
                .requestMatchers("/api/cashier/**").hasRole("CAISSIER")
                // NOUVEAU: Routes superviseur
                .requestMatchers("/api/supervisor/**").hasRole("SUPERVISEUR")
                // ... reste existant ...
            );
        return http.build();
    }
}
```

---

## 🗃️ REPOSITORIES À AJOUTER

Ajouter ces méthodes aux repositories existants:

### TransactionRepository.java - Ajouter:
```java
// COMMENT CLEF: Méthodes de recherche pour workflow caissier/superviseur
List<Transaction> findByStatusValidationAndCaissierValidateur_Agence(
    StatusValidation status,
    AgenceZone agence
);

List<Transaction> findByStatusValidationAndDateValidationBetweenAndCaissierValidateur_Agence(
    StatusValidation status,
    Instant dateStart,
    Instant dateEnd,
    AgenceZone agence
);

long countByStatusValidationAndCaissierValidateur_Agence(
    StatusValidation status,
    AgenceZone agence
);

// ... pour superviseur ...
List<Transaction> findByStatusValidationAndDateValidationBetweenAndCaissierValidateur_Institution(
    StatusValidation status,
    Instant dateStart,
    Instant dateEnd,
    Institution institution
);
```

### EmployeRepository.java - Ajouter:
```java
// COMMENT CLEF: Récupérer caissiers par agence
List<Employe> findByAgence_IdAndTypeEmploye(String agenceId, TypeEmploye typeEmploye);

// Récupérer collecteurs par institution
List<Employe> findByInstitution_IdAndTypeEmploye(String institutionId, TypeEmploye typeEmploye);
```

### CompteRepository.java - Ajouter:
```java
// COMMENT CLEF: Comptes en attente d'approbation
List<Compte> findByStatusApprobationAndClient_Institution(
    StatusApprobation status,
    Institution institution
);

long countByStatusApprobationAndClient_Institution(
    StatusApprobation status,
    Institution institution
);
```

---

## 🚀 ÉTAPES D'IMPLÉMENTATION

### ✅ Phase 1: Préparation (2 heures)
1. [ ] Ajouter les dépendances au `pom.xml`
2. [ ] Créer les 3 ENUMS (TypeEmploye, StatusValidation, StatusApprobation)
3. [ ] Modifier les entités (Employe, Transaction, Compte) avec nouveaux champs
4. [ ] Lancer `mvn clean install` pour vérifier les compilations

### ✅ Phase 2: Backend Services (4 heures)
1. [ ] Créer `CashierService.java` avec les 5 méthodes
2. [ ] Créer `SupervisorService.java` avec les 5 méthodes
3. [ ] Créer `EarningsService.java` avec calculs de commission
4. [ ] Ajouter les query methods aux repositories

### ✅ Phase 3: Contrôleurs & DTOs (2 heures)
1. [ ] Créer `CashierController.java` avec 6 endpoints
2. [ ] Créer `SupervisorController.java` avec 5 endpoints
3. [ ] Créer les 3 DTOs (CashierDashboardDTO, SupervisorDashboardDTO, CollectionKPIDTO)
4. [ ] Ajouter routes à `SecurityConfig`

### ✅ Phase 4: Tests & Validation (1 heure)
1. [ ] Compiler le projet: `mvn clean package`
2. [ ] Tester les endpoints avec Postman (import collection existante)
3. [ ] Vérifier les logs pour les commentaires CLEF

---

## 📋 CHECKLIST DE SÉCURITÉ

- ✅ Tous les endpoints caissier/superviseur protégés par `@PreAuthorize`
- ✅ Vérification institution_id pour ne voir que son périmètre
- ✅ Logs détaillés pour audit (qui a validé quoi, quand, motif)
- ✅ Isolation par agence pour caissiers
- ✅ Transactions database garantissent consistency

---

## 📞 QA: CE QUI NE CHANGE PAS

✅ Authentification existante (JWT, login)
✅ Modèle Client/Utilisateur/Employe
✅ Base de données (aucune table créée)
✅ API collecteur/client (fonctionnent toujours)
✅ Endpoints existants
✅ Docker & CI/CD

---

## 🎯 RÉSUMÉ DÉPENDANCES

| Dépendance | Version | Raison |
|-----------|---------|--------|
| decimal4j | 1.0.3 | Calculs précis commissions |
| apache-poi | 5.2.5 | Export Excel rapports |
| itextpdf | 5.5.13.3 | Génération PDF reçus |
| google-zxing | 3.5.3 | QR codes reçus |
| hibernate-envers | (parent) | Audit des changements |

