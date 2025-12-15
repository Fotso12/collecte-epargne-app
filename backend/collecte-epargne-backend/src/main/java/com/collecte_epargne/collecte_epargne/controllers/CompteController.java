package com.collecte_epargne.collecte_epargne.controllers;

import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.services.implementations.CompteService;
import com.collecte_epargne.collecte_epargne.services.interfaces.CompteInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/comptes")
public class CompteController {

    private final CompteService compteService;

    public CompteController(CompteService compteService) {
        this.compteService = compteService;
    }

    @PostMapping
    public ResponseEntity<CompteDto> save(@RequestBody CompteDto compteDto) {
        try {
            return new ResponseEntity<>(compteService.save(compteDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<List<CompteDto>> getAll() {
        try {
            return new ResponseEntity<>(compteService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des comptes", e);
        }
    }

    @GetMapping("/{idCompte}")
    public ResponseEntity<CompteDto> getById(@PathVariable String idCompte) {
        try {
            return new ResponseEntity<>(compteService.getById(idCompte), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/numero/{numCompte}")
    public ResponseEntity<CompteDto> getByNumCompte(@PathVariable String numCompte) {
        try {
            return new ResponseEntity<>(compteService.getByNumCompte(numCompte), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/client/{codeClient}")
    public ResponseEntity<List<CompteDto>> getByClient(@PathVariable String codeClient) {
        try {
            return new ResponseEntity<>(compteService.getByClient(codeClient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{idCompte}")
    public ResponseEntity<CompteDto> update(@PathVariable String idCompte, @RequestBody CompteDto compteDto) {
        try {
            return new ResponseEntity<>(compteService.update(idCompte, compteDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{idCompte}")
    public ResponseEntity<Void> delete(@PathVariable String idCompte) {
        try {
            compteService.delete(idCompte);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
