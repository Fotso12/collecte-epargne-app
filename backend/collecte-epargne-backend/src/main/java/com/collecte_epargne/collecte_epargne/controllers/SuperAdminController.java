package com.collecte_epargne.collecte_epargne.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * COMMENT CLEF: Controller SuperAdmin - Vue globale (test@example.com)
 * Sécurité: ROLE_ADMIN seulement - Peut voir TOUTES les agences/données
 */
@RestController
@RequestMapping("/api/superadmin")
@PreAuthorize("hasRole('ADMIN')")
public class SuperAdminController {
    
    private static final Logger log = LoggerFactory.getLogger(SuperAdminController.class);

    /**
     * COMMENT CLEF: Dashboard global - Stats toutes agences
     */
    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        log.info("GET /api/superadmin/dashboard");
        return ResponseEntity.ok("Dashboard global - À implémenter");
    }

    /**
     * COMMENT CLEF: Liste toutes les agences avec stats
     */
    @GetMapping("/companies")
    public ResponseEntity<String> getAllCompanies() {
        log.info("GET /api/superadmin/companies");
        return ResponseEntity.ok("Liste agences - À implémenter");
    }

    /**
     * COMMENT CLEF: Détails d'une agence spécifique
     */
    @GetMapping("/companies/{idAgence}/details")
    public ResponseEntity<String> getCompanyDetails(@PathVariable Integer idAgence) {
        log.info("GET /api/superadmin/companies/{}/details", idAgence);
        return ResponseEntity.ok("Détails agence - À implémenter");
    }

    /**
     * COMMENT CLEF: Liste tous les utilisateurs (globalement)
     */
    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers() {
        log.info("GET /api/superadmin/users");
        return ResponseEntity.ok("Liste utilisateurs - À implémenter");
    }

    /**
     * COMMENT CLEF: Liste tous les employés (globalement)
     */
    @GetMapping("/employees")
    public ResponseEntity<String> getAllEmployees() {
        log.info("GET /api/superadmin/employees");
        return ResponseEntity.ok("Liste employés - À implémenter");
    }

    /**
     * COMMENT CLEF: Déverrouille un compte utilisateur
     */
    @PostMapping("/manage/unlock-account/{idUser}")
    public ResponseEntity<Map<String, String>> unlockAccount(@PathVariable Integer idUser) {
        log.info("POST /api/superadmin/manage/unlock-account/{}", idUser);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Compte déverrouillé - À implémenter");
        return ResponseEntity.ok(response);
    }

    /**
     * COMMENT CLEF: Reporting global
     */
    @GetMapping("/reporting/global")
    public ResponseEntity<String> getReportingGlobal() {
        log.info("GET /api/superadmin/reporting/global");
        return ResponseEntity.ok("Reporting global - À implémenter");
    }
}
