package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteCotisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/compte-cotisations")
public class CompteCotisationController {

    private final CompteCotisationService compteCotisationService;

    public CompteCotisationController(CompteCotisationService compteCotisationService) {
        this.compteCotisationService = compteCotisationService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CompteCotisationDto dto) {
        try {
            return new ResponseEntity<>(compteCotisationService.save(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CompteCotisationDto>> getAll() {
        try {
            return new ResponseEntity<>(compteCotisationService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des comptes cotisation", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return new ResponseEntity<>(compteCotisationService.getById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/compte/{idCompte}")
    public ResponseEntity<?> getByCompte(@PathVariable String idCompte) {
        try {
            return new ResponseEntity<>(compteCotisationService.getByCompte(idCompte), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<?> getByPlan(@PathVariable String idPlan) {
        try {
            return new ResponseEntity<>(compteCotisationService.getByPlanCotisation(idPlan), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CompteCotisationDto dto) {
        try {
            return new ResponseEntity<>(compteCotisationService.update(id, dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            compteCotisationService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

