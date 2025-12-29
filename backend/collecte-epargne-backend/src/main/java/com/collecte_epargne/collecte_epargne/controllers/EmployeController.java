package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.services.implementations.EmployeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/employes")
public class EmployeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeController.class);

    private final EmployeService employeService;

    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    // ----------------------------------------------------------------------
    // CRUD Générique (Base pour tout Employe)
    // ----------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<?> save(@RequestBody EmployeDto employeDto) {
        logger.info("Requête POST reçue pour créer un employé - Type: {}, Matricule: {}", 
                employeDto.getTypeEmploye(), employeDto.getMatricule());
        try {
            EmployeDto saved = employeService.save(employeDto);
            logger.info("Employé créé avec succès via API - Matricule: {}", saved.getMatricule());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'employé - Type: {}, Erreur: {}", 
                    employeDto.getTypeEmploye(), e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<EmployeDto>> getAll() {
        logger.debug("Requête GET reçue pour récupérer tous les employés");
        try {
            List<EmployeDto> employes = employeService.getAll();
            logger.info("Récupération réussie de {} employé(s) via API", employes.size());
            return new ResponseEntity<>(employes, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des employés", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des employés", e);
        }
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<?> getById(@PathVariable String matricule) {
        logger.debug("Requête GET reçue pour récupérer l'employé - Matricule: {}", matricule);
        try {
            EmployeDto employe = employeService.getById(matricule);
            logger.info("Employé récupéré avec succès via API - Matricule: {}", matricule);
            return new ResponseEntity<>(employe, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Employé non trouvé via API - Matricule: {}, Erreur: {}", matricule, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{matricule}")
    public ResponseEntity<?> update(@PathVariable String matricule, @RequestBody EmployeDto employeDto) {
        logger.info("Requête PUT reçue pour mettre à jour l'employé - Matricule: {}, Type: {}", 
                matricule, employeDto.getTypeEmploye());
        try {
            EmployeDto updated = employeService.update(matricule, employeDto);
            logger.info("Employé mis à jour avec succès via API - Matricule: {}", matricule);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'employé - Matricule: {}, Erreur: {}", 
                    matricule, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matricule}")
    public ResponseEntity<?> delete(@PathVariable String matricule) {
        logger.info("Requête DELETE reçue pour supprimer l'employé - Matricule: {}", matricule);
        try {
            employeService.delete(matricule);
            logger.info("Employé supprimé avec succès via API - Matricule: {}", matricule);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'employé - Matricule: {}, Erreur: {}", 
                    matricule, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ----------------------------------------------------------------------
    // Listage Spécialisé (CRUD par Rôle)
    // ----------------------------------------------------------------------

    @GetMapping("/superviseurs")
    public ResponseEntity<List<EmployeDto>> getSuperviseurs() {
        logger.debug("Requête GET reçue pour récupérer tous les superviseurs");
        List<EmployeDto> superviseurs = employeService.getSuperviseurs();
        logger.info("Récupération réussie de {} superviseur(s) via API", superviseurs.size());
        return new ResponseEntity<>(superviseurs, HttpStatus.OK);
    }

    @GetMapping("/caissiers")
    public ResponseEntity<List<EmployeDto>> getCaissiers() {
        logger.debug("Requête GET reçue pour récupérer tous les caissiers");
        List<EmployeDto> caissiers = employeService.getCaissiers();
        logger.info("Récupération réussie de {} caissier(s) via API", caissiers.size());
        return new ResponseEntity<>(caissiers, HttpStatus.OK);
    }

    @GetMapping("/collecteurs")
    public ResponseEntity<List<EmployeDto>> getCollecteurs() {
        logger.debug("Requête GET reçue pour récupérer tous les collecteurs");
        List<EmployeDto> collecteurs = employeService.getCollecteurs();
        logger.info("Récupération réussie de {} collecteur(s) via API", collecteurs.size());
        return new ResponseEntity<>(collecteurs, HttpStatus.OK);
    }

    // ----------------------------------------------------------------------
    // Relations et Métriques Collecteur
    // ----------------------------------------------------------------------

    @GetMapping("/{idSuperviseur}/collecteurs")
    public ResponseEntity<?> getCollecteursBySuperviseur(@PathVariable String idSuperviseur) {
        logger.debug("Requête GET reçue pour récupérer les collecteurs du superviseur - ID: {}", idSuperviseur);
        try {
            List<EmployeDto> collecteurs = employeService.getCollecteursBySuperviseur(idSuperviseur);
            logger.info("Récupération réussie de {} collecteur(s) pour le superviseur ID: {} via API", 
                    collecteurs.size(), idSuperviseur);
            return new ResponseEntity<>(collecteurs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des collecteurs du superviseur - ID: {}, Erreur: {}", 
                    idSuperviseur, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/collecteurs/{idCollecteur}/clients")
    public ResponseEntity<List<ClientDto>> getClientsByCollecteur(@PathVariable String idCollecteur) {
        logger.debug("Requête GET reçue pour récupérer les clients du collecteur - ID: {}", idCollecteur);
        try {
            List<ClientDto> clients = employeService.getClientsByCollecteur(idCollecteur);
            logger.info("Récupération réussie de {} client(s) pour le collecteur ID: {} via API", 
                    clients.size(), idCollecteur);
            return new ResponseEntity<>(clients, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des clients du collecteur - ID: {}, Erreur: {}", 
                    idCollecteur, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // --- Fonctions de Tri ---

    /** Liste les collecteurs du plus au moins performant (par nombre de clients) */
    @GetMapping("/collecteurs/performance/client-count")
    public ResponseEntity<List<EmployeDto>> getCollecteursByClientCount() {
        logger.debug("Requête GET reçue pour récupérer les collecteurs triés par nombre de clients");
        List<EmployeDto> collecteurs = employeService.getCollecteursOrderedByClientCount();
        logger.info("Récupération réussie de {} collecteur(s) triés par nombre de clients via API", collecteurs.size());
        return new ResponseEntity<>(collecteurs, HttpStatus.OK);
    }

    /** Liste les collecteurs du plus au moins performant (par score d'épargne total) */
    @GetMapping("/collecteurs/performance/score-total")
    public ResponseEntity<List<EmployeDto>> getCollecteursByTotalClientScore() {
        logger.debug("Requête GET reçue pour récupérer les collecteurs triés par score total");
        List<EmployeDto> collecteurs = employeService.getCollecteursOrderedByTotalClientScore();
        logger.info("Récupération réussie de {} collecteur(s) triés par score total via API", collecteurs.size());
        return new ResponseEntity<>(collecteurs, HttpStatus.OK);
    }


}