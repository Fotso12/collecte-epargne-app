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
@PreAuthorize("hasRole('SUPERVISOR') or hasRole('SUPERVISEUR') or hasRole('ADMIN') or hasRole('SUP')")
public class SuperviseurController {
    
    private static final Logger log = LoggerFactory.getLogger(SuperviseurController.class);
    
    private final SuperviseurService superviseurService;
    private final com.collecte_epargne.collecte_epargne.services.implementations.SuperviseurKpiService kpiService;

    // Repositories needed to find the connected employee
    private final com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository utilisateurRepository;
    private final com.collecte_epargne.collecte_epargne.repositories.EmployeRepository employeRepository;
    private final com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository agenceZoneRepository;

    public SuperviseurController(SuperviseurService superviseurService,
                                 com.collecte_epargne.collecte_epargne.services.implementations.SuperviseurKpiService kpiService,
                                 com.collecte_epargne.collecte_epargne.repositories.UtilisateurRepository utilisateurRepository,
                                 com.collecte_epargne.collecte_epargne.repositories.EmployeRepository employeRepository,
                                 com.collecte_epargne.collecte_epargne.repositories.AgenceZoneRepository agenceZoneRepository) {
        this.superviseurService = superviseurService;
        this.kpiService = kpiService;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
        this.agenceZoneRepository = agenceZoneRepository;
    }

    private Integer getAuthenticatedSuperviseurId(java.security.Principal principal) {
        if (principal == null) throw new RuntimeException("Utilisateur non authentifié");
        String email = principal.getName();
        com.collecte_epargne.collecte_epargne.entities.Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));

        // Allow ADMIN to bypass employee check
        if (utilisateur.getRole().getCode().equalsIgnoreCase("admin")) {
            // Pour l'admin, on essaie de trouver un superviseur par défaut ou on retourne null
            return employeRepository.findByTypeEmploye(com.collecte_epargne.collecte_epargne.utils.TypeEmploye.SUPERVISEUR)
                    .stream().findFirst()
                    .map(com.collecte_epargne.collecte_epargne.entities.Employe::getIdEmploye)
                    .orElse(null); // Return null if no supervisor found
        }
            
        com.collecte_epargne.collecte_epargne.entities.Employe employe = employeRepository.findByUtilisateurLogin(utilisateur.getLogin())
            .orElseThrow(() -> new RuntimeException("Cet utilisateur n'est pas lié à un employé (Superviseur)"));
            
        return employe.getIdEmploye();
    }

    /**
     * COMMENT CLEF: Dashboard superviseur - KPIs agence
     */
    @GetMapping("/dashboard")
    public ResponseEntity<SuperviseurDashboardDTO> getDashboard(java.security.Principal principal) {
        log.info("GET /api/superviseur/dashboard - User: {}", principal.getName());
        try {
            Integer idSuperviseur = getAuthenticatedSuperviseurId(principal);
            
            // Si Admin et pas de superviseur trouvé, retourner un dashboard vide
            if (idSuperviseur == null) {
                SuperviseurDashboardDTO emptyDashboard = new SuperviseurDashboardDTO();
                emptyDashboard.setNomSuperviseur("ADMIN (Vue Globale)");
                emptyDashboard.setAgenceNom("Toutes Agences");
                return ResponseEntity.ok(emptyDashboard);
            }
            
            SuperviseurDashboardDTO dashboard = superviseurService.obtenirDashboard(idSuperviseur);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Erreur dashboard superviseur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * COMMENT CLEF: KPIs complets pour le superviseur
     */
    @GetMapping("/kpis")
    public ResponseEntity<SuperviseurKpiDto> getKpis(java.security.Principal principal) {
        log.info("GET /api/superviseur/kpis - User: {}", principal.getName());
        try {
            Integer idSup = getAuthenticatedSuperviseurId(principal);
            com.collecte_epargne.collecte_epargne.entities.AgenceZone agence = null;

            if (idSup != null) {
                agence = employeRepository.findById(idSup)
                    .map(com.collecte_epargne.collecte_epargne.entities.Employe::getAgenceZone)
                    .orElse(null);
            }

            // Fallback pour Admin si aucune agence trouvée via l'employé
            if (agence == null) {
                agence = agenceZoneRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Aucune agence trouvée dans le système"));
            }

            SuperviseurKpiDto kpis = kpiService.getKpis(agence);
            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            log.error("Erreur récupération KPIs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * COMMENT CLEF: Récupère comptes EN_ATTENTE d'approbation
     */
    /**
     * COMMENT CLEF: Récupère comptes EN_ATTENTE d'approbation
     */
    @GetMapping("/comptes/pending")
    public ResponseEntity<List<CompteDto>> getComptesEnAttente(java.security.Principal principal) {
        try {
            Integer idSuperviseur = getAuthenticatedSuperviseurId(principal);
            log.info("GET /api/superviseur/comptes/pending - Superviseur: {}", idSuperviseur);
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
            java.security.Principal principal,
            @RequestBody Map<String, Object> request) {
        
        try {
            Integer idSuperviseur = getAuthenticatedSuperviseurId(principal);
            log.info("POST /api/superviseur/comptes/{}/approve - Superviseur: {}", idCompte, idSuperviseur);
        
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
            java.security.Principal principal,
            @RequestBody Map<String, Object> request) {
        
        try {
            Integer idSuperviseur = getAuthenticatedSuperviseurId(principal);
            log.info("POST /api/superviseur/comptes/{}/reject - Superviseur: {}", idCompte, idSuperviseur);
        
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
