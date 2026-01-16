package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.*;
import com.collecte_epargne.collecte_epargne.services.implementations.SuperviseurService;
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
 * COMMENT CLEF: Controller Superviseur - Endpoints approbation comptes + KPIs + Collection
 * Sécurité: Filtre par agenceZone du superviseur authentifié
 */
@RestController
@RequestMapping("/api/superviseur")
@PreAuthorize("hasRole('SUPERVISEUR')")
public class SuperviseurController {
    
    private static final Logger log = LoggerFactory.getLogger(SuperviseurController.class);
    
    private final SuperviseurService superviseurService;

    public SuperviseurController(SuperviseurService superviseurService) {
        this.superviseurService = superviseurService;
    }

    /**
     * COMMENT CLEF: Dashboard superviseur - KPIs agence
     */
    @GetMapping("/dashboard")
    public ResponseEntity<SuperviseurDashboardDTO> getDashboard(@RequestParam Integer idSuperviseur) {
        log.info("GET /api/superviseur/dashboard - Superviseur: {}", idSuperviseur);
        try {
            SuperviseurDashboardDTO dashboard = superviseurService.obtenirDashboard(idSuperviseur);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Erreur dashboard superviseur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Récupère comptes EN_ATTENTE d'approbation
     */
    @GetMapping("/comptes/pending")
    public ResponseEntity<List<CompteDto>> getComptesEnAttente(@RequestParam Integer idSuperviseur) {
        log.info("GET /api/superviseur/comptes/pending - Superviseur: {}", idSuperviseur);
        try {
            List<CompteDto> comptes = superviseurService.obtenirComptesEnAttenteApprobation(idSuperviseur);
            return ResponseEntity.ok(comptes);
        } catch (RuntimeException e) {
            log.error("Erreur récupération comptes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Approuve un compte - Modal confirmation
     * Request body: { "confirmé": true }
     */
    @PostMapping("/comptes/{idCompte}/approve")
    public ResponseEntity<CompteDto> approuverCompte(
            @PathVariable String idCompte,
            @RequestParam Integer idSuperviseur,
            @RequestBody Map<String, Object> request) {
        
        log.info("POST /api/superviseur/comptes/{}/approve - Superviseur: {}", idCompte, idSuperviseur);
        
        try {
            // COMMENT CLEF: Vérifier confirmation
            Boolean confirme = (Boolean) request.get("confirmé");
            if (confirme == null || !confirme) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            CompteDto result = superviseurService.approuverCompte(idCompte, idSuperviseur);
            log.info("Compte {} approuvé avec succès", idCompte);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Erreur approbation compte: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Rejette un compte - Modal motif
     * Request body: { "motifRejet": "Données manquantes", "confirmé": true }
     */
    @PostMapping("/comptes/{idCompte}/reject")
    public ResponseEntity<CompteDto> rejeterCompte(
            @PathVariable String idCompte,
            @RequestParam Integer idSuperviseur,
            @RequestBody Map<String, Object> request) {
        
        log.info("POST /api/superviseur/comptes/{}/reject - Superviseur: {}", idCompte, idSuperviseur);
        
        try {
            String motifRejet = (String) request.get("motifRejet");
            Boolean confirme = (Boolean) request.get("confirmé");
            
            if (motifRejet == null || motifRejet.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (confirme == null || !confirme) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            CompteDto result = superviseurService.rejeterCompte(idCompte, motifRejet, idSuperviseur);
            log.info("Compte {} rejeté - Motif: {}", idCompte, motifRejet);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Erreur rejet compte: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Meilleur collecteur par montant collecté
     */
    @GetMapping("/kpi/best-collector")
    public ResponseEntity<CollecteurKPIDTO> getMeilleurCollecteur(@RequestParam Integer idAgence) {
        log.info("GET /api/superviseur/kpi/best-collector - Agence: {}", idAgence);
        // À implémenter
        return ResponseEntity.ok(new CollecteurKPIDTO());
    }

    /**
     * COMMENT CLEF: Historique collection par période
     * Paramètre: period = DAILY, WEEKLY, MONTHLY, SEMI_ANNUAL
     */
    @GetMapping("/kpi/collection-history")
    public ResponseEntity<List<CollectionKPIDTO>> getHistoriqueCollection(
            @RequestParam Integer idAgence,
            @RequestParam(defaultValue = "DAILY") String period) {
        
        log.info("GET /api/superviseur/kpi/collection-history - Agence: {}, Période: {}", idAgence, period);
        try {
            List<CollectionKPIDTO> historique = superviseurService.obtenirHistoriqueCollection(idAgence, period);
            return ResponseEntity.ok(historique);
        } catch (RuntimeException e) {
            log.error("Erreur historique collection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: Voir clients assignés aux collecteurs de l'agence (view only)
     */
    @GetMapping("/clients")
    public ResponseEntity<List<ClientDto>> getClients(@RequestParam Integer idAgence) {
        log.info("GET /api/superviseur/clients - Agence: {}", idAgence);
        try {
            List<ClientDto> clients = superviseurService.obtenirClientsAgence(idAgence);
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
        log.info("GET /api/superviseur/clients/{}", codeClient);
        return ResponseEntity.ok("Client detail - À implémenter");
    }

    /**
     * COMMENT CLEF: Voir collecteurs de l'agence (view only)
     */
    @GetMapping("/collecteurs")
    public ResponseEntity<List<EmployeDto>> getCollecteurs(@RequestParam Integer idAgence) {
        log.info("GET /api/superviseur/collecteurs - Agence: {}", idAgence);
        try {
            List<EmployeDto> collecteurs = superviseurService.obtenirCollecteursAgence(idAgence);
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
        log.info("GET /api/superviseur/collecteurs/{}", idCollecteur);
        return ResponseEntity.ok("Collecteur detail - À implémenter");
    }

    /**
     * COMMENT CLEF: Reporting financier superviseur
     */
    @GetMapping("/reporting/financial")
    public ResponseEntity<String> getReportingFinancier(@RequestParam Integer idAgence) {
        log.info("GET /api/superviseur/reporting/financial - Agence: {}", idAgence);
        return ResponseEntity.ok("Reporting financier - À implémenter");
    }

    /**
     * COMMENT CLEF: Générer reçus batch
     */
    @PostMapping("/receipts/generate")
    public ResponseEntity<String> genererRecusBatch(@RequestBody Map<String, Object> request) {
        log.info("POST /api/superviseur/receipts/generate");
        return ResponseEntity.ok("Génération reçus batch - À implémenter");
    }
}
