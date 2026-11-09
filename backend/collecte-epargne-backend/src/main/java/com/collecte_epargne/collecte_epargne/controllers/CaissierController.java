package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CaissierDashboardDTO;
import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.dtos.TransactionDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CaissierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * COMMENT CLEF: Controller Caissier - Endpoints validation transactions + dashboard
 * Sécurité: Filtre par agenceZone du caissier authentifié
 */
@RestController
@RequestMapping("/api/caissier")
@PreAuthorize("hasRole('CAISSIER') or hasRole('CASHIER') or hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('SUPERVISEUR') or hasRole('CAIS') or hasRole('SUP')")
public class CaissierController {
    
    private static final Logger log = LoggerFactory.getLogger(CaissierController.class);
    
    private final CaissierService caissierService;
    private final com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository utilisateurRepository;
    private final com.collecte_epargne.collecte_epargne.repositories.EmployeRepository employeRepository;

    public CaissierController(CaissierService caissierService,
                              com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository utilisateurRepository,
                              com.collecte_epargne.collecte_epargne.repositories.EmployeRepository employeRepository) {
        this.caissierService = caissierService;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
    }

    private Integer getAuthenticatedCaissierId(java.security.Principal principal) {
        if (principal == null) throw new RuntimeException("Utilisateur non authentifié");
        String email = principal.getName();
        com.collecte_epargne.collecte_epargne.entities.Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));

        // Allow ADMIN to bypass employee check
        if (utilisateur.getRole().getCode().equalsIgnoreCase("admin")) {
            // Pour l'admin, on essaie de trouver un caissier par défaut (le premier trouvé)
            return employeRepository.findByTypeEmploye(com.collecte_epargne.collecte_epargne.utils.TypeEmploye.CAISSIER)
                    .stream().findFirst()
                    .map(com.collecte_epargne.collecte_epargne.entities.Employe::getIdEmploye)
                    .orElse(null); // Return null if no caissier found
        }
            
        com.collecte_epargne.collecte_epargne.entities.Employe employe = employeRepository.findByUtilisateurLogin(utilisateur.getLogin())
            .orElseThrow(() -> new RuntimeException("Cet utilisateur n'est pas lié à un employé (Caissier)"));
            
        return employe.getIdEmploye();
    }

    /**
     * COMMENT CLEF: Dashboard caissier - KPIs agence
     */
    @GetMapping("/dashboard")
    public ResponseEntity<CaissierDashboardDTO> getDashboard(java.security.Principal principal) {
        log.info("GET /api/caissier/dashboard - User: {}", principal.getName());
        try {
            Integer idCaissier = getAuthenticatedCaissierId(principal);
            
            // Si Admin et pas de caissier trouvé, retourner un dashboard vide ou 404 géré
            if (idCaissier == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            CaissierDashboardDTO dashboard = caissierService.obtenirDashboard(idCaissier);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Erreur dashboard caissier: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Récupère transactions EN_ATTENTE de validation
     */
    /**
     * COMMENT CLEF: Récupère transactions EN_ATTENTE de validation
     */
    @GetMapping("/transactions/pending")
    public ResponseEntity<List<TransactionDto>> getTransactionsPending(java.security.Principal principal) {
        try {
            Integer idCaissier = getAuthenticatedCaissierId(principal);
            log.info("GET /api/caissier/transactions/pending - Caissier: {}", idCaissier);
            List<TransactionDto> transactions = caissierService.obtenirTransactionsEnAttente(idCaissier);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            log.error("Erreur récupération transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Valide une transaction - Modal confirmation
     * Request body: { "confirmé": true }
     */
    @PostMapping("/transactions/{idTransaction}/validate")
    public ResponseEntity<TransactionDto> validerTransaction(
            @PathVariable String idTransaction,
            java.security.Principal principal,
            @RequestBody Map<String, Object> request) {
        
        try {
            Integer idCaissier = getAuthenticatedCaissierId(principal);
            log.info("POST /api/caissier/transactions/{}/validate - Caissier: {}", idTransaction, idCaissier);
        
            // COMMENT CLEF: Vérifier confirmation
            Boolean confirme = (Boolean) request.get("confirmé");
            if (confirme == null || !confirme) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
            }
            
            TransactionDto result = caissierService.validerTransaction(idTransaction, idCaissier);
            log.info("Transaction {} validée avec succès", idTransaction);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Erreur validation transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Rejette une transaction - Modal motif
     * Request body: { "motifRejet": "Montant incorrect", "confirmé": true }
     */
    @PostMapping("/transactions/{idTransaction}/reject")
    public ResponseEntity<TransactionDto> rejeterTransaction(
            @PathVariable String idTransaction,
            java.security.Principal principal,
            @RequestBody Map<String, Object> request) {
        
        try {
            Integer idCaissier = getAuthenticatedCaissierId(principal);
            log.info("POST /api/caissier/transactions/{}/reject - Caissier: {}", idTransaction, idCaissier);
        
            String motifRejet = (String) request.get("motifRejet");
            Boolean confirme = (Boolean) request.get("confirmé");
            
            if (motifRejet == null || motifRejet.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Motif de rejet requis");
                return ResponseEntity.badRequest().build();
            }
            
            if (confirme == null || !confirme) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            TransactionDto result = caissierService.rejeterTransaction(idTransaction, motifRejet, idCaissier);
            log.info("Transaction {} rejetée - Motif: {}", idTransaction, motifRejet);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Erreur rejet transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Voir clients assignés aux collecteurs de l'agence (view only)
     */
    @GetMapping("/clients")
    public ResponseEntity<List<ClientDto>> getClients(@RequestParam Integer idAgence) {
        log.info("GET /api/caissier/clients - Agence: {}", idAgence);
        try {
            List<ClientDto> clients = caissierService.obtenirClientsAgence(idAgence);
            return ResponseEntity.ok(clients);
        } catch (RuntimeException e) {
            log.error("Erreur récupération clients: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Voir détails d'un client
     */
    @GetMapping("/clients/{codeClient}")
    public ResponseEntity<String> getClientDetail(@PathVariable String codeClient) {
        log.info("GET /api/caissier/clients/{}", codeClient);
        return ResponseEntity.ok("Client detail - À implémenter");
    }

    /**
     * COMMENT CLEF: Voir collecteurs de l'agence (view only)
     */
    @GetMapping("/collecteurs")
    public ResponseEntity<List<EmployeDto>> getCollecteurs(@RequestParam Integer idAgence) {
        log.info("GET /api/caissier/collecteurs - Agence: {}", idAgence);
        try {
            List<EmployeDto> collecteurs = caissierService.obtenirCollecteursAgence(idAgence);
            return ResponseEntity.ok(collecteurs);
        } catch (RuntimeException e) {
            log.error("Erreur récupération collecteurs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Voir détails d'un collecteur
     */
    @GetMapping("/collecteurs/{idCollecteur}")
    public ResponseEntity<String> getCollecteurDetail(@PathVariable Integer idCollecteur) {
        log.info("GET /api/caissier/collecteurs/{}", idCollecteur);
        return ResponseEntity.ok("Collecteur detail - À implémenter");
    }

    /**
     * COMMENT CLEF: Synchroniser transactions offline
     * À implémenter avec TransactionOfflineService
     */
    @PostMapping("/transactions/sync")
    public ResponseEntity<String> synchroniserOffline(@RequestParam Integer idCaissier) {
        log.info("POST /api/caissier/transactions/sync - Caissier: {}", idCaissier);
        return ResponseEntity.ok("Synchronisation en cours...");
    }

    /**
     * COMMENT CLEF: Générer reçu PDF pour transaction
     */
    @PostMapping("/receipts/{idTransaction}/generate")
    public ResponseEntity<String> genererRecuPDF(@PathVariable String idTransaction) {
        log.info("POST /api/caissier/receipts/{}/generate", idTransaction);
        return ResponseEntity.ok("Génération reçu PDF - À implémenter");
    }

    /**
     * COMMENT CLEF: Reporting financier caissier
     */
    @GetMapping("/reporting/financial")
    public ResponseEntity<String> getReportingFinancier(@RequestParam Integer idAgence) {
        log.info("GET /api/caissier/reporting/financial - Agence: {}", idAgence);
        return ResponseEntity.ok("Reporting financier - À implémenter");
    }

    /**
     * COMMENT CLEF: Export reporting (Excel, CSV)
     */
    @GetMapping("/reporting/export")
    public ResponseEntity<String> exportReporting(
            @RequestParam Integer idAgence,
            @RequestParam(defaultValue = "excel") String format) {
        log.info("GET /api/caissier/reporting/export - Format: {}", format);
        return ResponseEntity.ok("Export " + format + " - À implémenter");
    }
}
