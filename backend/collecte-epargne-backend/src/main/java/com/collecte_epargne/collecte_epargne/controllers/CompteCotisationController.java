package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteCotisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/compte-cotisations")
public class CompteCotisationController {

    private static final Logger logger = LoggerFactory.getLogger(CompteCotisationController.class);

    private final CompteCotisationService compteCotisationService;

    public CompteCotisationController(CompteCotisationService compteCotisationService) {
        this.compteCotisationService = compteCotisationService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CompteCotisationDto dto) {
        logger.info("Requête POST reçue pour créer un compte de cotisation - Compte: {}, Plan: {}", 
                dto.getIdCompte(), dto.getIdPlanCotisation());
        try {
            CompteCotisationDto saved = compteCotisationService.save(dto);
            logger.info("Compte de cotisation créé avec succès via API - ID: {}", saved.getId());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du compte de cotisation - Compte: {}, Plan: {}, Erreur: {}", 
                    dto.getIdCompte(), dto.getIdPlanCotisation(), e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CompteCotisationDto>> getAll() {
        logger.debug("Requête GET reçue pour récupérer tous les comptes de cotisation");
        try {
            List<CompteCotisationDto> comptes = compteCotisationService.getAll();
            logger.info("Récupération réussie de {} compte(s) de cotisation via API", comptes.size());
            return new ResponseEntity<>(comptes, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des comptes de cotisation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des comptes cotisation", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        logger.debug("Requête GET reçue pour récupérer le compte de cotisation - ID: {}", id);
        try {
            CompteCotisationDto compte = compteCotisationService.getById(id);
            logger.info("Compte de cotisation récupéré avec succès via API - ID: {}", id);
            return new ResponseEntity<>(compte, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Compte de cotisation non trouvé via API - ID: {}, Erreur: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/compte/{idCompte}")
    public ResponseEntity<?> getByCompte(@PathVariable String idCompte) {
        logger.debug("Requête GET reçue pour récupérer les comptes de cotisation du compte - ID: {}", idCompte);
        try {
            var comptes = compteCotisationService.getByCompte(idCompte);
            logger.info("Récupération réussie de {} compte(s) de cotisation pour le compte ID: {} via API", 
                    comptes.size(), idCompte);
            return new ResponseEntity<>(comptes, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Erreur lors de la récupération des comptes de cotisation du compte - ID: {}, Erreur: {}", 
                    idCompte, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<?> getByPlan(@PathVariable String idPlan) {
        logger.debug("Requête GET reçue pour récupérer les comptes de cotisation du plan - ID: {}", idPlan);
        try {
            var comptes = compteCotisationService.getByPlanCotisation(idPlan);
            logger.info("Récupération réussie de {} compte(s) de cotisation pour le plan ID: {} via API", 
                    comptes.size(), idPlan);
            return new ResponseEntity<>(comptes, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Erreur lors de la récupération des comptes de cotisation du plan - ID: {}, Erreur: {}", 
                    idPlan, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CompteCotisationDto dto) {
        logger.info("Requête PUT reçue pour mettre à jour le compte de cotisation - ID: {}, Statut: {}", 
                id, dto.getStatut());
        try {
            CompteCotisationDto updated = compteCotisationService.update(id, dto);
            logger.info("Compte de cotisation mis à jour avec succès via API - ID: {}", id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du compte de cotisation - ID: {}, Erreur: {}", 
                    id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        logger.info("Requête DELETE reçue pour supprimer le compte de cotisation - ID: {}", id);
        try {
            compteCotisationService.delete(id);
            logger.info("Compte de cotisation supprimé avec succès via API - ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du compte de cotisation - ID: {}, Erreur: {}", 
                    id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

