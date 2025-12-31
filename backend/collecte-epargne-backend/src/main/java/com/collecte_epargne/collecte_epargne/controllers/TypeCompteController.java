package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.TypeCompteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/type-comptes")
public class TypeCompteController {

    private static final Logger logger = LoggerFactory.getLogger(TypeCompteController.class);

    private final TypeCompteService typeCompteService;

    public TypeCompteController(TypeCompteService typeCompteService) {
        this.typeCompteService = typeCompteService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TypeCompteDto typeCompteDto) {
        logger.info("Création de type de compte");
        try {
            return new ResponseEntity<>(typeCompteService.save(typeCompteDto), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de type de compte: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<TypeCompteDto>> getAll() {
        logger.info("Récupération de tous les types de compte");
        try {
            return new ResponseEntity<>(typeCompteService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les types de compte: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des types de compte", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        logger.info("Récupération de type de compte avec id: {}", id);
        try {
            return new ResponseEntity<>(typeCompteService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de type de compte avec id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        logger.info("Récupération de type de compte avec code: {}", code);
        try {
            return new ResponseEntity<>(typeCompteService.getByCode(code), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de type de compte avec code {}: {}", code, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody TypeCompteDto typeCompteDto) {
        logger.info("Mise à jour de type de compte avec id: {}", id);
        try {
            return new ResponseEntity<>(typeCompteService.update(id, typeCompteDto), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de type de compte avec id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        logger.info("Suppression de type de compte avec id: {}", id);
        try {
            typeCompteService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de type de compte avec id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
