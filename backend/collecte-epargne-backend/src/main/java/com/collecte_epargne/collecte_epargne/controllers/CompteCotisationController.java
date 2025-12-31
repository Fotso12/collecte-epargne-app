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

    private static final Logger log = LoggerFactory.getLogger(CompteCotisationController.class);

    private final CompteCotisationService compteCotisationService;

    public CompteCotisationController(CompteCotisationService compteCotisationService) {
        this.compteCotisationService = compteCotisationService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CompteCotisationDto dto) {
        log.info("Requête POST : création d'un compte cotisation");
        try {
            CompteCotisationDto result = compteCotisationService.save(dto);
            log.info("Compte cotisation créé avec succès, id={}", result.getId());
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de la création du compte cotisation : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CompteCotisationDto>> getAll() {
        log.info("Requête GET : récupération de tous les comptes cotisation");
        try {
            return new ResponseEntity<>(compteCotisationService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des comptes cotisation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération des comptes cotisation", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        log.info("Requête GET : compte cotisation id={}", id);
        try {
            return new ResponseEntity<>(compteCotisationService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Compte cotisation non trouvé : {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/compte/{idCompte}")
    public ResponseEntity<?> getByCompte(@PathVariable String idCompte) {
        log.info("Requête GET : comptes cotisation du compte {}", idCompte);
        try {
            return new ResponseEntity<>(compteCotisationService.getByCompte(idCompte), HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Aucun compte cotisation pour le compte {}", idCompte);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<?> getByPlan(@PathVariable String idPlan) {
        log.info("Requête GET : comptes cotisation du plan {}", idPlan);
        try {
            return new ResponseEntity<>(compteCotisationService.getByPlanCotisation(idPlan), HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Aucun compte cotisation pour le plan {}", idPlan);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CompteCotisationDto dto) {
        log.info("Requête PUT : mise à jour du compte cotisation {}", id);
        try {
            return new ResponseEntity<>(compteCotisationService.update(id, dto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du compte cotisation {}", id, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        log.info("Requête DELETE : suppression du compte cotisation {}", id);
        try {
            compteCotisationService.delete(id);
            log.info("Compte cotisation supprimé avec succès : {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.warn("Tentative de suppression d'un compte cotisation inexistant : {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
