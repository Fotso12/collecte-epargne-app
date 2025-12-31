package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("api/comptes")
public class CompteController {

    private static final Logger log = LoggerFactory.getLogger(CompteController.class);

    private final CompteService compteService;

    public CompteController(CompteService compteService) {
        this.compteService = compteService;
    }

    @PostMapping
    public ResponseEntity<CompteDto> save(@RequestBody CompteDto compteDto) {
        log.info("POST /api/comptes - Création compte");

        try {
            return new ResponseEntity<>(compteService.save(compteDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur création compte", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CompteDto>> getAll() {
        log.info("GET /api/comptes - Liste comptes");

        try {
            return new ResponseEntity<>(compteService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur récupération comptes", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la récupération des comptes",
                    e
            );
        }
    }

    @GetMapping("/{idCompte}")
    public ResponseEntity<CompteDto> getById(@PathVariable String idCompte) {
        log.info("GET /api/comptes/{}", idCompte);

        try {
            return new ResponseEntity<>(compteService.getById(idCompte), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Compte non trouvé id={}", idCompte, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/numero/{numCompte}")
    public ResponseEntity<CompteDto> getByNumCompte(@PathVariable String numCompte) {
        log.info("GET /api/comptes/numero/{}", numCompte);

        try {
            return new ResponseEntity<>(compteService.getByNumCompte(numCompte), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Compte non trouvé numCompte={}", numCompte, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/client/{codeClient}")
    public ResponseEntity<List<CompteDto>> getByClient(@PathVariable String codeClient) {
        log.info("GET /api/comptes/client/{}", codeClient);

        try {
            return new ResponseEntity<>(compteService.getByClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Comptes non trouvés pour client={}", codeClient, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{idCompte}")
    public ResponseEntity<CompteDto> update(@PathVariable String idCompte,
                                            @RequestBody CompteDto compteDto) {
        log.info("PUT /api/comptes/{}", idCompte);

        try {
            return new ResponseEntity<>(compteService.update(idCompte, compteDto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur mise à jour compte id={}", idCompte, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{idCompte}")
    public ResponseEntity<Void> delete(@PathVariable String idCompte) {
        log.info("DELETE /api/comptes/{}", idCompte);

        try {
            compteService.delete(idCompte);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Erreur suppression compte id={}", idCompte, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
