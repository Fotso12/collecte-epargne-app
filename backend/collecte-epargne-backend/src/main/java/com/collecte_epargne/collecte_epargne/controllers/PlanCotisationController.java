package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;
import com.collecte_epargne.collecte_epargne.services.implementations.PlanCotisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/plan-cotisations")
public class PlanCotisationController {

    private static final Logger logger = LoggerFactory.getLogger(PlanCotisationController.class);

    private final PlanCotisationService planCotisationService;

    public PlanCotisationController(PlanCotisationService planCotisationService) {
        this.planCotisationService = planCotisationService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PlanCotisationDto dto) {
        logger.info("Requête POST reçue pour créer un plan de cotisation - Nom: {}", dto.getNom());
        try {
            PlanCotisationDto saved = planCotisationService.save(dto);
            logger.info("Plan de cotisation créé avec succès via API - ID: {}", saved.getIdPlan());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du plan de cotisation - Nom: {}, Erreur: {}", 
                    dto.getNom(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PlanCotisationDto>> getAll() {
        logger.debug("Requête GET reçue pour récupérer tous les plans de cotisation");
        try {
            List<PlanCotisationDto> plans = planCotisationService.getAll();
            logger.info("Récupération réussie de {} plan(s) de cotisation via API", plans.size());
            return new ResponseEntity<>(plans, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des plans de cotisation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des plans de cotisation", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        logger.debug("Requête GET reçue pour récupérer le plan de cotisation - ID: {}", id);
        try {
            PlanCotisationDto plan = planCotisationService.getById(id);
            logger.info("Plan de cotisation récupéré avec succès via API - ID: {}", id);
            return new ResponseEntity<>(plan, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Plan de cotisation non trouvé via API - ID: {}, Erreur: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody PlanCotisationDto dto) {
        logger.info("Requête PUT reçue pour mettre à jour le plan de cotisation - ID: {}, Nom: {}", id, dto.getNom());
        try {
            PlanCotisationDto updated = planCotisationService.update(id, dto);
            logger.info("Plan de cotisation mis à jour avec succès via API - ID: {}", id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du plan de cotisation - ID: {}, Erreur: {}", 
                    id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        logger.info("Requête DELETE reçue pour supprimer le plan de cotisation - ID: {}", id);
        try {
            planCotisationService.delete(id);
            logger.info("Plan de cotisation supprimé avec succès via API - ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du plan de cotisation - ID: {}, Erreur: {}", 
                    id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}


